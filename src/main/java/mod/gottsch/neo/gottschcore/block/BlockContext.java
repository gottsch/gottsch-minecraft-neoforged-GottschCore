package mod.gottsch.neo.gottschcore.block;


import mod.gottsch.neo.gottschcore.spatial.Coords;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * 
 * @author Mark Gottschling on Oct 19, 2022
 *
 */
public class BlockContext {
	private final ICoords coords;
	private final BlockState state;
	
	/**
	 * 
	 * @param world
	 * @param coords
	 */
	public BlockContext(CommonLevelAccessor level, ICoords coords) {
		this.coords = coords;
		this.state = level.getBlockState(coords.toPos());
	}
	
	public BlockContext(CommonLevelAccessor level, BlockPos pos) {
		this(level, new Coords(pos));
	}
	
	public BlockContext(ICoords coords, BlockState state) {
		this.coords = coords;
		this.state = state;
	}
	
	public boolean hasState() {
		if (state == null)
			return false;
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public Block toBlock() {
		if (state != null) {
			return state.getBlock();
		}
		return null;
	}
	
	public static Block toBlock(final Level level, final ICoords coords) {
		BlockState blockState = level.getBlockState(coords.toPos());
		if (blockState != null)
			return blockState.getBlock();
		return null;
	}
	
	public boolean equalsBlock(Block block) {
		if (state.getBlock() == block)
			return true;
		return false;
	}

	public boolean isAir() {
		return state.isAir();
	}

	/**
	 * Wrapper for Material.isReplaceable();
	 * 
	 * @return
	 */
	public boolean isReplaceable() {
		return state.canBeReplaced();
	}

	/**
	 * Wrapper for Material.isSolid()
	 * 
	 * @return
	 */
	public boolean isSolid() {
		return state.isSolid();
	}
	
	public boolean isFluid() {
		return !state.getFluidState().isEmpty();
	}
	
	public boolean isBurning() {
		return state.isBurning((BlockGetter) null, this.coords.toPos());
	}
	
	public boolean isLeaves() {
		return state.getBlock().defaultMapColor() == MapColor.PLANT &&
				state.ignitedByLava() &&
				state.getPistonPushReaction() == PushReaction.DESTROY;
	}
	
	public ICoords getCoords() {
		return coords;
	}

	public BlockState getState() {
		return state;
	}
}
