package net.neganote.gtutilities.integration.ae2.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.AEItemConfigWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.MEStockingBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAESlot;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.gtutilities.common.gui.widgets.SimpleScrollbarWidget;
import net.neganote.gtutilities.config.UtilConfig;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEEnlargedStockingInputBusPartMachine extends MEStockingBusPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEEnlargedStockingInputBusPartMachine.class, MEStockingBusPartMachine.MANAGED_FIELD_HOLDER);

    private static final int SLOTS_PER_ROW = 8;
    private static final int TOTAL_ROWS = Math.min(64, UtilConfig.INSTANCE.features.enlargedStockingSizeRows);
    private static final int TOTAL_SLOTS = SLOTS_PER_ROW * TOTAL_ROWS;

    @Persisted
    @DescSynced
    private boolean enlargedAutoPull = false;

    private Predicate<GenericStack> enlargedAutoPullTest = $ -> false;

    public MEEnlargedStockingInputBusPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        super.setAutoPull(false);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected NotifiableItemStackHandler createInventory(Object... args) {
        this.aeItemHandler = new ExportOnlyAEStockingItemList(this, TOTAL_SLOTS);
        return this.aeItemHandler;
    }

    @Override
    public boolean isAutoPull() {
        return enlargedAutoPull;
    }

    @Override
    public void setAutoPull(boolean autoPull) {
        this.enlargedAutoPull = autoPull;

        if (!isRemote()) {
            if (!this.enlargedAutoPull) {
                this.aeItemHandler.clearInventory(0);
            } else if (updateMEStatus()) {
                refreshListEnlarged();
                updateInventorySubscription();
            }
        }

        super.setAutoPull(false);
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player playerIn, InteractionHand hand, Direction gridSide,
                                                   BlockHitResult hitResult) {
        if (!isRemote()) {
            setAutoPull(!isAutoPull());
            if (isAutoPull()) {
                playerIn.sendSystemMessage(Component.translatable("gtceu.machine.me.stocking_auto_pull_enabled"));
            } else {
                playerIn.sendSystemMessage(Component.translatable("gtceu.machine.me.stocking_auto_pull_disabled"));
            }
        }
        return InteractionResult.sidedSuccess(isRemote());
    }

    @Override
    public void autoIO() {
        super.autoIO();

        if (getTicksPerCycle() == 0) {
            setTicksPerCycle(ConfigHolder.INSTANCE.compat.ae2.updateIntervals);
        }

        if (getOffsetTimer() % (long) getTicksPerCycle() == 0L) {
            if (!isRemote() && enlargedAutoPull) {
                if (updateMEStatus()) {
                    refreshListEnlarged();
                    super.syncME();
                    updateInventorySubscription();
                }
            }
        }
    }

    private void refreshListEnlarged() {
        IGrid grid = this.getMainNode().getGrid();
        if (grid == null) {
            aeItemHandler.clearInventory(0);
            return;
        }

        MEStorage networkStorage = grid.getStorageService().getInventory();
        var counter = networkStorage.getAvailableStacks();

        final int size = this.aeItemHandler.getSlots();
        final int min = this.getMinStackSize();

        PriorityQueue<Object2LongMap.Entry<AEKey>> topItems = new PriorityQueue<>(
                Comparator.comparingLong(Object2LongMap.Entry<AEKey>::getLongValue));

        for (Object2LongMap.Entry<AEKey> entry : counter) {
            long amount = entry.getLongValue();
            AEKey what = entry.getKey();

            if (amount <= 0) continue;
            if (!(what instanceof AEItemKey itemKey)) continue;

            long request = networkStorage.extract(what, amount, Actionable.SIMULATE, actionSource);
            if (request == 0) continue;

            if (enlargedAutoPullTest != null && !enlargedAutoPullTest.test(new GenericStack(itemKey, amount))) continue;

            if (amount >= min) {
                if (topItems.size() < size) {
                    topItems.offer(entry);
                } else if (amount > topItems.peek().getLongValue()) {
                    topItems.poll();
                    topItems.offer(entry);
                }
            }
        }

        int index;
        int itemAmount = topItems.size();
        for (index = 0; index < size; index++) {
            if (topItems.isEmpty()) break;

            Object2LongMap.Entry<AEKey> entry = topItems.poll();
            AEKey what = entry.getKey();
            long amount = entry.getLongValue();

            long request = networkStorage.extract(what, amount, Actionable.SIMULATE, actionSource);

            ExportOnlyAEItemSlot slot = this.aeItemHandler.getInventory()[itemAmount - index - 1];
            slot.setConfig(new GenericStack(what, 1));
            slot.setStock(new GenericStack(what, request));
        }

        aeItemHandler.clearInventory(index);
    }

    @Override
    public void setAutoPullTest(Predicate<GenericStack> autoPullTest) {
        this.enlargedAutoPullTest = autoPullTest;
        super.setAutoPullTest(autoPullTest);
    }

    @Override
    protected CompoundTag writeConfigToTag() {
        if (!enlargedAutoPull) {
            CompoundTag tag = super.writeConfigToTag();
            tag.putBoolean("AutoPull", false);
            return tag;
        }
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("AutoPull", true);
        tag.putByte("GhostCircuit",
                (byte) IntCircuitBehaviour.getCircuitConfiguration(circuitInventory.getStackInSlot(0)));
        return tag;
    }

    @Override
    protected void readConfigFromTag(CompoundTag tag) {
        if (tag.getBoolean("AutoPull")) {
            setAutoPull(true);
            circuitInventory.setStackInSlot(0, IntCircuitBehaviour.stack(tag.getByte("GhostCircuit")));
            return;
        }
        setAutoPull(false);
        super.readConfigFromTag(tag);
    }

    @Override
    public Widget createUIWidget() {
        int visibleRows = 4;
        int rowHeight = 40;
        int startX = 10;
        int startY = 20;
        int scrollbarX = 158;

        final List<AEItemConfigWidget> allRowWidgets = new ArrayList<>();

        Consumer<Integer> onScrollChanged = (currentScroll) -> {
            for (int i = 0; i < allRowWidgets.size(); i++) {
                AEItemConfigWidget row = allRowWidgets.get(i);
                int relativeIndex = i - currentScroll;
                if (relativeIndex >= 0 && relativeIndex < visibleRows) {
                    row.setVisible(true);
                    row.setSelfPosition(startX, startY + (relativeIndex * rowHeight));
                } else {
                    row.setVisible(false);
                }
            }
        };

        SimpleScrollbarWidget scrollbar = new SimpleScrollbarWidget(
                scrollbarX,
                startY,
                visibleRows * rowHeight - 4,
                onScrollChanged);

        scrollbar.setRange(0, TOTAL_ROWS - visibleRows, 1);

        WidgetGroup group = new WidgetGroup(new Position(0, 0), new Size(178, 200)) {

            @Override
            public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
                if (super.mouseWheelMove(mouseX, mouseY, wheelDelta)) {
                    return true;
                }
                if (isMouseOver(getPositionX(), getPositionY(), getSizeWidth(), getSizeHeight(), mouseX, mouseY)) {
                    return scrollbar.mouseWheelMove(mouseX, mouseY, wheelDelta);
                }
                return false;
            }
        };

        group.addWidget(new LabelWidget(5, 5, () -> this.isOnline ?
                "gtceu.gui.me_network.online" :
                "gtceu.gui.me_network.offline"));

        for (int i = 0; i < TOTAL_ROWS; i++) {
            final int currentRowIndex = i;
            final int startSlotIndex = currentRowIndex * SLOTS_PER_ROW;

            AtomicInteger localSlotCounter = new AtomicInteger(0);
            ExportOnlyAEItemList viewHandler = new ExportOnlyAEItemList(
                    this,
                    SLOTS_PER_ROW,
                    () -> {
                        int globalSlotIndex = startSlotIndex + localSlotCounter.getAndIncrement();
                        if (globalSlotIndex < this.aeItemHandler.getSlots()) {
                            return this.aeItemHandler.getInventory()[globalSlotIndex];
                        }
                        return null;
                    }) {

                @Override
                public boolean isStocking() {
                    return true;
                }

                @Override
                public boolean isAutoPull() {
                    return MEEnlargedStockingInputBusPartMachine.this.isAutoPull();
                }
            };

            var root = (ExportOnlyAEStockingItemList) MEEnlargedStockingInputBusPartMachine.this.aeItemHandler;
            Runnable rootCb = root::fireContentsChanged;

            for (int s = 0; s < SLOTS_PER_ROW; s++) {
                int global = startSlotIndex + s;
                if (global >= 0 && global < MEEnlargedStockingInputBusPartMachine.this.aeItemHandler.getSlots()) {
                    MEEnlargedStockingInputBusPartMachine.this.aeItemHandler.getInventory()[global]
                            .setOnContentsChanged(rootCb);
                }
            }

            AEItemConfigWidget rowWidget = new AEItemConfigWidget(startX, startY, viewHandler) {

                @Override
                public boolean hasStackInConfig(GenericStack stack) {
                    return MEEnlargedStockingInputBusPartMachine.this.aeItemHandler.hasStackInConfig(stack, true);
                }
            };

            rowWidget.setVisible(false);
            allRowWidgets.add(rowWidget);
            group.addWidget(rowWidget);
        }

        group.addWidget(scrollbar);
        onScrollChanged.accept(0);

        return group;
    }

    private class ExportOnlyAEStockingItemList extends ExportOnlyAEItemList {

        public ExportOnlyAEStockingItemList(MetaMachine holder, int slots) {
            super(holder, slots, ExportOnlyAEStockingItemSlot::new);
        }

        @Override
        public boolean isStocking() {
            return true;
        }

        @Override
        public boolean isAutoPull() {
            return MEEnlargedStockingInputBusPartMachine.this.isAutoPull();
        }

        public void fireContentsChanged() {
            super.onContentsChanged();
        }
    }

    private class ExportOnlyAEStockingItemSlot extends ExportOnlyAEItemSlot {

        public ExportOnlyAEStockingItemSlot() {
            super();
        }

        public ExportOnlyAEStockingItemSlot(@Nullable GenericStack config, @Nullable GenericStack stock) {
            super(config, stock);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0 && this.stock != null) {
                if (this.config != null) {
                    if (!isOnline()) return ItemStack.EMPTY;
                    MEStorage aeNetwork = getMainNode().getGrid().getStorageService().getInventory();

                    Actionable action = simulate ? Actionable.SIMULATE : Actionable.MODULATE;
                    var key = config.what();
                    long extracted = aeNetwork.extract(key, amount, action, actionSource);

                    if (extracted > 0) {
                        ItemStack resultStack = key instanceof AEItemKey itemKey ? itemKey.toStack((int) extracted) :
                                ItemStack.EMPTY;

                        if (!simulate) {
                            this.stock = ExportOnlyAESlot.copy(stock, stock.amount() - extracted);
                            if (this.stock.amount() == 0) {
                                this.stock = null;
                            }
                            if (this.onContentsChanged != null) {
                                this.onContentsChanged.run();
                            }
                        }
                        return resultStack;
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ExportOnlyAEStockingItemSlot copy() {
            return new ExportOnlyAEStockingItemSlot(
                    this.config == null ? null : copy(this.config),
                    this.stock == null ? null : copy(this.stock));
        }
    }
}
