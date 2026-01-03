package net.neganote.gtutilities.integration.ae2.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.IRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerGroupDistinctness;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;

import com.lowdragmc.lowdraglib.syncdata.ISubscription;

import net.minecraft.world.item.crafting.Ingredient;
import net.neganote.gtutilities.integration.ae2.machine.ExpandedPatternBufferPartMachine;
import net.neganote.gtutilities.integration.ae2.machine.ExpandedPatternBufferProxyPartMachine;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExpandedProxySlotRecipeHandler {

    @Getter
    private final List<RecipeHandlerList> proxySlotHandlers;

    public ExpandedProxySlotRecipeHandler(ExpandedPatternBufferProxyPartMachine machine, int slots) {
        proxySlotHandlers = new ArrayList<>(slots);
        for (int i = 0; i < slots; ++i) {
            proxySlotHandlers.add(new ProxyRHL(machine));
        }
    }

    public void updateProxy(ExpandedPatternBufferPartMachine patternBuffer) {
        var slotHandlers = patternBuffer.getInternalRecipeHandler().getSlotHandlers();
        for (int i = 0; i < proxySlotHandlers.size(); ++i) {
            ProxyRHL proxyRHL = (ProxyRHL) proxySlotHandlers.get(i);
            ExpandedInternalSlotRecipeHandler.SlotRHL slotRHL = (ExpandedInternalSlotRecipeHandler.SlotRHL) slotHandlers
                    .get(i);
            proxyRHL.setBuffer(patternBuffer, slotRHL);
        }
    }

    public void clearProxy() {
        for (var slotHandler : proxySlotHandlers) {
            ((ProxyRHL) slotHandler).clearBuffer();
        }
    }

    private static class ProxyRHL extends RecipeHandlerList {

        private final ProxyItemRecipeHandler circuit;
        private final ProxyItemRecipeHandler sharedItem;
        private final ProxyItemRecipeHandler slotItem;
        private final ProxyFluidRecipeHandler sharedFluid;
        private final ProxyFluidRecipeHandler slotFluid;

        public ProxyRHL(ExpandedPatternBufferProxyPartMachine machine) {
            super(IO.IN);
            circuit = new ProxyItemRecipeHandler(machine);
            sharedItem = new ProxyItemRecipeHandler(machine);
            slotItem = new ProxyItemRecipeHandler(machine);
            sharedFluid = new ProxyFluidRecipeHandler(machine);
            slotFluid = new ProxyFluidRecipeHandler(machine);
            addHandlers(circuit, sharedItem, slotItem, sharedFluid, slotFluid);
            this.setGroup(RecipeHandlerGroupDistinctness.BUS_DISTINCT);
        }

        public void setBuffer(ExpandedPatternBufferPartMachine buffer,
                              ExpandedInternalSlotRecipeHandler.SlotRHL slotRHL) {
            circuit.setProxy(buffer.getCircuitInventory());
            sharedItem.setProxy(buffer.getShareInventory());
            sharedFluid.setProxy(buffer.getShareTank());
            slotItem.setProxy(slotRHL.getItemRecipeHandler());
            slotFluid.setProxy(slotRHL.getFluidRecipeHandler());
        }

        public void clearBuffer() {
            circuit.setProxy(null);
            sharedItem.setProxy(null);
            sharedFluid.setProxy(null);
            slotItem.setProxy(null);
            slotFluid.setProxy(null);
        }

        @Override
        public boolean isDistinct() {
            return true;
        }

        @Override
        public void setDistinct(boolean ignored, boolean notify) {}
    }

    @Getter
    private static class ProxyItemRecipeHandler extends NotifiableRecipeHandlerTrait<Ingredient> {

        private IRecipeHandlerTrait<Ingredient> proxy = null;
        private ISubscription proxySub = null;
        private final IO handlerIO = IO.IN;
        private final RecipeCapability<Ingredient> capability = ItemRecipeCapability.CAP;
        private final boolean isDistinct = true;

        public ProxyItemRecipeHandler(MetaMachine machine) {
            super(machine);
        }

        public void setProxy(IRecipeHandlerTrait<Ingredient> proxy) {
            this.proxy = proxy;
            if (proxySub != null) {
                proxySub.unsubscribe();
                proxySub = null;
            }
            if (proxy != null) {
                proxySub = proxy.addChangedListener(this::notifyListeners);
            }
        }

        @Override
        public List<Ingredient> handleRecipeInner(IO io, GTRecipe recipe, List<Ingredient> left, boolean simulate) {
            if (proxy == null) return left;
            return proxy.handleRecipeInner(io, recipe, left, simulate);
        }

        @Override
        public int getSize() {
            return proxy == null ? 0 : proxy.getSize();
        }

        @Override
        public @NotNull List<Object> getContents() {
            return proxy == null ? Collections.emptyList() : proxy.getContents();
        }

        @Override
        public double getTotalContentAmount() {
            return proxy == null ? 0 : proxy.getTotalContentAmount();
        }

        public int getPriority() {
            return proxy == null ? IFilteredHandler.LOW : proxy.getPriority();
        }
    }

    @Getter
    private static class ProxyFluidRecipeHandler extends NotifiableRecipeHandlerTrait<FluidIngredient> {

        private IRecipeHandlerTrait<FluidIngredient> proxy = null;
        private ISubscription proxySub = null;
        private final IO handlerIO = IO.IN;
        private final RecipeCapability<FluidIngredient> capability = FluidRecipeCapability.CAP;
        private final boolean isDistinct = true;

        public ProxyFluidRecipeHandler(MetaMachine machine) {
            super(machine);
        }

        public void setProxy(IRecipeHandlerTrait<FluidIngredient> proxy) {
            this.proxy = proxy;
            if (proxySub != null) {
                proxySub.unsubscribe();
                proxySub = null;
            }
            if (proxy != null) {
                proxySub = proxy.addChangedListener(this::notifyListeners);
            }
        }

        @Override
        public List<FluidIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<FluidIngredient> left,
                                                       boolean simulate) {
            if (proxy == null) return left;
            return proxy.handleRecipeInner(io, recipe, left, simulate);
        }

        @Override
        public int getSize() {
            return proxy == null ? 0 : proxy.getSize();
        }

        @Override
        public @NotNull List<Object> getContents() {
            return proxy == null ? Collections.emptyList() : proxy.getContents();
        }

        @Override
        public double getTotalContentAmount() {
            return proxy == null ? 0 : proxy.getTotalContentAmount();
        }

        public int getPriority() {
            return proxy == null ? IFilteredHandler.LOW : proxy.getPriority();
        }
    }
}
