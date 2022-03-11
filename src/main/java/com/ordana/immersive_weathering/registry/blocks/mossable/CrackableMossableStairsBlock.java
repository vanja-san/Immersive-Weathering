package com.ordana.immersive_weathering.registry.blocks.mossable;

import com.ordana.immersive_weathering.registry.blocks.crackable.CrackSpreader;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CrackableMossableStairsBlock extends MossableStairsBlock implements CrackableMossable {

    private final Supplier<Item> brickItem;
    private final CrackLevel crackLevel;

    public CrackableMossableStairsBlock(MossLevel mossLevel, CrackLevel crackLevel, Supplier<Item> brickItem, Supplier<Block> baseBlockState, AbstractBlock.Settings settings) {
        super(mossLevel, baseBlockState, settings);
        this.crackLevel = crackLevel;
        this.brickItem = brickItem;
    }

    @Override
    public CrackSpreader getCrackSpreader() {
        return CrackSpreader.INSTANCE;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld serverLevel, BlockPos pos, Random random) {
        float weatherChance = 0.1f;
        if (random.nextFloat() < weatherChance) {
            boolean isMoss = this.getMossSpreader().getWanderWeatheringState(true, pos, serverLevel);
            Optional<BlockState> opt = Optional.empty();
            if(isMoss) {
                opt = this.getNextMossy(state);
            } else if(this.getCrackSpreader().getWanderWeatheringState(true, pos, serverLevel)){
                opt = this.getNextCracked(state);
            }
            BlockState newState = opt.orElse(state.with(WEATHERABLE,false));
            serverLevel.setBlockState(pos, newState);
        }
    }


    @Override
    public Item getRepairItem(BlockState state) {
        return brickItem.get();
    }

    @Override
    public CrackLevel getCrackLevel() {
        return crackLevel;
    }

}