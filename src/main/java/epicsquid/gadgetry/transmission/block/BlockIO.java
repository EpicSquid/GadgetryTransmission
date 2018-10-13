package epicsquid.gadgetry.transmission.block;

import epicsquid.gadgetry.core.block.BlockTEFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

public class BlockIO extends BlockTEFacing {
  AxisAlignedBB AABB_X = new AxisAlignedBB(0, 0.125, 0.125, 1, 0.875, 0.875);
  AxisAlignedBB AABB_Y = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 1, 0.875);
  AxisAlignedBB AABB_Z = new AxisAlignedBB(0.125, 0.125, 0, 0.875, 0.875, 1);

  public BlockIO(Material mat, SoundType type, float hardness, String name, Class<? extends TileEntity> teClass) {
    super(mat, type, hardness, name, teClass);
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
    EnumFacing face = state.getValue(BlockTEFacing.facing);
    Vec3i v = face.getDirectionVec();
    if (v.getX() != 0) {
      return AABB_X;
    } else if (v.getY() != 0) {
      return AABB_Y;
    } else {
      return AABB_Z;
    }
  }
}
