package net.neganote.gtutilities.mixin;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;

import net.neganote.gtutilities.config.UtilConfig;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParallelHatchPartMachine.class)
public abstract class ParallelHatchPartMachineMixin {

    @Shadow
    public abstract void setCurrentParallel(int parallelAmount);

    @Shadow
    @Final
    private int maxParallel;

    @Inject(
            method = "<init>",
            at = @At("TAIL"))
    public void constructor(IMachineBlockEntity holder, int tier, CallbackInfo ci) {
        if (UtilConfig.INSTANCE.features.parallelHatchAutoConfigure)
            this.setCurrentParallel(this.maxParallel);
    }
}
