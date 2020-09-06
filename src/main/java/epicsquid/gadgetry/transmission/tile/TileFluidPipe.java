package epicsquid.gadgetry.transmission.tile;

import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.tile.CableNetwork;
import epicsquid.gadgetry.core.lib.tile.CableWorldData;
import epicsquid.gadgetry.core.lib.tile.TileCable;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import net.minecraftforge.fluids.FluidStack;

public class TileFluidPipe extends TileCable {

  public static final String FLUID = "fluid";

  //In lieu of actually fixing anything in any major way, just simply going to up capacities here. original values was 125. New is 500.
  //The base class this is extending needs reworking for compatibility issues.
  //Ultimately we want a pipe that will push fluid through based on some sort of pressure system so that connects and volume capacity are more dynamic.
  //the current model is a hard limit and more connections actually **reduces** flow if the network has too high fluid input. Which is silly. You made physics cry.
  public TileFluidPipe() {
    super(FluidCableNetwork.TYPE_FLUID);
    addModule(new ModuleFluidPipe(FLUID, this, 500));
    config.setAllIO(FaceIO.NEUTRAL);
    config.setAllModules(FLUID);
  }

  @Override
  public CableNetwork constructNetwork(CableWorldData data) {
    return new FluidCableNetwork(data);
  }

  public class ModuleFluidPipe extends ModuleFluid {

    public ModuleFluidPipe(String name, TileModular tile, int giveLimit) {
      super(name, tile, giveLimit);
      addTank(new ExtendedFluidTank(500, new PredicateTrue(), true) {
        @Override
        public int fill(FluidStack amount, boolean doFill) {
          if (!(network instanceof FluidCableNetwork)) {
            return 0;
          }
          FluidCableNetwork n = (FluidCableNetwork) network;
          if (n.canInsert(amount)) {
            if (n.fluid == null) {
              int sent = Math.min(n.sharedCapacity, Math.min(giveLimit, amount.amount));
              if (doFill) {
                n.fluid = new FluidStack(amount.getFluid(), sent);
              }
              return sent;
            } else {
              int oldAmount = n.fluid.amount;
              int newAmount = Math.min(n.sharedCapacity, n.fluid.amount + Math.min(giveLimit, amount.amount));
              int sent = newAmount - oldAmount;
              if (doFill) {
                n.fluid.amount += sent;
              }
              return sent;
            }
          } else {
            return 0;
          }
        }
      });
    }

  }
}
