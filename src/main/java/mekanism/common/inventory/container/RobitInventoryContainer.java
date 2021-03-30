package mekanism.common.inventory.container;

import mekanism.common.entity.Robit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RobitInventoryContainer extends Container
{
	public Robit robit;

	public RobitInventoryContainer(InventoryPlayer inventory, Robit entity)
	{
		robit = entity;
		robit.openInventory();

		for(int slotY = 0; slotY < 3; slotY++)
		{
			for(int slotX = 0; slotX < 9; slotX++)
			{
				addSlotToContainer(new Slot(entity, slotX + slotY * 9, 8 + slotX * 18, 18 + slotY * 18));
			}
		}

		int slotX;

		for(slotX = 0; slotX < 3; ++slotX)
		{
			for(int slotY = 0; slotY < 9; ++slotY)
			{
				addSlotToContainer(new Slot(inventory, slotY + slotX * 9 + 9, 8 + slotY * 18, 84 + slotX * 18));
			}
		}

		for(slotX = 0; slotX < 9; slotX++)
		{
			addSlotToContainer(new Slot(inventory, slotX, 8 + slotX * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return !robit.isDead;
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

			if(slotID < 27)
			{
				if(!mergeItemStack(slotStack, 27, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if(!mergeItemStack(slotStack, 0, 27, false))
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
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		robit.closeInventory();
	}
}
