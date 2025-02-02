package mekanism.common.inventory.container;

import mekanism.api.gas.IGasItem;
import mekanism.common.inventory.slot.SlotStorageTank;
import mekanism.common.tile.SolarNeutronActivatorTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SolarNeutronActivatorContainer extends Container
{
	private SolarNeutronActivatorTileEntity tileEntity;

	public SolarNeutronActivatorContainer(InventoryPlayer inventory, SolarNeutronActivatorTileEntity tentity)
	{
		tileEntity = tentity;
		addSlotToContainer(new SlotStorageTank(tentity, 0, 5, 56));
		addSlotToContainer(new SlotStorageTank(tentity, 1, 155, 56));

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

			if(slotStack.getItem() instanceof IGasItem)
			{
				if(slotID != 0 && slotID != 1)
				{
					if(((IGasItem)slotStack.getItem()).canProvideGas(slotStack, tileEntity.inputTank.getGas() != null ? tileEntity.inputTank.getGas().getGas() : null))
					{
						if(!mergeItemStack(slotStack, 0, 1, false))
						{
							return null;
						}
					}
					else if(((IGasItem)slotStack.getItem()).canReceiveGas(slotStack, tileEntity.outputTank.getGas() != null ? tileEntity.outputTank.getGas().getGas() : null))
					{
						if(!mergeItemStack(slotStack, 1, 2, false))
						{
							return null;
						}
					}
				}
				else {
					if(!mergeItemStack(slotStack, 2, inventorySlots.size(), true))
					{
						return null;
					}
				}
			}
			else {
				if(slotID >= 2 && slotID <= 28)
				{
					if(!mergeItemStack(slotStack, 29, inventorySlots.size(), false))
					{
						return null;
					}
				}
				else if(slotID > 28)
				{
					if(!mergeItemStack(slotStack, 2, 28, false))
					{
						return null;
					}
				}
				else {
					if(!mergeItemStack(slotStack, 2, inventorySlots.size(), true))
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
