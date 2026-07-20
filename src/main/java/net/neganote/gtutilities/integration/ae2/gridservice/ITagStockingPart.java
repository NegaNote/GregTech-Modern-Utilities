package net.neganote.gtutilities.integration.ae2.gridservice;

import com.gregtechceu.gtceu.api.machine.feature.IMachineFeature;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlot;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlotList;

import appeng.api.networking.IGrid;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

import java.util.List;

public interface ITagStockingPart extends IMachineFeature {

    IManagedGridNode getMainNode();

    IActionSource getActionSource();

    boolean isOnline();

    IConfigurableSlotList getSlotList();

    int getMinStackSize();

    int getTicksPerCycle();

    boolean isTagKeyAllowed(AEKey key, long amount);

    default int getStockingSlots() {
        return getSlotList().getConfigurableSlots();
    }

    default boolean isTagStockSyncDue() {
        int interval = getTicksPerCycle();
        if (interval <= 0) {
            interval = ConfigHolder.INSTANCE.compat.ae2.updateIntervals;
        }
        if (interval <= 0) {
            interval = 1;
        }
        return self().getOffsetTimer() % interval == 0;
    }

    default void applyTagSelection(List<GenericStack> selection) {
        IConfigurableSlotList slots = getSlotList();
        int slotCount = slots.getConfigurableSlots();
        int i = 0;
        for (GenericStack stack : selection) {
            if (i >= slotCount) {
                break;
            }
            IConfigurableSlot slot = slots.getConfigurableSlot(i++);
            slot.setConfig(new GenericStack(stack.what(), 1));
            slot.setStock(stack);
        }
        slots.clearInventory(i);
        self().markDirty();
        self().getHolder().self().setChanged();
    }

    default void markTagRefresh() {
        if (self().isRemote()) {
            return;
        }
        IGrid grid = getMainNode().getGrid();
        if (grid != null) {
            ITagStockingService service = grid.getService(ITagStockingService.class);
            if (service != null) {
                service.markForRefresh(this);
            }
        }
    }
}
