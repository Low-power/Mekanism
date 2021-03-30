package mekanism.common.item;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.entity.Robit;
import mekanism.common.tile.ChargepadTileEntity;
import mekanism.common.util.LangUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import java.util.List;

public class RobitItem extends ItemEnergized implements ISustainedInventory
{
	public RobitItem()
	{
		super(100000);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
	{
		super.addInformation(itemstack, player, list, flag);

		list.add(EnumColor.INDIGO + LangUtils.localize("tooltip.name") + ": " + EnumColor.GREY + getName(itemstack));
		list.add(EnumColor.AQUA + LangUtils.localize("tooltip.inventory") + ": " + EnumColor.GREY + (getInventory(itemstack) != null && getInventory(itemstack).tagCount() != 0));
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float posX, float posY, float posZ)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		if(tileEntity instanceof ChargepadTileEntity)
		{
			ChargepadTileEntity chargepad = (ChargepadTileEntity)tileEntity;
			if(!chargepad.isActive)
			{
				if(!world.isRemote)
				{
					Robit robit = new Robit(world, x+0.5, y+0.1, z+0.5);

					robit.setHome(Coord4D.get(chargepad));
					robit.setEnergy(getEnergy(itemstack));
					robit.setOwner(player.getCommandSenderName());
					robit.setInventory(getInventory(itemstack));
					robit.setName(getName(itemstack));

					world.spawnEntityInWorld(robit);
				}

				player.setCurrentItemOrArmor(0, null);

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canSend(ItemStack itemStack)
	{
		return false;
	}

	public void setName(ItemStack itemstack, String name)
	{
		if(itemstack.stackTagCompound == null)
		{
			itemstack.setTagCompound(new NBTTagCompound());
		}

		itemstack.stackTagCompound.setString("name", name);
	}

	public String getName(ItemStack itemstack)
	{
		if(itemstack.stackTagCompound == null)
		{
			return "Robit";
		}

		String name = itemstack.stackTagCompound.getString("name");

		return name.equals("") ? "Robit" : name;
	}

	@Override
	public void setInventory(NBTTagList nbtTags, Object... data)
	{
		if(data[0] instanceof ItemStack)
		{
			ItemStack itemStack = (ItemStack)data[0];

			if(itemStack.stackTagCompound == null)
			{
				itemStack.setTagCompound(new NBTTagCompound());
			}

			itemStack.stackTagCompound.setTag("Items", nbtTags);
		}
	}

	@Override
	public NBTTagList getInventory(Object... data)
	{
		if(data[0] instanceof ItemStack)
		{
			ItemStack itemStack = (ItemStack)data[0];

			if(itemStack.stackTagCompound == null)
			{
				return null;
			}

			return itemStack.stackTagCompound.getTagList("Items", 10);
		}

		return null;
	}
}
