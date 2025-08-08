package mod.gottsch.neo.gottschcore.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;

/**
 * @author by Mark Gottschling on 5/7/2025
 */
public interface IHalfBlock {
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    public Half getHalf(BlockState state);
}
