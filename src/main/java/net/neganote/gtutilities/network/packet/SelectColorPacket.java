package net.neganote.gtutilities.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.neganote.gtutilities.common.item.InfiniteSprayCanBehaviour;
import net.neganote.gtutilities.common.item.InfiniteSprayCanItem;
import net.neganote.gtutilities.utils.UtilColor;

import java.util.function.Supplier;

public class SelectColorPacket {

    private final InteractionHand hand;
    private final int selectedIndex;

    public SelectColorPacket(InteractionHand hand, int selectedIndex) {
        this.hand = hand;
        this.selectedIndex = selectedIndex;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(hand);
        buf.writeVarInt(selectedIndex);
    }

    public static SelectColorPacket decode(FriendlyByteBuf buf) {
        return new SelectColorPacket(buf.readEnum(InteractionHand.class), buf.readVarInt());
    }

    public static void handle(SelectColorPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack stack = player.getItemInHand(msg.hand);
            if (stack.getItem() instanceof InfiniteSprayCanItem) {
                UtilColor[] colors = UtilColor.values();
                UtilColor selectedColor = null;

                if (msg.selectedIndex >= 0 && msg.selectedIndex < colors.length) {
                    selectedColor = colors[msg.selectedIndex];
                }

                InfiniteSprayCanBehaviour.setColor(stack, selectedColor);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
