package mekanism.common.item;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import java.util.List;

public class PortableTeleporter extends ItemEnergized
{
	public PortableTeleporter()
	{
		super(1000000);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
	{
		if(getFrequency(itemstack) != null)
		{
			list.add(EnumColor.INDIGO + LangUtils.localize("gui.frequency") + ": " + EnumColor.GREY + getFrequency(itemstack));
			list.add(EnumColor.INDIGO + LangUtils.localize("gui.mode") + ": " + EnumColor.GREY + LangUtils.localize("gui." + (isPrivateMode(itemstack) ? "private" : "public")));
		}
		super.addInformation(itemstack, player, list, flag);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
		{
			player.openGui(Mekanism.instance, 14, world, 0, 0, 0);
		}
		return itemstack;
	}

	public static double calculateEnergyCost(Entity entity, Coord4D coords)
	{
		if(coords == null)
		{
			return 0;
		}

		int neededEnergy = 1000;

		if(entity.worldObj.provider.dimensionId != coords.dimensionId)
		{
			neededEnergy+=10000;
		}

		int distance = (int)entity.getDistance(coords.xCoord, coords.yCoord, coords.zCoord);

		neededEnergy+=(distance*10);

		return neededEnergy;
	}

	@Override
	public boolean canSend(ItemStack itemStack)
	{
		return false;
	}

	public boolean isPrivateMode(ItemStack stack) {
		if(stack.stackTagCompound != null)
		{
			return stack.stackTagCompound.getBoolean("private");
		}
		return false;
	}

	public void setPrivateMode(ItemStack stack, boolean isPrivate) {
		if(stack.stackTagCompound == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.stackTagCompound.setBoolean("private", isPrivate);
	}

	public String getFrequency(ItemStack stack) {
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("frequency"))
		{
			return stack.stackTagCompound.getString("frequency");
		}
		return null;
	}

	public void setFrequency(ItemStack stack, String frequency) {
		if(stack.stackTagCompound == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		if(frequency == null || frequency.isEmpty())
		{
			stack.stackTagCompound.removeTag("frequency");
			return;
		}
		stack.stackTagCompound.setString("frequency", frequency);
	}
}
