package mekanism.common.inventory.container;

import mekanism.common.tile.FuelwoodHeaterTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class FuelwoodHeaterContainer extends Container
{
	private FuelwoodHeaterTileEntity tileEntity;

	public FuelwoodHeaterContainer(InventoryPlayer inventory, FuelwoodHeaterTileEntity tentity)
	{
		tileEntity = tentity;
		addSlotToContainer(new Slot(tentity, 0, 15, 29));

		int slotY;

		for(slotY = 0; slotY < 3; slotY++)
		{
			for(int slotX = 0; slotX < 9; slotX++)
			{
				addSlotToContainer(new Slot(inventory, slotX + slotY * 9 + 9, 8 + slotX * 18, 84 + slotY * 18));
			}
		}

		for(slotY = 0; slotY < 9; slotY++)
		{
			addSlotToContainer(new Slot(inventory, slotY, 8 + slotY * 18, 142));
		}

		tileEntity.open(inventory.player);
		tileEntity.openInventory();
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		tileEntity.close(player);
		tileEntity.closeInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tileEntity.isUseableByPlayer(player);
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

			if(TileEntityFurnace.getItemBurnTime(slotStack) > 0)
			{
				if(slotID != 0)
				{
					if(!mergeItemStack(slotStack, 0, 1, false))
					{
						return null;
					}
				}
				else if(slotID == 0)
				{
					if(!mergeItemStack(slotStack, 1, inventorySlots.size(), true))
					{
						return null;
					}
				}
			}
			else {
				if(slotID >= 1 && slotID <= 27)
				{
					if(!mergeItemStack(slotStack, 28, inventorySlots.size(), false))
					{
						return null;
					}
				}
				else if(slotID > 27)
				{
					if(!mergeItemStack(slotStack, 1, 27, false))
					{
						return null;
					}
				}
				else {
					if(!mergeItemStack(slotStack, 1, inventorySlots.size(), true))
					{
						return null;
					}
				}
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
}
