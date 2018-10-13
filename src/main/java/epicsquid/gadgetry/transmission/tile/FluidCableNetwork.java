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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidCableNetwork extends CableNetwork {
  public FluidStack fluid = null;
  public int sharedCapacity = 0;
  public static final String TYPE_FLUID = "fluid";

  public FluidCableNetwork(CableWorldData data) {
    super(data);
    type = TYPE_FLUID;
  }

  public FluidCableNetwork(CableWorldData data, int id) {
    super(data, id);
    type = TYPE_FLUID;
  }

  @Override
  public boolean needsDistributionTick() {
    return fluid != null;
  }

  @Override
  public void processUpdateFor(TileCable t) {
    super.processUpdateFor(t);
    sharedCapacity = 16000;
  }

  public void distribute() {
    List<TileEntity> outputs = new ArrayList<TileEntity>();
    List<EnumFacing> outputFaces = new ArrayList<EnumFacing>();
    for (Pair<TileEntity, EnumFacing> p : connections) {
      if (p.first().hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, p.second())
          && p.first().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, p.second()).fill(fluid.copy(), false) > 0) {
        outputs.add(p.first());
        outputFaces.add(p.second());
      }
    }
    if (outputs.size() > 0) {
      int toEach = (int) (fluid.amount / outputs.size());
      int remainder = fluid.amount - (toEach * outputs.size());

      for (int i = 0; i < outputs.size(); i++) {
        IFluidHandler e = outputs.get(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, outputFaces.get(i));
        int toSend = toEach + (i < remainder ? 1 : 0);
        int sent = e.fill(new FluidStack(fluid.getFluid(), toSend), false);
        if (sent > 0) {
          e.fill(new FluidStack(fluid.getFluid(), sent), true);
          fluid.amount -= sent;
          outputs.get(i).markDirty();
          data.markDirty();
        }
      }
    }
    if (fluid.amount == 0) {
      fluid = null;
    }
  }

  public boolean canInsert(FluidStack s) {
    int counter = 0;
    if (fluid != null && s != null && fluid.getFluid() != s.getFluid()) {
      return false;
    }
    for (Pair<TileEntity, EnumFacing> p : connections) {
      if (p.first() != null && p.first().hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, p.second())
          && p.first().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, p.second()).fill(s.copy(), false) > 0) {
        counter++;
      }
    }
    return counter > 0;
  }

  public FluidStack getFluid() {
    return fluid;
  }

  public int getSharedCapacity() {
    return sharedCapacity;
  }

  public void readFromNBT(NBTTagCompound tag) {
    if (tag.hasKey("fluid")) {
      fluid = fluid.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
    }
    sharedCapacity = tag.getInteger("capacity");
    super.readFromNBT(tag);
  }

  public NBTTagCompound writeToNBT() {
    NBTTagCompound tag = super.writeToNBT();
    if (fluid != null) {
      tag.setTag("fluid", fluid.writeToNBT(new NBTTagCompound()));
    }
    tag.setInteger("capacity", sharedCapacity);
    return tag;
  }
}
