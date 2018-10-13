package epicsquid.gadgetry.transmission.tile;

import epicsquid.gadgetry.core.lib.tile.CableNetwork;
import epicsquid.gadgetry.core.lib.tile.CableWorldData;
import epicsquid.gadgetry.core.lib.tile.TileCable;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import net.minecraftforge.energy.EnergyStorage;

public class TileEnergyCable extends TileCable {

  public static final String ENERGY = "energy";

  public TileEnergyCable() {
    super(EnergyCableNetwork.TYPE_ENERGY);
    addModule(new ModuleEnergyCable(ENERGY, this, 1600, 1600, 1600));
    config.setAllIO(FaceIO.NEUTRAL);
    config.setAllModules(ENERGY);
  }

  @Override
  public CableNetwork constructNetwork(CableWorldData data) {
    return new EnergyCableNetwork(data);
  }

  public class ModuleEnergyCable extends ModuleEnergy {
    public ModuleEnergyCable(String name, TileModular tile, int capacity, int receiveLimit, int giveLimit) {
      super(name, tile, capacity, receiveLimit, giveLimit);
    }

    @Override
    public EnergyStorage constructBattery(int capacity, int maxIn, int maxOut, int energy) {
      return new EnergyStorage(capacity, maxIn, maxOut, energy) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
          if (!(network instanceof EnergyCableNetwork)) {
            return 0;
          }
          EnergyCableNetwork n = (EnergyCableNetwork) network;
          if (n.hasOutputs) {
            int oldAmount = n.storedPower;
            int newAmount = Math.min(n.sharedCapacity, n.storedPower + maxReceive);
            int sent = newAmount - oldAmount;
            if (!simulate) {
              n.storedPower += sent;
            }
            return sent;
          }
          return 0;
        }
      };
    }
  }
}
