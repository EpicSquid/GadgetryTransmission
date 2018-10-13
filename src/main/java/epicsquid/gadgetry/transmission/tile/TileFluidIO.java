package epicsquid.gadgetry.transmission.tile;

import epicsquid.gadgetry.core.EventManager;
import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TileFluidIO extends TileModular implements ITickable {

  public static final String TANK = "tank";
  public static final String INVENTORY = "inventory";
  public EnumFacing face = null;

  public TileFluidIO() {
    addModule(new ModuleFluid(TANK, this, 125).addTank(new ExtendedFluidTank(2000, new PredicateTrue(), true) {
      @Override
      public int fill(FluidStack stack, boolean simulate) {
        if (stack != null) {
          ModuleInventory minv = (ModuleInventory) TileFluidIO.this.modules.get(INVENTORY);
          IInventory inv = (IInventory) minv;
          boolean filtered = false;
          for (int i = 0; i < 4; i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
              filtered = true;
            }
          }
          if (filtered) {
            for (int i = 0; i < 4; i++) {
              ItemStack s = inv.getStackInSlot(i);
              Fluid f = null;
              if (s.getItem() == Items.WATER_BUCKET) {
                f = FluidRegistry.WATER;
              }
              if (s.getItem() == Items.LAVA_BUCKET) {
                f = FluidRegistry.LAVA;
              }
              if (s.getItem() instanceof UniversalBucket) {
                FluidStack t = ((UniversalBucket) s.getItem()).getFluid(s);
                if (t != null) {
                  f = t.getFluid();
                }
              }
              if (f == stack.getFluid()) {
                filtered = false;
              }
            }
          }
          if (!filtered) {
            TileFluidIO.this.markDirty();
            return super.fill(stack, simulate);
          } else {
            return 0;
          }
        }
        return 0;
      }
    }));
    addModule(new ModuleInventory(INVENTORY, this, 4, "item_io", new int[] {}, new int[] {}).setSlotPredicate(0, new PredicateTrue())
        .setSlotPredicate(1, new PredicateTrue()).setSlotPredicate(2, new PredicateTrue()).setSlotPredicate(3, new PredicateTrue()));
    canModifyIO = false;
    config.setAllIO(FaceIO.NEUTRAL);
    config.setAllModules(TANK);
  }

  @Override
  public void update() {
    if (face == null) {
      IBlockState state = world.getBlockState(getPos());
      face = state.getValue(BlockTEFacing.facing);
      config.setIO(face, FaceIO.OUT);
      config.setIO(face.getOpposite(), FaceIO.IN);
      markDirty();
    }
    if (!world.isRemote) {
      ModuleFluid tank = (ModuleFluid) modules.get(TANK);
      FluidStack stack = tank.tanks.get(0).getFluid();
      if (stack == null || stack != null && stack.amount < tank.tanks.get(0).getCapacity()) {
        TileEntity t = world.getTileEntity(getPos().offset(face, -1));
        if (t != null && t.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face)) {
          IFluidHandler handler = t.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face);
          IFluidTankProperties[] tanks = handler.getTankProperties();
          for (IFluidTankProperties p : tanks) {
            if (p.getContents() != null) {
              FluidStack s = p.getContents().copy();
              s.amount = Math.max(s.amount, tank.receiveLimit);
              FluidStack drained = handler.drain(s, true);
              if (drained != null && drained.amount > 0) {
                tank.tanks.get(0).fill(drained, true);
                handler.drain(drained, true);
                markDirty();
                t.markDirty();
                EventManager.markForUpdate(t.getPos(), t);
                break;
              }
            }
          }
        }
      }
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);
    if (nbt.hasKey("face")) {
      face = EnumFacing.getFront(nbt.getInteger("face"));
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    super.writeToNBT(nbt);
    if (face != null) {
      nbt.setInteger("face", face.getIndex());
    }
    return nbt;
  }
}
