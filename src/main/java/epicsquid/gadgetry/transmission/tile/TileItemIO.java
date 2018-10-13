package epicsquid.gadgetry.transmission.tile;

import epicsquid.gadgetry.core.EventManager;
import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.core.util.InventoryUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileItemIO extends TileModular implements ITickable {

  public static final String INVENTORY = "INVENTORY";
  public EnumFacing face = null;

  public TileItemIO() {
    addModule(new ModuleInventory(INVENTORY, this, 13, "item_io", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }) {
      @Override
      public boolean isItemValidForSlot(int index, ItemStack stack) {
        boolean filtered = false;
        if (index >= 0 && index < 9) {
          filtered = false;
          for (int i = 9; i < 13; i++) {
            if (!((IInventory) this).getStackInSlot(i).isEmpty()) {
              filtered = true;
            }
          }
          if (filtered) {
            for (int i = 9; i < 13; i++) {
              ItemStack s = ((IInventory) this).getStackInSlot(i);
              if (RecipeBase.stackMatches(stack, s)) {
                filtered = false;
              }
            }
          }
        }
        return predicates.get(index).test(stack) && !filtered;
      }
    }.setSlotPredicate(0, new PredicateTrue()).setSlotPredicate(1, new PredicateTrue()).setSlotPredicate(2, new PredicateTrue())
        .setSlotPredicate(3, new PredicateTrue()).setSlotPredicate(4, new PredicateTrue()).setSlotPredicate(5, new PredicateTrue())
        .setSlotPredicate(6, new PredicateTrue()).setSlotPredicate(7, new PredicateTrue()).setSlotPredicate(8, new PredicateTrue())
        .setSlotPredicate(9, new PredicateTrue()).setSlotPredicate(10, new PredicateTrue()).setSlotPredicate(11, new PredicateTrue())
        .setSlotPredicate(12, new PredicateTrue()));
    canModifyIO = false;
    config.setAllIO(FaceIO.NEUTRAL);
    config.setAllModules(INVENTORY);
  }

  @Override
  public void update() {
    if (face == null) {
      IBlockState state = world.getBlockState(getPos());
      face = state.getValue(BlockTEFacing.facing);
      config.setIO(face, FaceIO.OUT);
      config.setIO(face.getOpposite(), FaceIO.IN);
      markDirty();
    }
    if (!world.isRemote) {
      ModuleInventory moduleInv = (ModuleInventory) modules.get(INVENTORY);
      IInventory inv = ((IInventory) moduleInv);
      if (inv != null) {
        TileEntity t = world.getTileEntity(getPos().offset(face, -1));
        if (t != null && t.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)) {
          IItemHandler handler = t.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
          for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack s = handler.extractItem(i, 64, true);
            if (!s.isEmpty()) {
              int inserted = InventoryUtil
                  .attemptInsert(s.copy(), (IItemHandler) moduleInv.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite(), this),
                      true);
              if (inserted > 0) {
                ItemStack toInsert = s.copy();
                toInsert.setCount(1);
                InventoryUtil.attemptInsert(toInsert, moduleInv.inventory, false);
                handler.extractItem(i, 1, false);
                markDirty();
                t.markDirty();
                EventManager.markForUpdate(t.getPos(), t);
                break;
              }
            }
          }
        }
      }
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);
    if (nbt.hasKey("face")) {
      face = EnumFacing.getFront(nbt.getInteger("face"));
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    super.writeToNBT(nbt);
    if (face != null) {
      nbt.setInteger("face", face.getIndex());
    }
    return nbt;
  }
}
