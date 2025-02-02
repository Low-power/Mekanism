package mekanism.common.inventory.container;

import invtweaks.api.container.ChestContainer;
import mekanism.common.block.Machine.MachineType;
import mekanism.common.inventory.slot.SlotPersonalChest;
import mekanism.common.tile.PersonalChestTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@ChestContainer(isLargeChest=true)
public class PersonalChestContainer extends Container
{
	private PersonalChestTileEntity tileEntity;
	private IInventory itemInventory;
	private boolean isBlock;

	public PersonalChestContainer(InventoryPlayer inventory, PersonalChestTileEntity tentity, IInventory inv, boolean b)
	{
		tileEntity = tentity;
		itemInventory = inv;
		isBlock = b;

		if(isBlock)
		{
			tileEntity.open(inventory.player);
			tileEntity.openInventory();
		}
		else {
			itemInventory.openInventory();
		}

		for(int slotY = 0; slotY < 6; slotY++)
		{
			for(int slotX = 0; slotX < 9; slotX++)
			{
				addSlotToContainer(new SlotPersonalChest(getInv(), slotX + slotY * 9, 8 + slotX * 18, 26 + slotY * 18));
			}
		}

		int slotX;

		for(slotX = 0; slotX < 3; ++slotX)
		{
			for(int slotY = 0; slotY < 9; ++slotY)
			{
				addSlotToContainer(new SlotPersonalChest(inventory, slotY + slotX * 9 + 9, 8 + slotY * 18, 148 + slotX * 18));
			}
		}

		for(slotX = 0; slotX < 9; ++slotX)
		{
			addSlotToContainer(new SlotPersonalChest(inventory, slotX, 8 + slotX * 18, 206));
		}
	}

	public IInventory getInv()
	{
		if(isBlock)
		{
			return tileEntity;
		}
		else {
			return itemInventory;
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		if(isBlock)
		{
			tileEntity.close(player);
			tileEntity.closeInventory();
		}
		else {
			itemInventory.closeInventory();
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		if(isBlock)
		{
			return tileEntity.isUseableByPlayer(player);
		}

		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
	{
		ItemStack stack = null;
		Slot currentSlot = (Slot)inventorySlots.get(slotID);

		if(currentSlot != null && currentSlot.getHasStack())
		{
			ItemStack slotStack = currentSlot.getStack();
			stack = slotStack.copy();

			if(slotID < 54)
			{
				if(!mergeItemStack(slotStack, 54, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if(!mergeItemStack(slotStack, 0, 54, false))
			{
				return null;
			}

			if(slotStack.stackSize == 0)
			{
				currentSlot.putStack((ItemStack)null);
			}
			else {
				currentSlot.onSlotChanged();
			}

			if(slotStack.stackSize == stack.stackSize)
			{
				return null;
			}

			currentSlot.onPickupFromSlot(player, slotStack);
		}

		return stack;
	}

	@Override
	public ItemStack slotClick(int slotNumber, int destSlot, int modifier, EntityPlayer player)
	{
		if(modifier == 2 && destSlot >= 0 && destSlot < 9)
		{
			ItemStack itemStack = player.inventory.getStackInSlot(destSlot);
			if(MachineType.get(itemStack) == MachineType.PERSONAL_CHEST)
			{
				return null;
			}
		}

		return super.slotClick(slotNumber, destSlot, modifier, player);
	}
}
