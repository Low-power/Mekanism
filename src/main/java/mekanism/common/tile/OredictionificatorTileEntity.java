package mekanism.common.tile;

import mekanism.api.Coord4D;
import mekanism.api.IConfigCardAccess.ISpecialConfigData;
import mekanism.api.Range4D;
import mekanism.common.HashList;
import mekanism.common.Mekanism;
import mekanism.common.OreDictCache;
import mekanism.common.PacketHandler;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISustainedData;
import mekanism.common.block.Machine.MachineType;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OredictionificatorTileEntity extends ContainerTileEntity implements IRedstoneControl, ISpecialConfigData, ISustainedData
{
	public static final int MAX_LENGTH = 24;

	public HashList<OredictionificatorFilter> filters = new HashList<OredictionificatorFilter>();

	public static List<String> possibleFilters = Arrays.asList("ingot", "ore", "dust", "nugget");

	public RedstoneControl controlType = RedstoneControl.DISABLED;

	public boolean didProcess;

	public OredictionificatorTileEntity()
	{
		super(MachineType.OREDICTIONIFICATOR.name);

		inventory = new ItemStack[2];
		doAutoSync = false;
	}

	@Override
	public void onUpdate()
	{
		if(!worldObj.isRemote)
		{
			if(playersUsing.size() > 0)
			{
				for(EntityPlayer player : playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(this), getGenericPacket(new ArrayList())), (EntityPlayerMP)player);
				}
			}
			didProcess = false;
			if(MekanismUtils.canFunction(this) && inventory[0] != null && getValidName(inventory[0]) != null)
			{
				ItemStack result = getResult(inventory[0]);
				if(result != null)
				{
					if(inventory[1] == null)
					{
						inventory[0].stackSize--;
						if(inventory[0].stackSize <= 0)
						{
							inventory[0] = null;
						}
						inventory[1] = result;
						didProcess = true;
					}
					else if(inventory[1].isItemEqual(result) && inventory[1].stackSize < inventory[1].getMaxStackSize())
					{
						inventory[0].stackSize--;
						if(inventory[0].stackSize <= 0)
						{
							inventory[0] = null;
						}
						inventory[1].stackSize++;
						didProcess = true;
					}
					markDirty();
				}
			}
		}
	}

	public String getValidName(ItemStack stack)
	{
		List<String> def = OreDictCache.getOreDictName(stack);
		for(String s : def)
		{
			for(String pre : possibleFilters)
			{
				if(s.startsWith(pre))
				{
					return s;
				}
			}
		}
		return null;
	}

	public ItemStack getResult(ItemStack stack)
	{
		String s = getValidName(stack);
		if(s == null)
		{
			return null;
		}
		List<ItemStack> ores = OreDictionary.getOres(s);
		for(OredictionificatorFilter filter : filters)
		{
			if(filter.filter.equals(s))
			{
				if(ores.size()-1 >= filter.index)
				{
					return MekanismUtils.size(ores.get(filter.index), 1);
				}
				else {
					return null;
				}
			}
		}
		return null;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		if(side == MekanismUtils.getLeft(facing).ordinal())
		{
			return new int[] {0};
		}
		else if(side == MekanismUtils.getRight(facing).ordinal())
		{
			return new int[] {1};
		}
		else {
			return InventoryUtils.EMPTY;
		}
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	{
		return slotID == 1;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		if(slotID == 0)
		{
			return getResult(itemstack) != null;
		}

		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		nbtTags.setInteger("controlType", controlType.ordinal());

		NBTTagList filterTags = new NBTTagList();

		for(OredictionificatorFilter filter : filters)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			filter.write(tagCompound);
			filterTags.appendTag(tagCompound);
		}

		if(filterTags.tagCount() != 0)
		{
			nbtTags.setTag("filters", filterTags);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];

		if(nbtTags.hasKey("filters"))
		{
			NBTTagList tagList = nbtTags.getTagList("filters", NBT.TAG_COMPOUND);

			for(int i = 0; i < tagList.tagCount(); i++)
			{
				filters.add(OredictionificatorFilter.readFromNBT((NBTTagCompound)tagList.getCompoundTagAt(i)));
			}
		}
	}

	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);

		if(worldObj.isRemote)
		{
			int type = dataStream.readInt();
			if(type == 0)
			{
				controlType = RedstoneControl.values()[dataStream.readInt()];
				didProcess = dataStream.readBoolean();
				filters.clear();
				int amount = dataStream.readInt();
				for(int i = 0; i < amount; i++)
				{
					filters.add(OredictionificatorFilter.readFromPacket(dataStream));
				}
			}
			else if(type == 1)
			{
				controlType = RedstoneControl.values()[dataStream.readInt()];
				didProcess = dataStream.readBoolean();
			}
			else if(type == 2)
			{
				filters.clear();
				int amount = dataStream.readInt();
				for(int i = 0; i < amount; i++)
				{
					filters.add(OredictionificatorFilter.readFromPacket(dataStream));
				}
			}
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);

		data.add(Integer.valueOf(0));

		data.add(Integer.valueOf(controlType.ordinal()));
		data.add(Boolean.valueOf(didProcess));

		data.add(Integer.valueOf(filters.size()));

		for(OredictionificatorFilter filter : filters)
		{
			filter.write(data);
		}

		return data;
	}

	public ArrayList getGenericPacket(ArrayList data)
	{
		super.getNetworkedData(data);

		data.add(Integer.valueOf(1));

		data.add(Integer.valueOf(controlType.ordinal()));
		data.add(Boolean.valueOf(didProcess));

		return data;

	}

	public ArrayList getFilterPacket(ArrayList data)
	{
		super.getNetworkedData(data);

		data.add(Integer.valueOf(2));

		data.add(Integer.valueOf(filters.size()));

		for(OredictionificatorFilter filter : filters)
		{
			filter.write(data);
		}

		return data;
	}

	@Override
	public void openInventory()
	{
		if(!worldObj.isRemote)
		{
			Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getFilterPacket(new ArrayList())), new Range4D(Coord4D.get(this)));
		}
	}

	@Override
	public NBTTagCompound getConfigurationData(NBTTagCompound nbtTags)
	{
		NBTTagList filterTags = new NBTTagList();

		for(OredictionificatorFilter filter : filters)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			filter.write(tagCompound);
			filterTags.appendTag(tagCompound);
		}

		if(filterTags.tagCount() != 0)
		{
			nbtTags.setTag("filters", filterTags);
		}
		return nbtTags;
	}

	@Override
	public void setConfigurationData(NBTTagCompound nbtTags)
	{
		if(nbtTags.hasKey("filters"))
		{
			NBTTagList tagList = nbtTags.getTagList("filters", NBT.TAG_COMPOUND);

			for(int i = 0; i < tagList.tagCount(); i++)
			{
				filters.add(OredictionificatorFilter.readFromNBT((NBTTagCompound)tagList.getCompoundTagAt(i)));
			}
		}
	}

	@Override
	public String getDataType()
	{
		return getBlockType().getUnlocalizedName() + "." + fullName + ".name";
	}

	@Override
	public void writeSustainedData(ItemStack itemStack) {
		itemStack.stackTagCompound.setBoolean("hasOredictionificatorConfig", true);

		NBTTagList filterTags = new NBTTagList();

		for(OredictionificatorFilter filter : filters)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			filter.write(tagCompound);
			filterTags.appendTag(tagCompound);
		}

		if(filterTags.tagCount() != 0)
		{
			itemStack.stackTagCompound.setTag("filters", filterTags);
		}
	}

	@Override
	public void readSustainedData(ItemStack itemStack) {
		if(itemStack.stackTagCompound.hasKey("hasOredictionificatorConfig"))
		{
			if(itemStack.stackTagCompound.hasKey("filters"))
			{
				NBTTagList tagList = itemStack.stackTagCompound.getTagList("filters", NBT.TAG_COMPOUND);

				for(int i = 0; i < tagList.tagCount(); i++)
				{
					filters.add(OredictionificatorFilter.readFromNBT((NBTTagCompound)tagList.getCompoundTagAt(i)));
				}
			}
		}
	}

	@Override
	public RedstoneControl getControlType()
	{
		return controlType;
	}

	@Override
	public void setControlType(RedstoneControl type)
	{
		controlType = type;
	}

	@Override
	public boolean canPulse()
	{
		return true;
	}

	public static class OredictionificatorFilter
	{
		public String filter;
		public int index;

		public void write(NBTTagCompound nbtTags)
		{
			nbtTags.setString("filter", filter);
			nbtTags.setInteger("index", index);
		}

		protected void read(NBTTagCompound nbtTags)
		{
			filter = nbtTags.getString("filter");
			index = nbtTags.getInteger("index");
		}

		public void write(ArrayList data)
		{
			data.add(filter);
			data.add(Integer.valueOf(index));
		}

		protected void read(ByteBuf dataStream)
		{
			filter = PacketHandler.readString(dataStream);
			index = dataStream.readInt();
		}

		public static OredictionificatorFilter readFromNBT(NBTTagCompound nbtTags)
		{
			OredictionificatorFilter filter = new OredictionificatorFilter();

			filter.read(nbtTags);

			return filter;
		}

		public static OredictionificatorFilter readFromPacket(ByteBuf dataStream)
		{
			OredictionificatorFilter filter = new OredictionificatorFilter();

			filter.read(dataStream);

			return filter;
		}

		@Override
		public OredictionificatorFilter clone()
		{
			OredictionificatorFilter newFilter = new OredictionificatorFilter();
			newFilter.filter = filter;
			newFilter.index = index;

			return newFilter;
		}

		@Override
		public int hashCode()
		{
			int code = 1;
			code = 31 * code + filter.hashCode();
			return code;
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof OredictionificatorFilter && ((OredictionificatorFilter)obj).filter.equals(filter);
		}
	}
}
