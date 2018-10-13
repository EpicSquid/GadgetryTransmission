package epicsquid.gadgetry.transmission.tile;

import java.util.ArrayList;
import java.util.List;

import akka.japi.Pair;
import epicsquid.gadgetry.core.lib.tile.CableNetwork;
import epicsquid.gadgetry.core.lib.tile.CableWorldData;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileCable;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.util.InventoryUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemCableNetwork extends CableNetwork {
  public ArrayList<ItemStack> items = new ArrayList<ItemStack>();
  public static final String TYPE_ITEMS = "items";
  int offset = 0;

  public ItemCableNetwork(CableWorldData data) {
    super(data);
    type = TYPE_ITEMS;
  }

  public ItemCableNetwork(CableWorldData data, int id) {
    super(data, id);
    type = TYPE_ITEMS;
  }

  @Override
  public boolean needsDistributionTick() {
    return items.size() > 0;
  }

  public void distribute() {
    boolean doContinue = true;
    for (int i = 0; i < items.size() && doContinue; i++) {
      List<TileEntity> outputs = new ArrayList<TileEntity>();
      List<EnumFacing> outputFaces = new ArrayList<EnumFacing>();
      for (Pair<TileEntity, EnumFacing> p : connections) {
        if (p.first().hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, p.second())) {
          IItemHandler h = p.first().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, p.second());
          int insertable = InventoryUtil.attemptInsert(items.get(i).copy(), h, true);
          if (insertable > 0) {
            outputs.add(p.first());
            outputFaces.add(p.second());
          }
        }
      }
      if (outputs.size() > 0) {
        doContinue = false;
        int toEach = items.get(i).getCount() / outputs.size();
        int remainder = items.get(i).getCount() - (toEach * outputs.size());

        for (int j = 0; j < outputs.size(); j++) {
          int fixedIndex = (j + offset) % outputs.size();
          IItemHandler e = outputs.get(fixedIndex).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputFaces.get(fixedIndex));
          int toSend = toEach + (j < remainder ? 1 : 0);
          ItemStack stack = items.get(i).copy();
          stack.setCount(toSend);
          int sent = InventoryUtil.attemptInsert(stack.copy(), e, true);
          if (sent > 0) {
            InventoryUtil.attemptInsert(stack.copy(), e, false);
            stack.shrink(sent);
            items.get(i).shrink(sent);
            outputs.get(fixedIndex).markDirty();
            data.markDirty();
          }
        }
      }
    }
    for (int i = 0; i < items.size(); i++) {
      if (items.get(i).isEmpty()) {
        items.remove(i);
        i = Math.max(0, i - 1);
        data.markDirty();
      }
    }
    offset++;
  }

  public int canInsert(ItemStack s) {
    int counter = 0;
    if (s.isEmpty()) {
      return 0;
    }
    int amount = 0;
    for (Pair<TileEntity, EnumFacing> p : connections) {
      if (p.first() != null && p.first().hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, p.second())) {
        IItemHandler handler = p.first().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, p.second());
        int insertable = InventoryUtil.attemptInsert(s, handler, true);
        if (insertable > 0) {
          amount += insertable;
        }
      }
    }
    return Math.min(s.getCount(), amount);
  }

  @Override
  public void processUpdateFor(TileCable c) {
    for (EnumFacing f : EnumFacing.values()) {
      if (c.config.ioConfig.get(f) != FaceIO.NEUTRAL) {
        TileEntity t2 = c.getWorld().getTileEntity(c.getPos().offset(f));
        String s = TileBase.getTileName(t2.getClass());
        if (s.length() >= 14 && s.substring(0, 14).equalsIgnoreCase("tile_duct_item")) {
          //
        } else if (t2 != null && !t2.getWorld().isAirBlock(t2.getPos()) && !cables.contains(t2.getPos())) {
          connections.add(new Pair<TileEntity, EnumFacing>(t2, f.getOpposite()));
        }
      }
    }
  }

  public ArrayList<ItemStack> getItems() {
    return items;
  }

  public void readFromNBT(NBTTagCompound tag) {
    if (tag.hasKey("items")) {
      NBTTagList items = tag.getTagList("items", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < items.tagCount(); i++) {
        this.items.add(new ItemStack(items.getCompoundTagAt(i)));
      }
    }
    offset = tag.getInteger("offset");
    super.readFromNBT(tag);
  }

  public NBTTagCompound writeToNBT() {
    NBTTagCompound tag = super.writeToNBT();
    if (items.size() > 0) {
      NBTTagList itemlist = new NBTTagList();
      for (ItemStack s : items) {
        itemlist.appendTag(s.writeToNBT(new NBTTagCompound()));
      }
    }
    tag.setInteger("offset", offset);
    return tag;
  }
}
