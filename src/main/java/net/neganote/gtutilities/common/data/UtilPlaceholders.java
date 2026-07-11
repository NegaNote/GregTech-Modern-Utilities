package net.neganote.gtutilities.common.data;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.placeholder.*;
import com.gregtechceu.gtceu.api.placeholder.exceptions.NotSupportedException;
import com.gregtechceu.gtceu.api.placeholder.exceptions.PlaceholderException;

import net.minecraft.network.chat.Component;
import net.neganote.gtutilities.common.machine.multiblock.WEBHubMachine;

import java.util.List;

public class UtilPlaceholders {

    public static void init() {
        PlaceholderHandler.addPlaceholder(new Placeholder("webfrequency") {

            @Override
            public MultiLineComponent apply(PlaceholderContext ctx,
                                            List<MultiLineComponent> args) throws PlaceholderException {
                PlaceholderUtils.checkArgs(args, 0);
                MetaMachine machine = MetaMachine.getMachine(ctx.level(), ctx.pos());
                if (!(machine instanceof WEBHubMachine wat)) {
                    throw new NotSupportedException();
                }
                var freq = wat.getFrequencyString();
                return MultiLineComponent.of(Component.translatable("gtmutils.web_machine.current_frequency",
                        freq));
            }
        });
    }
}
