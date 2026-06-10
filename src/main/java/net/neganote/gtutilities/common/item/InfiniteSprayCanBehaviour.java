package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.blockentity.IPaintable;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.pipenet.IPipeNode;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.utils.BreadthFirstBlockSearch;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.event.level.BlockEvent;

import appeng.api.implementations.blockentities.IColorableBlockEntity;
import appeng.api.util.AEColor;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class InfiniteSprayCanBehaviour implements IInteractionItem, IAddInformation {

    private static final ImmutableMap<DyeColor, Block> GLASS_MAP;
    private static final ImmutableMap<DyeColor, Block> GLASS_PANE_MAP;
    private static final ImmutableMap<DyeColor, Block> TERRACOTTA_MAP;
    private static final ImmutableMap<DyeColor, Block> WOOL_MAP;
    private static final ImmutableMap<DyeColor, Block> CARPET_MAP;
    private static final ImmutableMap<DyeColor, Block> CONCRETE_MAP;
    private static final ImmutableMap<DyeColor, Block> CONCRETE_POWDER_MAP;
    private static final ImmutableMap<DyeColor, Block> SHULKER_BOX_MAP;

    private static Block getBlock(DyeColor color, String postfix) {
        ResourceLocation id = new ResourceLocation("minecraft", color.getSerializedName() + "_" + postfix);
        return BuiltInRegistries.BLOCK.get(id);
    }

    static {
        ImmutableMap.Builder<DyeColor, Block> glassBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<DyeColor, Block> glassPaneBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<DyeColor, Block> terracottaBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<DyeColor, Block> woolBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<DyeColor, Block> carpetBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<DyeColor, Block> concreteBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<DyeColor, Block> concretePowderBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<DyeColor, Block> shulkerBoxBuilder = ImmutableMap.builder();

        for (DyeColor color : DyeColor.values()) {
            glassBuilder.put(color, getBlock(color, "stained_glass"));
            glassPaneBuilder.put(color, getBlock(color, "stained_glass_pane"));
            terracottaBuilder.put(color, getBlock(color, "terracotta"));
            woolBuilder.put(color, getBlock(color, "wool"));
            carpetBuilder.put(color, getBlock(color, "carpet"));
            concreteBuilder.put(color, getBlock(color, "concrete"));
            concretePowderBuilder.put(color, getBlock(color, "concrete_powder"));
            shulkerBoxBuilder.put(color, getBlock(color, "shulker_box"));
        }
        GLASS_MAP = glassBuilder.build();
        GLASS_PANE_MAP = glassPaneBuilder.build();
        TERRACOTTA_MAP = terracottaBuilder.build();
        WOOL_MAP = woolBuilder.build();
        CARPET_MAP = carpetBuilder.build();
        CONCRETE_MAP = concreteBuilder.build();
        CONCRETE_POWDER_MAP = concretePowderBuilder.build();
        SHULKER_BOX_MAP = shulkerBoxBuilder.build();
    }

    private static final TriPredicate<IPaintable, IPaintable, Direction> paintablePredicate = (parent, child, dir) -> {
        if (parent == null) return true;
        if (!parent.getClass().equals(child.getClass())) {
            return false;
        }
        return parent.getPaintingColor() == child.getPaintingColor();
    };

    @SuppressWarnings("rawtypes")
    private static final TriPredicate<IPipeNode, IPipeNode, Direction> gtPipePredicate = (parent, child, direction) -> {
        if (parent == null) return true;
        if (!paintablePredicate.test(parent, child, direction)) {
            return false;
        }
        return parent.isConnected(direction) && child.isConnected(direction.getOpposite());
    };

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null) return InteractionResult.PASS;

        DyeColor selectedColor = getColor(stack);
        int maxBlocksToRecolor = player.isShiftKeyDown() ? ConfigHolder.INSTANCE.tools.sprayCanChainLength : 1;

        var pos = context.getClickedPos();
        tryPaintAt(level, pos, selectedColor, maxBlocksToRecolor, player);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void tryPaintAt(Level level, BlockPos pos, @Nullable DyeColor color, int limit, Player player) {
        var first = level.getBlockEntity(pos);
        if (first == null || !handleSpecialBlockEntities(first, color, limit, level, player))
            handleBlocks(pos, color, limit, level);

        GTSoundEntries.SPRAY_CAN_TOOL.play(level, null, player.position(), 1.0f, 1.0f);
    }

    private static boolean tryStripAt(Level level, BlockPos pos, Player player) {
        var before = level.getBlockState(pos);
        var be = level.getBlockEntity(pos);
        if (be != null && handleSpecialBlockEntities(be, null, 1, level, player)) return true;

        tryStripBlockColor(level, pos, before.getBlock());
        return level.getBlockState(pos) != before;
    }

    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getLevel() instanceof Level level)) return;

        ItemStack offhand = player.getOffhandItem();
        if (!(offhand.getItem() instanceof InfiniteSprayCanItem)) return;

        tryPaintAt(level, event.getPos(), getColor(offhand), 1, player);
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) return;
        if (event.getPlayer() == null) return;
        if (!(event.getLevel() instanceof Level level)) return;
        var player = event.getPlayer();

        var offhand = player.getOffhandItem();
        if (!(offhand.getItem() instanceof InfiniteSprayCanItem)) return;

        var pos = event.getPos();
        var stateBefore = level.getBlockState(pos);

        if (!tryStripAt(level, pos, player)) return;

        GTSoundEntries.SPRAY_CAN_TOOL.play(level, null, player.position(), 1.0f, 1.0f);

        var stateAfter = level.getBlockState(pos);
        if (stateAfter == stateBefore) return;
        event.setCanceled(true);

        var be = level.getBlockEntity(pos);
        var tool = player.getMainHandItem();

        level.removeBlock(pos, false);
        if (!player.isCreative()) stateAfter.getBlock().playerDestroy(level, player, pos, stateAfter, be, tool);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        DyeColor currentColor = getColor(stack);
        if (currentColor != null) {
            tooltip.add(Component.translatable("behaviour.paintspray.infinite.tooltip.current_color",
                    Component.translatable("color.minecraft." + currentColor.getSerializedName())));
        } else {
            tooltip.add(Component.translatable("behaviour.paintspray.infinite.tooltip.solvent"));
        }
        tooltip.add(Component.translatable("behaviour.paintspray.infinite.tooltip.info"));
        tooltip.add(Component.translatable("behaviour.paintspray.infinite.tooltip.info_1"));
        tooltip.add(Component.translatable("behaviour.paintspray.infinite.tooltip.info_2"));
    }

    public static void setColor(ItemStack stack, @Nullable DyeColor color) {
        if (color == null) {
            stack.getOrCreateTag().putInt("color", -1);
        } else {
            stack.getOrCreateTag().putInt("color", color.ordinal());
        }
    }

    @Nullable
    public static DyeColor getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("color") || tag.getInt("color") == -1) {
            return null;
        }
        int ordinal = tag.getInt("color");
        DyeColor[] colors = DyeColor.values();
        if (ordinal >= 0 && ordinal < colors.length) {
            return colors[ordinal];
        }
        return null;
    }

    private static void handleBlocks(BlockPos start, @Nullable DyeColor color, int limit, Level level) {
        var collected = BreadthFirstBlockSearch
                .conditionalBlockPosSearch(start,
                        (parent, child) -> parent == null ||
                                level.getBlockState(child).is(level.getBlockState(parent).getBlock()),
                        limit, limit * 6);
        for (var pos : collected) {
            tryPaintBlock(level, pos, color);
        }
    }

    private static boolean handleSpecialBlockEntities(BlockEntity first, @Nullable DyeColor color, int limit,
                                                      Level level, Player player) {
        if (GTCEu.Mods.isAE2Loaded() && first instanceof IColorableBlockEntity) {
            var collected = BreadthFirstBlockSearch.conditionalSearch(
                    IColorableBlockEntity.class,
                    (IColorableBlockEntity) first,
                    first.getLevel(),
                    be -> ((BlockEntity) be).getBlockPos(),
                    (parent, child, dir) -> {
                        if (parent == null) return true;
                        return parent.getColor() == child.getColor();
                    },
                    limit,
                    limit * 6);

            AEColor ae2Color = color == null ?
                    AEColor.TRANSPARENT :
                    AEColor.values()[color.ordinal()];

            for (IColorableBlockEntity colorable : collected) {
                if (colorable.getColor() != ae2Color) {
                    colorable.recolourBlock(null, ae2Color, player);
                }
            }
            return true;
        }

        else if (first instanceof IPipeNode pipe) {
            var collected = BreadthFirstBlockSearch.conditionalSearch(IPipeNode.class, pipe,
                    first.getLevel(), IPipeNode::getPipePos,
                    gtPipePredicate, limit, limit * 6);
            paintPaintables(collected, color);
            return true;
        } else if (first instanceof IPaintable paintable) {
            var collected = BreadthFirstBlockSearch.conditionalSearch(IPaintable.class, paintable,
                    first.getLevel(), p -> ((BlockEntity) p).getBlockPos(),
                    paintablePredicate, limit, limit * 6);
            paintPaintables(collected, color);
            return true;
        }

        else if (first instanceof ShulkerBoxBlockEntity shulkerBox) {
            var tag = shulkerBox.saveWithoutMetadata();
            var pos = first.getBlockPos();
            recolorBlockNoState(SHULKER_BOX_MAP, color, level, pos, Blocks.SHULKER_BOX);
            if (level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity newShulker) {
                newShulker.load(tag);
            }
            return true;
        }

        return false;
    }

    private static <T extends IPaintable> void paintPaintables(Set<T> paintables, @Nullable DyeColor color) {
        for (var c : paintables) {
            paintPaintable(c, color);
        }
    }

    private static void tryPaintBlock(Level level, BlockPos pos, @Nullable DyeColor color) {
        var blockState = level.getBlockState(pos);
        var block = blockState.getBlock();
        if (color == null) {
            tryStripBlockColor(level, pos, block);
            return;
        }
        if (!recolorBlockState(level, pos, color)) {
            tryPaintSpecialBlock(level, pos, block, color);
        }
    }

    private static void tryPaintSpecialBlock(Level world, BlockPos pos, Block block, @Nullable DyeColor color) {
        if (block.defaultBlockState().is(Tags.Blocks.GLASS)) {
            if (recolorBlockNoState(GLASS_MAP, color, world, pos, Blocks.GLASS)) {
                return;
            }
        }
        if (block.defaultBlockState().is(Tags.Blocks.GLASS_PANES)) {
            if (recolorBlockNoState(GLASS_PANE_MAP, color, world, pos, Blocks.GLASS_PANE)) {
                return;
            }
        }
        if (block.defaultBlockState().is(BlockTags.TERRACOTTA)) {
            if (recolorBlockNoState(TERRACOTTA_MAP, color, world, pos, Blocks.TERRACOTTA)) {
                return;
            }
        }
        if (block.defaultBlockState().is(BlockTags.WOOL)) {
            if (recolorBlockNoState(WOOL_MAP, color, world, pos, null)) {
                return;
            }
        }
        if (block.defaultBlockState().is(BlockTags.WOOL_CARPETS)) {
            if (recolorBlockNoState(CARPET_MAP, color, world, pos, null)) {
                return;
            }
        }
        if (block.defaultBlockState().is(CustomTags.CONCRETE_BLOCK)) {
            if (recolorBlockNoState(CONCRETE_MAP, color, world, pos, null)) {
                return;
            }
        }
        if (block.defaultBlockState().is(CustomTags.CONCRETE_POWDER_BLOCK)) {
            recolorBlockNoState(CONCRETE_POWDER_MAP, color, world, pos, null);
        }
    }

    private static void paintPaintable(IPaintable paintable, DyeColor color) {
        if (color == null) {
            if (!paintable.isPainted()) {
                return;
            }
            paintable.setPaintingColor(IPaintable.UNPAINTED_COLOR);
        } else if (paintable.getPaintingColor() != color.getMapColor().col) {
            paintable.setPaintingColor(color.getMapColor().col);
        }
    }

    private static boolean recolorBlockNoState(Map<DyeColor, Block> map, @Nullable DyeColor color,
                                               Level level, BlockPos pos, Block defaultBlock) {
        Block newBlock = map.getOrDefault(color, defaultBlock);
        if (newBlock == Blocks.AIR) newBlock = defaultBlock;

        BlockState old = level.getBlockState(pos);
        if (newBlock != null && newBlock != old.getBlock()) {
            BlockState state = newBlock.defaultBlockState();
            for (Property property : old.getProperties()) {
                if (!state.hasProperty(property)) continue;
                state.setValue(property, old.getValue(property));
            }
            level.setBlockAndUpdate(pos, state);
            return true;
        }
        return false;
    }

    private static void tryStripBlockColor(Level world, BlockPos pos, Block block) {
        if (block instanceof StainedGlassBlock) {
            world.setBlockAndUpdate(pos, Blocks.GLASS.defaultBlockState());
            return;
        }
        if (block instanceof StainedGlassPaneBlock) {
            world.setBlockAndUpdate(pos, Blocks.GLASS_PANE.defaultBlockState());
            return;
        }
        if (block.defaultBlockState().is(BlockTags.TERRACOTTA) && block != Blocks.TERRACOTTA) {
            world.setBlockAndUpdate(pos, Blocks.TERRACOTTA.defaultBlockState());
            return;
        }
        if (block.defaultBlockState().is(BlockTags.WOOL) && block != Blocks.WHITE_WOOL) {
            world.setBlockAndUpdate(pos, Blocks.WHITE_WOOL.defaultBlockState());
            return;
        }
        if (block.defaultBlockState().is(BlockTags.WOOL_CARPETS) && block != Blocks.WHITE_CARPET) {
            world.setBlockAndUpdate(pos, Blocks.WHITE_CARPET.defaultBlockState());
            return;
        }
        if (block.defaultBlockState().is(CustomTags.CONCRETE_BLOCK) && block != Blocks.WHITE_CONCRETE) {
            world.setBlockAndUpdate(pos, Blocks.WHITE_CONCRETE.defaultBlockState());
            return;
        }
        if (block.defaultBlockState().is(CustomTags.CONCRETE_POWDER_BLOCK) && block != Blocks.WHITE_CONCRETE_POWDER) {
            world.setBlockAndUpdate(pos, Blocks.WHITE_CONCRETE_POWDER.defaultBlockState());
            return;
        }

        BlockState state = world.getBlockState(pos);
        for (Property prop : state.getProperties()) {
            if (prop.getValueClass() == DyeColor.class) {
                BlockState defaultState = block.defaultBlockState();
                DyeColor defaultColor = DyeColor.WHITE;
                try {
                    defaultColor = (DyeColor) defaultState.getValue(prop);
                } catch (IllegalArgumentException ignored) {}
                recolorBlockState(world, pos, defaultColor);
                return;
            }
        }
    }

    private static boolean recolorBlockState(Level level, BlockPos pos, DyeColor color) {
        BlockState state = level.getBlockState(pos);
        for (Property property : state.getProperties()) {
            if (property.getValueClass() == DyeColor.class) {
                level.setBlockAndUpdate(pos, state.setValue(property, color));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onEntitySwing(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return false;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        return false;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack,
                                                           @NotNull Player player,
                                                           @NotNull LivingEntity interactionTarget,
                                                           @NotNull InteractionHand hand) {
        return InteractionResult.PASS;
    }
}
