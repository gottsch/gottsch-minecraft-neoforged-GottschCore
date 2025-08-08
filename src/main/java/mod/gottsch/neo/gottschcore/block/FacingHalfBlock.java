package mod.gottsch.neo.gottschcore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Half;

/**
 * @author by Mark Gottschling on 5/7/2025
 */
public class FacingHalfBlock extends HalfBlock implements IFacingBlock {

    public FacingHalfBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        return super.getStateForPlacement(context)
                .setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double) pos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP);
    }

    /** @deprecated */
    @Deprecated
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.setValue(FACING, rot.rotate(this.getFacing(state)));
    }

    public Direction getFacing(BlockState state) {
        return (Direction) state.getValue(FACING);
    }
}
