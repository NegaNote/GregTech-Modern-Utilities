package net.neganote.gtutilities.integration.ae2.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.AEFluidConfigWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.MEStockingHatchPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEFluidList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEFluidSlot;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAESlot;
import com.gregtechceu.gtceu.integration.ae2.utils.AEUtil;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.neganote.gtutilities.common.gui.widgets.SimpleScrollbarWidget;
import net.neganote.gtutilities.config.UtilConfig;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEFluidKey;
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
public class MEEnlargedStockingInputHatchPartMachine extends MEStockingHatchPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEEnlargedStockingInputHatchPartMachine.class, MEStockingHatchPartMachine.MANAGED_FIELD_HOLDER);

    private static final int SLOTS_PER_ROW = 8;
    private static final int TOTAL_ROWS = Math.min(64, UtilConfig.INSTANCE.features.enlargedStockingSizeRows);
    private static final int TOTAL_SLOTS = SLOTS_PER_ROW * TOTAL_ROWS;

    @Persisted
    @DescSynced
    private boolean enlargedAutoPull = false;
    private Predicate<GenericStack> enlargedAutoPullTest = $ -> false;

    public MEEnlargedStockingInputHatchPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        super.setAutoPull(false);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected NotifiableFluidTank createTank(int initialCapacity, int slots, Object... args) {
        this.aeFluidHandler = new ExportOnlyAEStockingFluidList(this, TOTAL_SLOTS);
        return this.aeFluidHandler;
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
                this.aeFluidHandler.clearInventory(0);
            } else if (updateMEStatus()) {
                refreshListEnlarged();
                updateTankSubscription();
            }
        }

        super.setAutoPull(false);
    }

    @Override
    public void setAutoPullTest(Predicate<GenericStack> autoPullTest) {
        this.enlargedAutoPullTest = autoPullTest;
        super.setAutoPullTest(autoPullTest);
    }

    @Override
    public void autoIO() {
        super.autoIO();

        if (getTicksPerCycle() == 0) setTicksPerCycle(ConfigHolder.INSTANCE.compat.ae2.updateIntervals);

        if (getOffsetTimer() % (long) getTicksPerCycle() == 0L) {
            if (!isRemote() && enlargedAutoPull) {
                if (updateMEStatus()) {
                    refreshListEnlarged();
                    super.syncME();
                    updateTankSubscription();
                }
            }
        }
    }

    private void refreshListEnlarged() {
        IGrid grid = this.getMainNode().getGrid();
        if (grid == null) {
            aeFluidHandler.clearInventory(0);
            return;
        }

        MEStorage networkStorage = grid.getStorageService().getInventory();
        var counter = networkStorage.getAvailableStacks();

        final int size = this.aeFluidHandler.getTanks();
        final int min = this.getMinStackSize();

        PriorityQueue<Object2LongMap.Entry<AEKey>> topFluids = new PriorityQueue<>(
                Comparator.comparingLong(Object2LongMap.Entry<AEKey>::getLongValue));

        for (Object2LongMap.Entry<AEKey> entry : counter) {
            long amount = entry.getLongValue();
            AEKey what = entry.getKey();

            if (amount <= 0) continue;
            if (!(what instanceof AEFluidKey fluidKey)) continue;

            long request = networkStorage.extract(what, amount, Actionable.SIMULATE, actionSource);
            if (request == 0) continue;

            if (enlargedAutoPullTest != null && !enlargedAutoPullTest.test(new GenericStack(fluidKey, amount)))
                continue;

            if (amount >= min) {
                if (topFluids.size() < size) {
                    topFluids.offer(entry);
                } else if (amount > topFluids.peek().getLongValue()) {
                    topFluids.poll();
                    topFluids.offer(entry);
                }
            }
        }

        int index;
        int fluidAmount = topFluids.size();
        for (index = 0; index < size; index++) {
            if (topFluids.isEmpty()) break;

            Object2LongMap.Entry<AEKey> entry = topFluids.poll();
            AEKey what = entry.getKey();
            long amount = entry.getLongValue();

            long request = networkStorage.extract(what, amount, Actionable.SIMULATE, actionSource);

            var slot = this.aeFluidHandler.getInventory()[fluidAmount - index - 1];
            slot.setConfig(new GenericStack(what, 1));
            slot.setStock(new GenericStack(what, request));
        }

        aeFluidHandler.clearInventory(index);
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
    public Widget createUIWidget() {
        int visibleRows = 4;
        int rowHeight = 40;
        int startX = 10;
        int startY = 20;
        int scrollbarX = 158;

        final List<AEFluidConfigWidget> allRowWidgets = new ArrayList<>();

        Consumer<Integer> onScrollChanged = (currentScroll) -> {
            for (int i = 0; i < allRowWidgets.size(); i++) {
                AEFluidConfigWidget row = allRowWidgets.get(i);
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
            ExportOnlyAEFluidList viewHandler = new ExportOnlyAEFluidList(
                    this,
                    SLOTS_PER_ROW,
                    () -> {
                        int globalSlotIndex = startSlotIndex + localSlotCounter.getAndIncrement();
                        if (globalSlotIndex < this.aeFluidHandler.getTanks()) {
                            return this.aeFluidHandler.getInventory()[globalSlotIndex];
                        }
                        return null;
                    }) {

                @Override
                public boolean isStocking() {
                    return true;
                }

                @Override
                public boolean isAutoPull() {
                    return MEEnlargedStockingInputHatchPartMachine.this.isAutoPull();
                }
            };

            var root = (ExportOnlyAEStockingFluidList) MEEnlargedStockingInputHatchPartMachine.this.aeFluidHandler;
            Runnable rootCb = root::fireContentsChanged;

            for (int s = 0; s < SLOTS_PER_ROW; s++) {
                int global = startSlotIndex + s;
                if (global >= 0 && global < MEEnlargedStockingInputHatchPartMachine.this.aeFluidHandler.getTanks()) {
                    MEEnlargedStockingInputHatchPartMachine.this.aeFluidHandler.getInventory()[global]
                            .setOnContentsChanged(rootCb);
                }
            }

            AEFluidConfigWidget rowWidget = new AEFluidConfigWidget(startX, startY, viewHandler) {

                @Override
                public boolean hasStackInConfig(GenericStack stack) {
                    return MEEnlargedStockingInputHatchPartMachine.this.aeFluidHandler.hasStackInConfig(stack, true);
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

    private class ExportOnlyAEStockingFluidList extends ExportOnlyAEFluidList {

        public ExportOnlyAEStockingFluidList(MetaMachine holder, int slots) {
            super(holder, slots, ExportOnlyAEStockingFluidSlot::new);
        }

        @Override
        public boolean isStocking() {
            return true;
        }

        @Override
        public boolean isAutoPull() {
            return MEEnlargedStockingInputHatchPartMachine.this.isAutoPull();
        }

        @Override
        public boolean hasStackInConfig(GenericStack stack, boolean checkExternal) {
            boolean inThisBus = hasStackInternal(stack);
            if (inThisBus) return true;
            if (checkExternal) {
                return testConfiguredInOtherPart(stack);
            }
            return false;
        }

        private boolean hasStackInternal(GenericStack stack) {
            if (stack == null || stack.amount() <= 0) return false;
            for (int i = 0; i < MEEnlargedStockingInputHatchPartMachine.this.aeFluidHandler.getTanks(); i++) {
                var slot = MEEnlargedStockingInputHatchPartMachine.this.aeFluidHandler.getConfigurableSlot(i);
                GenericStack config = slot.getConfig();
                if (config != null && config.what().equals(stack.what())) {
                    return true;
                }
            }
            return false;
        }

        public void fireContentsChanged() {
            super.onContentsChanged();
        }
    }

    private class ExportOnlyAEStockingFluidSlot extends ExportOnlyAEFluidSlot {

        public ExportOnlyAEStockingFluidSlot() {
            super();
        }

        public ExportOnlyAEStockingFluidSlot(@Nullable GenericStack config, @Nullable GenericStack stock) {
            super(config, stock);
        }

        @Override
        public ExportOnlyAEFluidSlot copy() {
            return new ExportOnlyAEStockingFluidSlot(
                    this.config == null ? null : copy(this.config),
                    this.stock == null ? null : copy(this.stock));
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            if (this.stock != null && this.config != null) {
                if (!isOnline()) return FluidStack.EMPTY;
                MEStorage aeNetwork = getMainNode().getGrid().getStorageService().getInventory();

                Actionable actionable = action.simulate() ? Actionable.SIMULATE : Actionable.MODULATE;
                var key = config.what();
                long extracted = aeNetwork.extract(key, maxDrain, actionable, actionSource);

                if (extracted > 0) {
                    FluidStack resultStack = key instanceof AEFluidKey fluidKey ?
                            AEUtil.toFluidStack(fluidKey, extracted) : FluidStack.EMPTY;

                    if (action.execute()) {
                        this.stock = ExportOnlyAESlot.copy(stock, stock.amount() - extracted);
                        if (this.stock.amount() == 0) this.stock = null;
                        if (this.onContentsChanged != null) this.onContentsChanged.run();
                    }
                    return resultStack;
                }
            }
            return FluidStack.EMPTY;
        }
    }
}
