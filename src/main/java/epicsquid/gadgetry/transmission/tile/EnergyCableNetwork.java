package epicsquid.gadgetry.transmission.tile;

import java.util.ArrayList;
import java.util.List;

import akka.japi.Pair;
import epicsquid.gadgetry.core.lib.tile.CableNetwork;
import epicsquid.gadgetry.core.lib.tile.CableWorldData;
import epicsquid.gadgetry.core.lib.tile.TileCable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyCableNetwork extends CableNetwork {
  public int storedPower = 0;
  public int sharedCapacity = 0;
  public boolean hasOutputs = false;

  public static final String TYPE_ENERGY = "energy";

  public EnergyCableNetwork(CableWorldData data) {
    super(data);
    type = TYPE_ENERGY;
  }

  public EnergyCableNetwork(CableWorldData data, int id) {
    super(data, id);
    type = TYPE_ENERGY;
  }

  @Override
  public boolean needsDistributionTick() {
    hasOutputs = false;
    for (Pair<TileEntity, EnumFacing> p : connections) {
      if (p.first().hasCapability(CapabilityEnergy.ENERGY, p.second()) && p.first().getCapability(CapabilityEnergy.ENERGY, p.second()).canReceive()
          && p.first().getCapability(CapabilityEnergy.ENERGY, p.second()).receiveEnergy(1, true) > 0) {
        hasOutputs = true;
      }
    }
    return storedPower > 0;
  }

  @Override
  public void processUpdateFor(TileCable t) {
    super.processUpdateFor(t);
    sharedCapacity = 160000;
  }

  public void distribute() {
    List<TileEntity> outputs = new ArrayList<TileEntity>();
    List<EnumFacing> outputFaces = new ArrayList<EnumFacing>();
    for (Pair<TileEntity, EnumFacing> p : connections) {
      if (p.first().hasCapability(CapabilityEnergy.ENERGY, p.second()) && p.first().getCapability(CapabilityEnergy.ENERGY, p.second()).canReceive()
          && p.first().getCapability(CapabilityEnergy.ENERGY, p.second()).receiveEnergy(1, true) > 0) {
        outputs.add(p.first());
        outputFaces.add(p.second());
      }
    }
    if (outputs.size() > 0) {
      int toEach = (int) (storedPower / outputs.size());
      int remainder = storedPower - (toEach * outputs.size());

      for (int i = 0; i < outputs.size(); i++) {
        IEnergyStorage e = outputs.get(i).getCapability(CapabilityEnergy.ENERGY, outputFaces.get(i));
        int toSend = toEach + (i < remainder ? 1 : 0);
        int sent = e.receiveEnergy(toSend, true);
        if (sent > 0) {
          e.receiveEnergy(sent, false);
          storedPower -= sent;
          outputs.get(i).markDirty();
          data.markDirty();
        }
      }
    }
  }

  public int getSharedPower() {
    return storedPower;
  }

  public int getSharedCapacity() {
    return sharedCapacity;
  }

  public void readFromNBT(NBTTagCompound tag) {
    storedPower = tag.getInteger("power");
    sharedCapacity = tag.getInteger("capacity");
    super.readFromNBT(tag);
  }

  public NBTTagCompound writeToNBT() {
    NBTTagCompound tag = super.writeToNBT();
    tag.setInteger("power", storedPower);
    tag.setInteger("capacity", sharedCapacity);
    return tag;
  }
}
