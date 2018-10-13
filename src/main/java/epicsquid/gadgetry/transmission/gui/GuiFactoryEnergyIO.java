package epicsquid.gadgetry.transmission.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.ElementEnergyBar;
import epicsquid.gadgetry.core.lib.gui.ElementToggleIOButton;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.gui.IGuiFactory;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.transmission.tile.TileEnergyIO;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryEnergyIO implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).tryAddSlot(0, 80, 42).initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementEnergyBar(80, 10, (ModuleEnergy) ((TileEnergyIO) t).modules.get(TileEnergyIO.BATTERY)))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TileEnergyIO) t).BATTERY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileEnergyIO.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 80, 40).initPlayerInventory(player.inventory, 0, 0);
  }

}
