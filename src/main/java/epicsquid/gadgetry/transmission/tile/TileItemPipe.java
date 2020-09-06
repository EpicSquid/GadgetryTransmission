package epicsquid.gadgetry.transmission.tile;

import epicsquid.gadgetry.core.lib.inventory.InventoryHandler;
import epicsquid.gadgetry.core.lib.tile.CableNetwork;
import epicsquid.gadgetry.core.lib.tile.CableWorldData;
import epicsquid.gadgetry.core.lib.tile.TileCable;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import net.minecraft.item.ItemStack;

//This mostly works, but bugs out every now and then. something wrong with the base networking syc maybe?
public class TileItemPipe extends TileCable {

  public static final String ITEM = "item";

  public TileItemPipe() {
    super(ItemCableNetwork.TYPE_ITEMS);
    addModule(new ModuleItemPipe(ITEM, this, 1));
    config.setAllIO(FaceIO.NEUTRAL);
    config.setAllModules(ITEM);
  }

  @Override
  public CableNetwork constructNetwork(CableWorldData data) {
    return new ItemCableNetwork(data);
  }

  public class ModuleItemPipe extends ModuleInventory {

    public ModuleItemPipe(String name, TileModular tile, int capacity) {
      super(name, tile, capacity, "pipe", new int[] { 0 }, new int[] { 0 });
      this.inventory = new InventoryHandler(capacity, this) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
          if (!(network instanceof ItemCableNetwork)) {
            return stack;
          }
          if (stack.isEmpty()) {
            return stack;
          }
          ItemCableNetwork n = (ItemCableNetwork) network;
          int insertable = n.canInsert(stack);
          if (insertable > 0) {
            int sendable = insertable;
            if (!simulate) {
              for (int i = 0; i < n.items.size(); i++) {
                if (ItemStack.areItemStacksEqual(stack, n.items.get(i))) {
                  int sent = Math.min(n.items.get(i).getMaxStackSize() - n.items.get(i).getCount(), insertable);
                  n.items.get(i).grow(sent);
                  insertable -= sent;
                }
              }
              if (insertable > 0) {
                ItemStack toSend = stack.copy();
                toSend.setCount(insertable);
                n.items.add(toSend);
              }
            }
            ItemStack finalSent = stack.copy();
            finalSent.setCount(stack.getCount() - sendable);
            return finalSent;
          } else {
            return stack;
          }
        }
      };
    }

  }
}
