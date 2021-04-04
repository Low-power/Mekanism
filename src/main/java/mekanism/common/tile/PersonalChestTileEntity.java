package mekanism.common.tile;

import mekanism.common.util.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;

public class PersonalChestTileEntity extends ContainerTileEntity
{
	public static int[] INV;

	public float lidAngle;

	public float prevLidAngle;

	public PersonalChestTileEntity()
	{
		super("PersonalChest");
		inventory = new ItemStack[54];
	}

	@Override
	public void onUpdate()
	{
		prevLidAngle = lidAngle;
		float increment = 0.1F;

		if(playersUsing.size() > 0 && lidAngle == 0F)
		{
			worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5D, zCoord + 0.5F, "random.chestopen", 0.5F, (worldObj.rand.nextFloat()*0.1F) + 0.9F);
		}

		if((playersUsing.size() == 0 && lidAngle > 0F) || (playersUsing.size() > 0 && lidAngle < 1F))
		{
			float angle = lidAngle;

			if(playersUsing.size() > 0)
			{
				lidAngle += increment;
			}
			else {
				lidAngle -= increment;
			}

			if(lidAngle > 1F)
			{
				lidAngle = 1F;
			}

			float split = 0.5F;

			if(lidAngle < split && angle >= split)
			{
				worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.chestclosed", 0.5F, (worldObj.rand.nextFloat()*0.1F) + 0.9F);
			}

			if(lidAngle < 0F)
			{
				lidAngle = 0F;
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		if(side == 0) return InventoryUtils.EMPTY;

		if(INV == null)
		{
			INV = new int[54];

			for(int i = 0; i < INV.length; i++)
			{
				INV[i] = i;
			}
		}

		return INV;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	{
		return true;
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer player)
	{
		return true;
	}

	@Override
	public boolean canSetFacing(int side)
	{
		return side != 0 && side != 1;
	}
}
