package net.neganote.gtutilities.integration.ae2.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferProxyPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.ProxySlotRecipeHandler;

import net.minecraft.MethodsReturnNonnullByDefault;

import java.lang.reflect.Field;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExpandedPatternBufferProxyPartMachine extends MEPatternBufferProxyPartMachine {

    public ExpandedPatternBufferProxyPartMachine(IMachineBlockEntity info) {
        super(info);
        try {
            Field handlerField = MEPatternBufferProxyPartMachine.class.getDeclaredField("proxySlotRecipeHandler");
            handlerField.setAccessible(true);
            handlerField.set(this,
                    new ProxySlotRecipeHandler(this, ExpandedPatternBufferPartMachine.EXPANDED_MAX_PATTERN_COUNT));
        } catch (Exception e) {
            throw new RuntimeException("FATAL: Failed to initialize Expanded Proxy Recipe Handler via reflection.", e);
        }
    }
}
