package com.ordana.immersive_weathering.registry.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class WallRootsBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0D, 4.0D, 5.0D, 16.0D, 12.0D, 16.0D), Direction.SOUTH, Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 11.0D), Direction.WEST, Block.box(5.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D), Direction.EAST, Block.box(0.0D, 4.0D, 0.0D, 11.0D, 12.0D, 16.0D)));

    public WallRootsBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState p_49219_, BlockGetter p_49220_, BlockPos p_49221_, CollisionContext p_49222_) {
        return SHAPES.get(p_49219_.getValue(FACING));
    }

    @Override
    public BlockState rotate(BlockState p_49207_, Rotation p_49208_) {
        return p_49207_.setValue(FACING, p_49208_.rotate(p_49207_.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState p_49204_, Mirror p_49205_) {
        return p_49204_.rotate(p_49205_.getRotation(p_49204_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos1) {
        return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(levelAccessor, pos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        BlockState blockstate = levelReader.getBlockState(blockpos);
        return blockstate.isFaceSturdy(levelReader, blockpos, direction) &&
                blockstate.isFertile(levelReader, pos);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = super.getStateForPlacement(context);
        LevelReader levelreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction[] directions = context.getNearestLookingDirections();

        for (Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                blockstate = blockstate.setValue(FACING, direction.getOpposite());
                if (blockstate.canSurvive(levelreader, blockpos)) {
                    return blockstate;
                }
            }
        }

        return null;
    }
}
