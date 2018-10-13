package epicsquid.gadgetry.transmission.proxy;

import epicsquid.gadgetry.core.lib.tile.CableManager;
import epicsquid.gadgetry.core.network.PacketHandler;
import epicsquid.gadgetry.transmission.tile.EnergyCableNetwork;
import epicsquid.gadgetry.transmission.tile.FluidCableNetwork;
import epicsquid.gadgetry.transmission.tile.ItemCableNetwork;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
  public void preInit(FMLPreInitializationEvent event) {
    PacketHandler.registerMessages();
  }

  public void init(FMLInitializationEvent event) {
    CableManager.networkRegistry.put(EnergyCableNetwork.TYPE_ENERGY, EnergyCableNetwork.class);
    CableManager.networkRegistry.put(FluidCableNetwork.TYPE_FLUID, FluidCableNetwork.class);
    CableManager.networkRegistry.put(ItemCableNetwork.TYPE_ITEMS, ItemCableNetwork.class);
  }

  public void postInit(FMLPostInitializationEvent event) {
  }
}
