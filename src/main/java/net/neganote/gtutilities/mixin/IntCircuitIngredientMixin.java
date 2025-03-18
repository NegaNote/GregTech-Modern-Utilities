package net.neganote.gtutilities.mixin;

import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import net.minecraft.world.item.ItemStack;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.common.item.UtilItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntCircuitIngredient.class)
public class IntCircuitIngredientMixin {
    @Shadow
    private int configuration;

//    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true, remap = false)
//    private void testMixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
//        var this$0 = (IntCircuitIngredient) (Object) this;
//
//        if (stack == null) {
//            cir.setReturnValue(false);
//        }
//        else {
//            cir.setReturnValue(IntCircuitBehaviour.isIntegratedCircuit(stack) &&
//                    IntCircuitBehaviour.getCircuitConfiguration(stack) == configuration);
//        }
//
//        GregTechModernUtilities.LOGGER.info("Circuit Recipe test returns " + cir.getReturnValue() + " for configuration " + configuration + " with " + stack.toString());
//
//        cir.cancel();
//    }

//
//    @Inject(method = "getItems", at = @At("HEAD"), cancellable = true, remap = false)
//    private void getItemsMixin(CallbackInfoReturnable<ItemStack[]> cir) {
//        cir.setReturnValue(new ItemStack[]{IntCircuitBehaviour.stack(configuration), UtilItems.punchCard(1, configuration)});
//        cir.cancel();
//    }
}
