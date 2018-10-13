package epicsquid.gadgetry.transmission.recipe;

import epicsquid.gadgetry.core.lib.ELRegistry;
import epicsquid.gadgetry.transmission.GadgetryTransmission;
import epicsquid.gadgetry.transmission.GadgetryTransmissionContent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class TransmissionRecipeRegistry {
  public static ResourceLocation getRL(String s) {
    return new ResourceLocation(GadgetryTransmission.MODID + ":" + s);
  }

  public static void registerShaped(IForgeRegistry<IRecipe> registry, String name, ItemStack result, Object... ingredients) {
    registry.register(new ShapedOreRecipe(getRL(name), result, ingredients).setRegistryName(getRL(name)));
  }

  public static void registerShapedMirrored(IForgeRegistry<IRecipe> registry, String name, ItemStack result, Object... ingredients) {
    registry.register(new ShapedOreRecipe(getRL(name), result, ingredients).setMirrored(true).setRegistryName(getRL(name)));
  }

  public static void registerShapeless(IForgeRegistry<IRecipe> registry, String name, ItemStack result, Object... ingredients) {
    registry.register(new ShapelessOreRecipe(getRL(name), result, ingredients).setRegistryName(getRL(name)));
  }

  @SubscribeEvent
  public void onRecipeRegistry(RegistryEvent.Register<IRecipe> event) {
    ELRegistry.setActiveMod(GadgetryTransmission.MODID, GadgetryTransmission.CONTAINER);
    registerShaped(event.getRegistry(), "energy_cable", new ItemStack(GadgetryTransmissionContent.energy_cable, 1), "n", "n", "n", 'n', "nuggetRedmetal");
    registerShaped(event.getRegistry(), "item_pipe", new ItemStack(GadgetryTransmissionContent.item_pipe, 1), "n", "n", "n", 'n', "nuggetSteel");
    registerShaped(event.getRegistry(), "fluid_pipe", new ItemStack(GadgetryTransmissionContent.fluid_pipe, 1), "n", "n", "n", 'n', "nuggetIron");
    registerShaped(event.getRegistry(), "energy_io", new ItemStack(GadgetryTransmissionContent.energy_io, 1), " I ", "IRI", 'I', "ingotRedmetal", 'R',
        "dustRedstone");
    registerShaped(event.getRegistry(), "item_io", new ItemStack(GadgetryTransmissionContent.item_io, 1), " I ", "IRI", 'I', "ingotSteel", 'R', "dustRedstone");
    registerShaped(event.getRegistry(), "fluid_io", new ItemStack(GadgetryTransmissionContent.fluid_io, 1), " I ", "IRI", 'I', "ingotIron", 'R',
        "dustRedstone");
  }
}
