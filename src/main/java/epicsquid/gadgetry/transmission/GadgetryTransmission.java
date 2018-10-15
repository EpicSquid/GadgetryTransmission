package epicsquid.gadgetry.transmission;

import epicsquid.gadgetry.transmission.proxy.CommonProxy;
import epicsquid.gadgetry.transmission.recipe.TransmissionRecipeRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = GadgetryTransmission.MODID, version = GadgetryTransmission.VERSION, name = GadgetryTransmission.NAME, dependencies = "required-before:gadgetrycore")
public class GadgetryTransmission {
  public static final String MODID = "gadgetrytransmission";
  public static final String VERSION = "@VERSION@";
  public static final String NAME = "Gadgetry: Transmission";

  @SidedProxy(clientSide = "epicsquid.gadgetry.transmission.proxy.ClientProxy", serverSide = "epicsquid.gadgetry.transmission.proxy.CommonProxy") public static CommonProxy proxy;

  public static ModContainer CONTAINER;

  @Instance public static GadgetryTransmission INSTANCE;

  public static CreativeTabs tab = new CreativeTabs("gadgetrytransmission") {
    @Override
    public String getTabLabel() {
      return "gadgetrytransmission";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
      return new ItemStack(GadgetryTransmissionContent.energy_cable, 1);
    }
  };

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    CONTAINER = Loader.instance().activeModContainer();
    MinecraftForge.EVENT_BUS.register(new GadgetryTransmissionContent());
    MinecraftForge.EVENT_BUS.register(new TransmissionRecipeRegistry());
    proxy.preInit(event);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    proxy.init(event);
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit(event);
  }
}
