package net.neganote.gtutilities.integration.ae2.gridservice;

import net.minecraft.nbt.CompoundTag;

import appeng.api.config.Actionable;
import appeng.api.networking.GridServices;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class TagStockingGridService implements ITagStockingService, IGridServiceProvider {

    public static void register() {
        GridServices.register(ITagStockingService.class, TagStockingGridService.class);
    }

    private final IGrid grid;

    private final Set<ITagStockingPart> parts = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<ITagStockingPart> pendingRefresh = Collections.newSetFromMap(new IdentityHashMap<>());

    public TagStockingGridService(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public void addNode(IGridNode node, @Nullable CompoundTag savedData) {
        if (node.getOwner() instanceof ITagStockingPart part) {
            parts.add(part);
            pendingRefresh.add(part);
        }
    }

    @Override
    public void removeNode(IGridNode node) {
        if (node.getOwner() instanceof ITagStockingPart part) {
            parts.remove(part);
            pendingRefresh.remove(part);
        }
    }

    @Override
    public void markForRefresh(ITagStockingPart part) {
        if (parts.contains(part)) {
            pendingRefresh.add(part);
        }
    }

    @Override
    public void onServerStartTick() {
        if (parts.isEmpty()) {
            return;
        }

        List<ITagStockingPart> due = new ArrayList<>();
        for (var part : parts) {
            if (!part.isOnline()) {
                continue;
            }
            if (pendingRefresh.contains(part) || part.isTagStockSyncDue()) {
                due.add(part);
            }
        }
        due.forEach(pendingRefresh::remove);

        if (due.isEmpty()) {
            return;
        }

        KeyCounter cached = grid.getStorageService().getCachedInventory();

        Map<ITagStockingPart, PriorityQueue<GenericStack>> queues = new IdentityHashMap<>();
        for (var part : due) {
            queues.put(part, new PriorityQueue<>(Comparator.comparingLong(GenericStack::amount)));
        }

        for (var entry : cached) {
            long amount = entry.getLongValue();
            if (amount <= 0) {
                continue;
            }
            var what = entry.getKey();
            for (var part : due) {
                if (amount < part.getMinStackSize()) {
                    continue;
                }
                int pool = part.getStockingSlots() * 2;
                if (pool <= 0) {
                    continue;
                }
                if (!part.isTagKeyAllowed(what, amount)) {
                    continue;
                }
                PriorityQueue<GenericStack> pq = queues.get(part);
                if (pq.size() < pool) {
                    pq.offer(new GenericStack(what, amount));
                } else if (amount > pq.peek().amount()) {
                    pq.poll();
                    pq.offer(new GenericStack(what, amount));
                }
            }
        }

        MEStorage storage = grid.getStorageService().getInventory();

        for (var part : due) {
            PriorityQueue<GenericStack> pq = queues.get(part);
            int n = pq.size();
            GenericStack[] ranked = new GenericStack[n];
            for (int i = n - 1; i >= 0; i--) {
                ranked[i] = pq.poll();
            }

            int cap = part.getStockingSlots();
            List<GenericStack> confirmed = new ArrayList<>(Math.min(cap, n));
            for (GenericStack candidate : ranked) {
                if (confirmed.size() >= cap) {
                    break;
                }
                long extractable = storage.extract(candidate.what(), candidate.amount(), Actionable.SIMULATE,
                        part.getActionSource());
                if (extractable > 0) {
                    confirmed.add(new GenericStack(candidate.what(), extractable));
                }
            }
            part.applyTagSelection(confirmed);
        }
    }
}
