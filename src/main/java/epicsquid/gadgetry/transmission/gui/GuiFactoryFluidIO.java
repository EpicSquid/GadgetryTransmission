package epicsquid.gadgetry.transmission.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.ElementFluidTank;
import epicsquid.gadgetry.core.lib.gui.ElementToggleIOButton;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.gui.IGuiFactory;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.transmission.tile.TileFluidIO;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryFluidIO implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).tryAddSlot(0, 10, 23).tryAddSlot(1, 28, 23).tryAddSlot(2, 10, 41).tryAddSlot(3, 28, 41)
        .initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementFluidTank(80, 10, ((ModuleFluid) ((TileFluidIO) t).modules.get(TileFluidIO.TANK)).tanks.get(0).getTankProperties()[0]))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TileFluidIO) t).TANK));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileFluidIO.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 10, 23).tryAddSlot(1, 28, 23).tryAddSlot(2, 10, 41).tryAddSlot(3, 28, 41)
        .initPlayerInventory(player.inventory, 0, 0);
  }

}
