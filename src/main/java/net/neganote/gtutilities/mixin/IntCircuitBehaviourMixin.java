package net.neganote.gtutilities.mixin;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neganote.gtutilities.common.item.UtilItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntCircuitBehaviour.class)
public class IntCircuitBehaviourMixin {

    @Inject(method = "isIntegratedCircuit", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isIntegratedCircuitMixin(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        boolean isCircuit = GTItems.PROGRAMMED_CIRCUIT.isIn(itemStack) || UtilItems.PUNCH_CARD.isIn(itemStack);
        if (isCircuit && !itemStack.hasTag()) {
            var compound = new CompoundTag();
            compound.putInt("Configuration", 0);
            itemStack.setTag(compound);
        }
        cir.setReturnValue(isCircuit);
        cir.cancel();
    }

}
