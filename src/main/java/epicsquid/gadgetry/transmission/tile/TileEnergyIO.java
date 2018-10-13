package epicsquid.gadgetry.transmission.tile;

import epicsquid.gadgetry.core.EventManager;
import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEnergyIO extends TileModular implements ITickable {

  public static final String BATTERY = "battery";
  public EnumFacing face = null;

  public TileEnergyIO() {
    addModule(new ModuleEnergy(BATTERY, this, 16000, 320, 320));
    canModifyIO = false;
    config.setAllIO(FaceIO.NEUTRAL);
    config.setAllModules(BATTERY);
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
      ModuleEnergy energy = (ModuleEnergy) modules.get(BATTERY);
      if (energy.battery.getEnergyStored() < energy.battery.getMaxEnergyStored()) {
        TileEntity t = world.getTileEntity(getPos().offset(face, -1));
        if (t != null && t.hasCapability(CapabilityEnergy.ENERGY, face)) {
          IEnergyStorage storage = t.getCapability(CapabilityEnergy.ENERGY, face);
          if (storage.getEnergyStored() > 0 && storage.canExtract()) {
            int received = energy.battery.receiveEnergy(storage.getEnergyStored(), true);
            if (received > 0) {
              energy.battery.receiveEnergy(received, false);
              storage.extractEnergy(received, false);
              markDirty();
              t.markDirty();
              EventManager.markForUpdate(t.getPos(), t);
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
