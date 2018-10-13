package epicsquid.gadgetry.transmission;

import java.util.ArrayList;

import epicsquid.gadgetry.core.block.BlockCable;
import epicsquid.gadgetry.core.lib.ELRegistry;
import epicsquid.gadgetry.core.lib.event.RegisterContentEvent;
import epicsquid.gadgetry.core.lib.event.RegisterGuiFactoriesEvent;
import epicsquid.gadgetry.core.lib.gui.GuiHandler;
import epicsquid.gadgetry.transmission.block.BlockIO;
import epicsquid.gadgetry.transmission.gui.GuiFactoryEnergyIO;
import epicsquid.gadgetry.transmission.gui.GuiFactoryFluidIO;
import epicsquid.gadgetry.transmission.gui.GuiFactoryItemIO;
import epicsquid.gadgetry.transmission.tile.TileEnergyCable;
import epicsquid.gadgetry.transmission.tile.TileEnergyIO;
import epicsquid.gadgetry.transmission.tile.TileFluidIO;
import epicsquid.gadgetry.transmission.tile.TileFluidPipe;
import epicsquid.gadgetry.transmission.tile.TileItemIO;
import epicsquid.gadgetry.transmission.tile.TileItemPipe;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GadgetryTransmissionContent {
  public static ArrayList<Item> items = new ArrayList<Item>();
  public static ArrayList<Block> blocks = new ArrayList<Block>();

  //public static Item //;

  public static Block energy_cable, fluid_pipe, item_pipe, energy_io, fluid_io, item_io;

  @SubscribeEvent
  public void registerContent(RegisterContentEvent event) {
    ELRegistry.setActiveMod(GadgetryTransmission.MODID, GadgetryTransmission.CONTAINER);
    event.addBlock(energy_cable = new BlockCable(Material.ROCK, SoundType.METAL, 1.0f, "energy_cable", TileEnergyCable.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryTransmission.tab));
    event.addBlock(fluid_pipe = new BlockCable(Material.ROCK, SoundType.METAL, 1.0f, "fluid_pipe", TileFluidPipe.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryTransmission.tab));
    event.addBlock(item_pipe = new BlockCable(Material.ROCK, SoundType.METAL, 1.0f, "item_pipe", TileItemPipe.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryTransmission.tab));
    event.addBlock(energy_io = new BlockIO(Material.ROCK, SoundType.METAL, 1.6f, "energy_io", TileEnergyIO.class).setOpacity(false).setHarvestReqs("pickaxe", 0)
        .setLightOpacity(0).setCreativeTab(GadgetryTransmission.tab));
    event.addBlock(fluid_io = new BlockIO(Material.ROCK, SoundType.METAL, 1.6f, "fluid_io", TileFluidIO.class).setOpacity(false).setHarvestReqs("pickaxe", 0)
        .setLightOpacity(0).setCreativeTab(GadgetryTransmission.tab));
    event.addBlock(item_io = new BlockIO(Material.ROCK, SoundType.METAL, 1.6f, "item_io", TileItemIO.class).setOpacity(false).setHarvestReqs("pickaxe", 0)
        .setLightOpacity(0).setCreativeTab(GadgetryTransmission.tab));

    for (Block b : blocks) {
      b.setCreativeTab(GadgetryTransmission.tab);
    }
    for (Item i : items) {
      i.setCreativeTab(GadgetryTransmission.tab);
    }
  }

  @SubscribeEvent
  public void onRegisterGuiFactories(RegisterGuiFactoriesEvent event) {
    GuiHandler.registerGui(new GuiFactoryEnergyIO());
    GuiHandler.registerGui(new GuiFactoryFluidIO());
    GuiHandler.registerGui(new GuiFactoryItemIO());
  }
}
