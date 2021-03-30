package mekanism.common.inventory.container;

import mekanism.common.entity.Robit;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RobitMainContainer extends Container
{
	private Robit robit;

	public RobitMainContainer(InventoryPlayer inventory, Robit entity)
	{
		robit = entity;
		addSlotToContainer(new SlotDischarge(entity, 27, 153, 17));

		robit.openInventory();

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
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		robit.closeInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return !robit.isDead;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
	{
		return null;
	}
}
