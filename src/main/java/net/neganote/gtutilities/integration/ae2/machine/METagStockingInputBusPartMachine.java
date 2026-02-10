package net.neganote.gtutilities.integration.ae2.machine;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfiguratorButton;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.AutoStockingFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.CircuitFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.AEItemConfigWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.MEStockingBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAESlot;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.gtutilities.common.gui.widgets.MultilineTextField;
import net.neganote.gtutilities.utils.TagMatcher;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class METagStockingInputBusPartMachine extends MEStockingBusPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            METagStockingInputBusPartMachine.class, MEStockingBusPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    protected String whitelistExpr = "";
    @Persisted
    @DescSynced
    protected String blacklistExpr = "";
    @DescSynced
    protected String Wltmp = "";
    @DescSynced
    protected String Bltmp = "";

    @DescSynced
    protected boolean whitelistBadSyntax = false;

    @DescSynced
    protected boolean blacklistBadSyntax = false;

    private Predicate<GenericStack> tagAutoPullTest = ($) -> true;

    private transient String wlLast = null;
    private transient String blLast = null;
    private transient TagMatcher.Compiled wlCompiled = TagMatcher.compile("");
    private transient TagMatcher.Compiled blCompiled = TagMatcher.compile("");

    private final transient Object2ByteOpenHashMap<AEItemKey> decisionCache = new Object2ByteOpenHashMap<>();
    private static final int DECISION_CACHE_LIMIT = 8192;

    public METagStockingInputBusPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        decisionCache.defaultReturnValue((byte) -1);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected NotifiableItemStackHandler createInventory(Object... args) {
        this.aeItemHandler = new ExportOnlyAETagStockingItemList(this, CONFIG_SIZE);
        return this.aeItemHandler;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            if (super.isAutoPull()) {
                super.setAutoPull(false);
            }

            invalidateFilterCaches();

            if (updateMEStatus()) {
                refreshListFromTags();
                super.syncME();
                updateInventorySubscription();
            }
        }
    }

    @Override
    public void autoIO() {
        if (!isRemote() && super.isAutoPull()) {
            super.setAutoPull(false);
        }

        if (this.getTicksPerCycle() == 0) {
            this.setTicksPerCycle(ConfigHolder.INSTANCE.compat.ae2.updateIntervals);
        }

        if (this.getOffsetTimer() % (long) this.getTicksPerCycle() == 0L) {
            if (!isRemote() && updateMEStatus()) {
                refreshListFromTags();
                super.syncME();
                updateInventorySubscription();
            }
        }
    }

    private static String norm(@Nullable String s) {
        if (s == null) return "";
        s = s.trim();
        return s;
    }

    private void invalidateFilterCaches() {
        wlLast = null;
        blLast = null;
        decisionCache.clear();
    }

    private void ensureCompiledUpToDate() {
        String wl = norm(whitelistExpr);
        String bl = norm(blacklistExpr);

        if (!Objects.equals(wl, wlLast)) {
            wlLast = wl;
            wlCompiled = TagMatcher.compile(wl);
            whitelistBadSyntax = !wlCompiled.isValid();
            decisionCache.clear();
        }
        if (!Objects.equals(bl, blLast)) {
            blLast = bl;
            blCompiled = TagMatcher.compile(bl);
            blacklistBadSyntax = !blCompiled.isValid();
            decisionCache.clear();
        }

        if (decisionCache.size() > DECISION_CACHE_LIMIT) {
            decisionCache.clear();
        }
    }

    protected boolean isAllowed(AEItemKey key) {
        ensureCompiledUpToDate();
        if (whitelistBadSyntax || blacklistBadSyntax) return false;

        if ((wlLast == null || wlLast.isEmpty()) && (blLast == null || blLast.isEmpty())) return false;

        byte cached = decisionCache.getByte(key);
        if (cached != -1) {
            return cached == 1;
        }

        boolean allowed;
        if (blLast != null && !blLast.isEmpty() && blCompiled != null && blCompiled.isValid()) {
            if (TagMatcher.doesItemMatch(key, blCompiled)) {
                decisionCache.put(key, (byte) 0);
                return false;
            }
        }

        if (wlLast != null && !wlLast.isEmpty() && wlCompiled != null && wlCompiled.isValid()) {
            allowed = TagMatcher.doesItemMatch(key, wlCompiled);
        } else {
            allowed = false;
        }

        decisionCache.put(key, allowed ? (byte) 1 : (byte) 0);
        return allowed;
    }

    protected void refreshListFromTags() {
        IGrid grid = this.getMainNode().getGrid();
        if (grid == null) {
            this.aeItemHandler.clearInventory(0);
            return;
        }

        MEStorage networkStorage = grid.getStorageService().getInventory();
        var counter = networkStorage.getAvailableStacks();

        PriorityQueue<Object2LongMap.Entry<AEKey>> top = new PriorityQueue<>(
                Comparator.comparingLong(Object2LongMap.Entry::getLongValue));

        for (Object2LongMap.Entry<AEKey> entry : counter) {
            long amount = entry.getLongValue();
            AEKey what = entry.getKey();

            if (amount <= 0) continue;
            if (!(what instanceof AEItemKey itemKey)) continue;

            if (!isAllowed(itemKey)) continue;

            long request = networkStorage.extract(what, amount, Actionable.SIMULATE, this.actionSource);
            if (request == 0L) continue;

            if (tagAutoPullTest != null && !tagAutoPullTest.test(new GenericStack(itemKey, amount))) continue;

            if (top.size() < CONFIG_SIZE) {
                top.offer(entry);
            } else if (amount > Objects.requireNonNull(top.peek()).getLongValue()) {
                top.poll();
                top.offer(entry);
            }
        }

        int itemAmount = top.size();

        int index;
        for (index = 0; index < CONFIG_SIZE && !top.isEmpty(); index++) {
            Object2LongMap.Entry<AEKey> entry = top.poll();
            AEKey what = entry.getKey();
            long amount = entry.getLongValue();
            long request = networkStorage.extract(what, amount, Actionable.SIMULATE, this.actionSource);

            ExportOnlyAEItemSlot slot = this.aeItemHandler.getInventory()[itemAmount - index - 1];
            slot.setConfig(new GenericStack(what, 1L));
            slot.setStock(new GenericStack(what, request));
        }
        this.aeItemHandler.clearInventory(index);
        markDirty();
        self().getHolder().self().setChanged();
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(new Position(0, 0), new Size(176, 150));

        int y = 6;
        group.addWidget(new LabelWidget(3, y, () -> this.isOnline ?
                "gtceu.gui.me_network.online" :
                "gtceu.gui.me_network.offline"));

        group.addWidget(new ToggleButtonWidget(176 - 55, 2, 50, 16, () -> false, pressed -> {
            whitelistExpr = pressed ? Wltmp : whitelistExpr;
            blacklistExpr = pressed ? Bltmp : blacklistExpr;

            invalidateFilterCaches();

            if (updateMEStatus()) {
                refreshListFromTags();
                super.syncME();
                updateInventorySubscription();
            }
        }).setTexture(new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("Confirm")),
                new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("Confirm"))));

        y += 14;
        var WLField = new MultilineTextField(
                7, y, 160, 25,
                () -> Wltmp,
                v -> {
                    Wltmp = v;
                },
                Component.literal("Whitelist tags..."),
                () -> whitelistBadSyntax ? 0xFFFF0000 : null);
        group.addWidget(WLField);

        y += 29;
        var BLField = new MultilineTextField(
                7, y, 160, 25,
                () -> Bltmp,
                v -> {
                    Bltmp = v;
                },
                Component.literal("Blacklist tags..."),
                () -> blacklistBadSyntax ? 0xFFFF0000 : null);
        group.addWidget(BLField);

        WLField.setDirectly(whitelistExpr);
        BLField.setDirectly(blacklistExpr);

        y += 29;

        group.addWidget(new AEItemConfigWidget(15, y, this.aeItemHandler) {

            @Override
            public boolean isStocking() {
                return true;
            }

            @Override
            public boolean isAutoPull() {
                return true;
            }
        });

        return group;
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player playerIn, InteractionHand hand, Direction gridSide,
                                                   BlockHitResult hitResult) {
        if (!this.isRemote()) {
            playerIn.sendSystemMessage(Component.literal("This bus is always Tag-Mode (no manual/autoPull toggle)."));
        }
        return InteractionResult.sidedSuccess(this.isRemote());
    }

    @Override
    protected CompoundTag writeConfigToTag() {
        CompoundTag tag = super.writeConfigToTag();
        tag.putString("WhitelistExpr", whitelistExpr);
        tag.putString("BlacklistExpr", blacklistExpr);
        tag.putBoolean("TagMode", true);
        return tag;
    }

    @Override
    protected void readConfigFromTag(CompoundTag tag) {
        super.readConfigFromTag(tag);
        if (!isRemote() && super.isAutoPull()) {
            super.setAutoPull(false);
        }

        if (tag.contains("WhitelistExpr")) whitelistExpr = tag.getString("WhitelistExpr");
        if (tag.contains("BlacklistExpr")) blacklistExpr = tag.getString("BlacklistExpr");

        invalidateFilterCaches();

        if (!isRemote() && updateMEStatus()) {
            refreshListFromTags();
            super.syncME();
            updateInventorySubscription();
        }
    }

    @Override
    public void setAutoPullTest(Predicate<GenericStack> autoPullTest) {
        this.tagAutoPullTest = autoPullTest;
        super.setAutoPullTest(autoPullTest);
    }

    // We can't call super to avoid the auto-pull toggle...
    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        configuratorPanel.attachConfigurators(new IFancyConfiguratorButton.Toggle(
                GuiTextures.BUTTON_POWER.getSubTexture(0, 0, 1, 0.5),
                GuiTextures.BUTTON_POWER.getSubTexture(0, 0.5, 1, 0.5),
                this::isWorkingEnabled, (clickData, pressed) -> this.setWorkingEnabled(pressed))
                .setTooltipsSupplier(pressed -> List.of(
                        Component.translatable(
                                pressed ? "behaviour.soft_hammer.enabled" : "behaviour.soft_hammer.disabled"))));
        for (var direction : Direction.values()) {
            if (this.getCoverContainer().hasCover(direction)) {
                var configurator = this.getCoverContainer().getCoverAtSide(direction).getConfigurator();
                if (configurator != null)
                    configuratorPanel.attachConfigurators(configurator);
            }
        }
        configuratorPanel.attachConfigurators(new IFancyConfiguratorButton.Toggle(
                GuiTextures.BUTTON_DISTINCT_BUSES.getSubTexture(0, 0.5, 1, 0.5),
                GuiTextures.BUTTON_DISTINCT_BUSES.getSubTexture(0, 0, 1, 0.5),
                this::isDistinct, (clickData, pressed) -> setDistinct(pressed))
                .setTooltipsSupplier(pressed -> List.of(
                        Component.translatable("gtceu.multiblock.universal.distinct")
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW))
                                .append(Component.translatable(pressed ? "gtceu.multiblock.universal.distinct.yes" :
                                        "gtceu.multiblock.universal.distinct.no")))));
        configuratorPanel.attachConfigurators(new CircuitFancyConfigurator(circuitInventory.storage));
        configuratorPanel.attachConfigurators(new AutoStockingFancyConfigurator(this));
    }

    private class ExportOnlyAETagStockingItemList extends ExportOnlyAEItemList {

        public ExportOnlyAETagStockingItemList(MetaMachine holder, int slots) {
            super(holder, slots, () -> METagStockingInputBusPartMachine.this.new ExportOnlyAETagStockingItemSlot());
        }

        @Override
        public boolean isAutoPull() {
            return true;
        }

        @Override
        public boolean isStocking() {
            return true;
        }

        @Override
        public boolean hasStackInConfig(GenericStack stack, boolean checkExternal) {
            boolean inThisBus = super.hasStackInConfig(stack, false);
            if (inThisBus) return true;
            return checkExternal && METagStockingInputBusPartMachine.this.testConfiguredInOtherPart(stack);
        }
    }

    private class ExportOnlyAETagStockingItemSlot extends ExportOnlyAEItemSlot {

        public ExportOnlyAETagStockingItemSlot() {}

        public ExportOnlyAETagStockingItemSlot(@Nullable GenericStack config, GenericStack stock) {
            super(config, stock);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0 && this.stock != null && this.config != null) {
                if (!METagStockingInputBusPartMachine.this.isOnline()) return ItemStack.EMPTY;

                IGrid grid = METagStockingInputBusPartMachine.this.getMainNode().getGrid();
                if (grid == null) return ItemStack.EMPTY;

                MEStorage aeNetwork = grid.getStorageService().getInventory();
                Actionable action = simulate ? Actionable.SIMULATE : Actionable.MODULATE;
                AEKey key = this.config.what();

                long extracted = aeNetwork.extract(key, amount, action,
                        METagStockingInputBusPartMachine.this.actionSource);
                if (extracted > 0L) {
                    ItemStack result;
                    if (key instanceof AEItemKey itemKey) result = itemKey.toStack((int) extracted);
                    else result = ItemStack.EMPTY;

                    if (!simulate) {
                        this.stock = ExportOnlyAESlot.copy(this.stock, this.stock.amount() - extracted);
                        if (this.stock.amount() == 0L) this.stock = null;
                        if (this.onContentsChanged != null) this.onContentsChanged.run();
                    }
                    return result;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ExportOnlyAETagStockingItemSlot copy() {
            return METagStockingInputBusPartMachine.this.new ExportOnlyAETagStockingItemSlot(
                    this.config == null ? null : copy(this.config),
                    this.stock == null ? null : copy(this.stock));
        }
    }
}
