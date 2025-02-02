package mekanism.common.inventory.container;

import mekanism.common.inventory.slot.SlotEnergy.SlotCharge;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.tile.EnergyCubeTileEntity;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class EnergyCubeContainer extends Container
{
	private EnergyCubeTileEntity tileEntity;

	public EnergyCubeContainer(InventoryPlayer inventory, EnergyCubeTileEntity unit)
	{
		tileEntity = unit;

		addSlotToContainer(new SlotCharge(unit, 0, 143, 35));
		addSlotToContainer(new SlotDischarge(unit, 1, 17, 35));

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

			if(ChargeUtils.canBeCharged(slotStack) || ChargeUtils.canBeDischarged(slotStack))
			{
				if(slotStack.getItem() == Items.redstone)
				{
					if(slotID != 1)
					{
						if(!mergeItemStack(slotStack, 1, 2, false))
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
				else {
					if(slotID != 1 && slotID != 0)
					{
						if(ChargeUtils.canBeDischarged(slotStack))
						{
							if(!mergeItemStack(slotStack, 1, 2, false))
							{
								if(canTransfer(slotStack))
								{
									if(!mergeItemStack(slotStack, 0, 1, false))
									{
										return null;
									}
								}
							}
						}
						else if(canTransfer(slotStack))
						{
							if(!mergeItemStack(slotStack, 0, 1, false))
							{
								return null;
							}
						}
					}
					else if(slotID == 1)
					{
						if(canTransfer(slotStack))
						{
							if(!mergeItemStack(slotStack, 0, 1, false))
							{
								if(!mergeItemStack(slotStack, 2, inventorySlots.size(), true))
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
					else if(slotID == 0)
					{
						if(!mergeItemStack(slotStack, 2, inventorySlots.size(), true))
						{
							return null;
						}
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

	private boolean canTransfer(ItemStack slotStack)
	{
		return MekanismUtils.useIC2() && slotStack.getItem() instanceof IElectricItem;
	}
}
