package net.neganote.gtutilities.mixin;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.common.data.GTItems;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.common.item.UtilItems;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ComponentItem.class)
public class ComponentItemMixin {
//    @Override
//    public boolean equals(Object obj) {
////        GregTechModernUtilities.LOGGER.info("ComponentItemMixin equals called");
//
//        var self = (ComponentItem) (Object) this;
//        var pCirc = GTItems.PROGRAMMED_CIRCUIT.get();
//        var pCard = UtilItems.PUNCH_CARD.get();
//
//        if (obj instanceof ComponentItem) {
//            var other = (ComponentItem) obj;
//            if (super.equals(pCard) || super.equals(pCirc)) {
//                GregTechModernUtilities.LOGGER.info("ComponentItemMixin equals called for " + self.toString() + " with " + obj.toString());
//                return other == pCirc || other == pCard;
//            }
//        }
//
//        return super.equals(obj);
//    }
}
