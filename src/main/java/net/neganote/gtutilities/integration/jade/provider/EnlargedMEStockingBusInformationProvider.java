package net.neganote.gtutilities.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.machine.MEStockingBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlot;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.integration.ae2.machine.MEEnlargedStockingInputBusPartMachine;
import net.neganote.gtutilities.integration.ae2.machine.MEEnlargedTagStockingInputBusPartMachine;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class EnlargedMEStockingBusInformationProvider
                                                      implements IBlockComponentProvider,
                                                      IServerDataProvider<BlockAccessor> {

    private static final int MAX_ICONS = 36;
    private static final int ICONS_PER_ROW = 9;

    @Override
    public ResourceLocation getUid() {
        return GregTechModernUtilities.id("enlarged_me_stocking_bus_info");
    }

    @Override
    public int getDefaultPriority() {
        return Integer.MAX_VALUE;
    }

    private static boolean isTargetMachine(Object mm) {
        return mm instanceof MEEnlargedStockingInputBusPartMachine ||
                mm instanceof MEEnlargedTagStockingInputBusPartMachine;
    }

    @Override
    public void appendServerData(CompoundTag out, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof MetaMachineBlockEntity mmbe)) return;

        Object mm = mmbe.getMetaMachine();
        if (!isTargetMachine(mm)) return;

        MEStockingBusPartMachine bus = (MEStockingBusPartMachine) mm;
        var slotList = bus.getSlotList();
        int total = slotList.getConfigurableSlots();

        ObjectOpenHashSet<AEItemKey> unique = new ObjectOpenHashSet<>();
        ListTag entries = new ListTag();

        for (int i = 0; i < total && entries.size() < MAX_ICONS; i++) {
            IConfigurableSlot slot = slotList.getConfigurableSlot(i);
            if (slot == null) continue;

            GenericStack cfg = slot.getConfig();
            GenericStack st = slot.getStock();

            AEItemKey key = null;
            if (cfg != null && cfg.what() instanceof AEItemKey k) key = k;
            else if (st != null && st.what() instanceof AEItemKey k) key = k;

            if (key == null) continue;
            if (!unique.add(key)) continue;

            ItemStack icon = key.toStack(st == null ? 0 : (int) st.amount());

            CompoundTag e = new CompoundTag();
            e.put("item", icon.save(new CompoundTag()));
            entries.add(e);
        }

        CompoundTag data = new CompoundTag();
        data.put("entries", entries);
        out.put(getUid().toString(), data);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof MetaMachineBlockEntity mmbe)) return;

        Object mm = mmbe.getMetaMachine();
        if (!isTargetMachine(mm)) return;

        CompoundTag root = accessor.getServerData().getCompound(getUid().toString());
        if (root == null) return;

        ListTag entries = root.getList("entries", Tag.TAG_COMPOUND);

        tooltip.clear();
        var helper = tooltip.getElementHelper();

        ItemStack picked = accessor.getPickedResult();
        Component title = !picked.isEmpty() ?
                Component.literal(picked.getHoverName().getString()).withStyle(ChatFormatting.WHITE) :
                Component.literal("Machine").withStyle(ChatFormatting.WHITE);

        tooltip.append(helper.text(title));

        int rowIndex = -1;
        int inRow = 0;

        for (int i = 0; i < entries.size() && i < MAX_ICONS; i++) {
            ItemStack icon = ItemStack.of(entries.getCompound(i).getCompound("item"));
            if (icon.isEmpty()) continue;

            if (rowIndex == -1 || inRow >= ICONS_PER_ROW) {
                tooltip.add(helper.item(icon));
                rowIndex = tooltip.size() - 1;
                inRow = 1;
            } else {
                tooltip.append(rowIndex, helper.item(icon));
                inRow++;
            }
        }

        tooltip.add(Component.literal("GregTech Modern Utilities")
                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
    }
}
