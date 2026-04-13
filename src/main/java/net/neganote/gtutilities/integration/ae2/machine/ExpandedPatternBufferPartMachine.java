package net.neganote.gtutilities.integration.ae2.machine;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.AETextInputButtonWidget;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEPatternViewSlotWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.InternalSlotRecipeHandler;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.crafting.pattern.EncodedPatternItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExpandedPatternBufferPartMachine extends MEPatternBufferPartMachine {

    protected static final int EXPANDED_MAX_PATTERN_COUNT = 72;

    public ExpandedPatternBufferPartMachine(IMachineBlockEntity info) {
        super(info);
        this.getPatternInventory().setSize(EXPANDED_MAX_PATTERN_COUNT);

        try {
            Field internalInvField = MEPatternBufferPartMachine.class.getDeclaredField("internalInventory");
            internalInvField.setAccessible(true);

            InternalSlot[] newInternalInv = new InternalSlot[EXPANDED_MAX_PATTERN_COUNT];
            for (int i = 0; i < EXPANDED_MAX_PATTERN_COUNT; i++) {
                newInternalInv[i] = new InternalSlot();
            }
            internalInvField.set(this, newInternalInv);

            Field recipeHandlerField = MEPatternBufferPartMachine.class.getDeclaredField("internalRecipeHandler");
            recipeHandlerField.setAccessible(true);
            recipeHandlerField.set(this, new InternalSlotRecipeHandler(this, newInternalInv));

        } catch (Exception e) {
            throw new RuntimeException("FATAL: Failed to initialize Expanded Pattern Buffer via reflection.", e);
        }
    }

    private void callPrivatePatternChange(int index) {
        try {
            Method method = MEPatternBufferPartMachine.class.getDeclaredMethod("onPatternChange", int.class);
            method.setAccessible(true);
            method.invoke(this, index);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke onPatternChange for index " + index, e);
        }
    }

    private String getCustomNameExpanded() {
        try {
            Field field = MEPatternBufferPartMachine.class.getDeclaredField("customName");
            field.setAccessible(true);
            return (String) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve customName via reflection", e);
        }
    }

    @Override
    public Widget createUIWidget() {
        int rowSize = 9;
        int colSize = 8;
        var group = new WidgetGroup(0, 0, 18 * rowSize + 16, 18 * colSize + 16);

        for (int i = 0; i < EXPANDED_MAX_PATTERN_COUNT; i++) {
            int index = i;
            int x = i % rowSize;
            int y = i / rowSize;

            var slot = new AEPatternViewSlotWidget(getPatternInventory(), index, 8 + x * 18, 14 + y * 18)
                    .setOccupiedTexture(GuiTextures.SLOT)
                    .setItemHook(stack -> {
                        if (!stack.isEmpty() && stack.getItem() instanceof EncodedPatternItem iep) {
                            final ItemStack out = iep.getOutput(stack);
                            if (!out.isEmpty()) return out;
                        }
                        return stack;
                    })
                    .setChangeListener(() -> callPrivatePatternChange(index))
                    .setBackground(GuiTextures.SLOT, GuiTextures.PATTERN_OVERLAY);
            group.addWidget(slot);
        }

        group.addWidget(new LabelWidget(8, 2,
                () -> this.isOnline ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"));

        group.addWidget(new AETextInputButtonWidget(18 * rowSize + 8 - 70, 2, 70, 10)
                .setText(getCustomNameExpanded())
                .setOnConfirm(this::setCustomName)
                .setButtonTooltips(Component.translatable("gui.gtceu.rename.desc")));

        return group;
    }
}
