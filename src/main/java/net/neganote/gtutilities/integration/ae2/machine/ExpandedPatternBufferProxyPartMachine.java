package net.neganote.gtutilities.integration.ae2.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.gtutilities.integration.ae2.machine.trait.ExpandedProxySlotRecipeHandler;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ExpandedPatternBufferProxyPartMachine extends TieredIOPartMachine
                                                   implements IMachineLife, IDataStickInteractable {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ExpandedPatternBufferProxyPartMachine.class, TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    //The proxy seems to be working fine now.
    @Getter
    private final ExpandedProxySlotRecipeHandler proxySlotRecipeHandler;

    @Persisted
    @Getter
    @DescSynced
    private @Nullable BlockPos bufferPos;

    private @Nullable ExpandedPatternBufferPartMachine buffer = null;
    private boolean bufferResolved = false;

    public ExpandedPatternBufferProxyPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.LuV, IO.IN);
        this.proxySlotRecipeHandler = new ExpandedProxySlotRecipeHandler(this, 72);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel() instanceof ServerLevel level) {
            level.getServer().tell(new TickTask(0, () -> this.setBuffer(bufferPos)));
        }
    }

    @Override
    public List<RecipeHandlerList> getRecipeHandlers() {
        return proxySlotRecipeHandler.getProxySlotHandlers();
    }

    public void setBuffer(@Nullable BlockPos pos) {
        this.bufferResolved = true;
        var level = getLevel();
        if (level == null || pos == null) {
            this.buffer = null;
        } else if (MetaMachine.getMachine(level, pos) instanceof ExpandedPatternBufferPartMachine machine) {
            bufferPos = pos;
            buffer = machine;
            machine.addProxy(this);
            if (!isRemote()) proxySlotRecipeHandler.updateProxy(machine);
        } else {
            this.buffer = null;
        }
    }

    @Nullable
    public ExpandedPatternBufferPartMachine getBuffer() {
        if (!bufferResolved) setBuffer(bufferPos);
        return buffer;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return getBuffer() != null;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        var buf = getBuffer();
        assert buf != null;
        return buf.createUI(entityPlayer);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onMachineRemoved() {
        var buf = getBuffer();
        if (buf != null) {
            buf.removeProxy(this);
            proxySlotRecipeHandler.clearProxy();
        }
    }

    @Override
    public InteractionResult onDataStickUse(Player player, ItemStack dataStick) {
        if (dataStick.hasTag()) {
            assert dataStick.getTag() != null;
            if (dataStick.getTag().contains("pos", Tag.TAG_INT_ARRAY)) {
                var posArray = dataStick.getOrCreateTag().getIntArray("pos");
                var newBufferPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
                setBuffer(newBufferPos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
