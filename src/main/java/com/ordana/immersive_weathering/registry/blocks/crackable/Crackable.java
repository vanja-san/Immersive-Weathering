package com.ordana.immersive_weathering.registry.blocks.crackable;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.ordana.immersive_weathering.registry.blocks.ModBlocks;
import com.ordana.immersive_weathering.registry.blocks.Weatherable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Supplier;

public interface Crackable extends Weatherable {

    Supplier<BiMap<Block, Block>> CRACK_LEVEL_INCREASES = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()

            .put(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS)
            .put(Blocks.BRICKS, ModBlocks.CRACKED_BRICKS.get())
            .put(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)
            .put(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS)
            .put(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS)
            .put(Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES)

            .put(Blocks.STONE_BRICK_SLAB, ModBlocks.CRACKED_STONE_BRICK_SLAB.get())
            .put(Blocks.BRICK_SLAB, ModBlocks.CRACKED_BRICK_SLAB.get())
            .put(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, ModBlocks.CRACKED_POLISHED_BLACKSTONE_BRICK_SLAB.get())
            .put(Blocks.NETHER_BRICK_SLAB, ModBlocks.CRACKED_NETHER_BRICK_SLAB.get())
            .put(Blocks.DEEPSLATE_BRICK_SLAB, ModBlocks.CRACKED_DEEPSLATE_BRICK_SLAB.get())
            .put(Blocks.DEEPSLATE_TILE_SLAB, ModBlocks.CRACKED_DEEPSLATE_TILE_SLAB.get())

            .put(Blocks.STONE_BRICK_STAIRS, ModBlocks.CRACKED_STONE_BRICK_STAIRS.get())
            .put(Blocks.BRICK_STAIRS, ModBlocks.CRACKED_BRICK_STAIRS.get())
            .put(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, ModBlocks.CRACKED_POLISHED_BLACKSTONE_BRICK_STAIRS.get())
            .put(Blocks.NETHER_BRICK_STAIRS, ModBlocks.CRACKED_NETHER_BRICK_STAIRS.get())
            .put(Blocks.DEEPSLATE_BRICK_STAIRS, ModBlocks.CRACKED_DEEPSLATE_BRICK_STAIRS.get())
            .put(Blocks.DEEPSLATE_TILE_STAIRS, ModBlocks.CRACKED_DEEPSLATE_TILE_STAIRS.get())

            .put(Blocks.STONE_BRICK_WALL, ModBlocks.CRACKED_STONE_BRICK_WALL.get())
            .put(Blocks.BRICK_WALL, ModBlocks.CRACKED_BRICK_WALL.get())
            .put(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, ModBlocks.CRACKED_POLISHED_BLACKSTONE_BRICK_WALL.get())
            .put(Blocks.NETHER_BRICK_WALL, ModBlocks.CRACKED_NETHER_BRICK_WALL.get())
            .put(Blocks.DEEPSLATE_BRICK_WALL, ModBlocks.CRACKED_DEEPSLATE_BRICK_WALL.get())
            .put(Blocks.DEEPSLATE_TILE_WALL, ModBlocks.CRACKED_DEEPSLATE_TILE_WALL.get())
            .build());

    //reverse map for reverse access in descending order
    Supplier<BiMap<Block, Block>> CRACK_LEVEL_DECREASES = Suppliers.memoize(() -> CRACK_LEVEL_INCREASES.get().inverse());


    //these can be removed if you want

    private static Optional<Block> getDecreasedCrackBlock(Block block) {
        return Optional.ofNullable(CRACK_LEVEL_DECREASES.get().get(block));
    }

    private static Block getUncrackedCrackBlock(Block block) {
        Block block2 = block;
        Block block3 = CRACK_LEVEL_DECREASES.get().get(block2);
        while (block3 != null) {
            block2 = block3;
            block3 = CRACK_LEVEL_DECREASES.get().get(block2);
        }
        return block2;
    }

    private static Optional<BlockState> getDecreasedCrackState(BlockState state) {
        return getDecreasedCrackBlock(state.getBlock()).map(block -> block.withPropertiesOf(state));
    }

    private static BlockState getUncrackedCrackState(BlockState state) {
        return getUncrackedCrackBlock(state.getBlock()).withPropertiesOf(state);
    }

    private static Optional<Block> getIncreasedCrackBlock(Block block) {
        return Optional.ofNullable(CRACK_LEVEL_INCREASES.get().get(block));
    }


    default Optional<BlockState> getNextCracked(BlockState state){
        return getIncreasedCrackBlock(state.getBlock()).map(block -> block.withPropertiesOf(state));

    };

    default Optional<BlockState> getPreviousCracked(BlockState state){
        return getIncreasedCrackBlock(state.getBlock()).map(block -> block.withPropertiesOf(state));

    }

    CrackSpreader getCrackSpreader();
    
    CrackLevel getCrackLevel();

    default boolean shouldWeather(BlockState state, BlockPos pos, Level level){
        return this.getCrackSpreader().canEventuallyWeather(state, pos, level);
    }
    Item getRepairItem(BlockState state);

    enum CrackLevel {
        UNCRACKED,
        CRACKED;
    }
}
