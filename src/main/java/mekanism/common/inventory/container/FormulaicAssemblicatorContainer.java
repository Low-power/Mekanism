package mekanism.common.inventory.container;

import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotOutput;
import mekanism.common.inventory.slot.SlotSpecific;
import mekanism.common.item.ItemCraftingFormula;
import mekanism.common.tile.FormulaicAssemblicatorTileEntity;
import mekanism.common.util.ChargeUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FormulaicAssemblicatorContainer extends Container
{
	private FormulaicAssemblicatorTileEntity tileEntity;

	public FormulaicAssemblicatorContainer(InventoryPlayer inventory, FormulaicAssemblicatorTileEntity tentity)
	{
		tileEntity = tentity;
		addSlotToContainer(new SlotDischarge(tentity, 1, 152, 76));
		addSlotToContainer(new SlotSpecific(tentity, 2, 6, 26, ItemCraftingFormula.class));

		int slotY;
		for(slotY = 0; slotY < 2; slotY++)
		{
			for(int slotX = 0; slotX < 9; slotX++)
			{
				addSlotToContainer(new Slot(tentity, slotX + slotY * 9 + 3, 8 + slotX * 18, 98 + slotY * 18));
			}
		}
		for(slotY = 0; slotY < 3; slotY++)
		{
			for(int slotX = 0; slotX < 3; slotX++)
			{
				addSlotToContainer(new Slot(tentity, slotX + slotY * 3 + 27, 26 + slotX * 18, 17 + slotY * 18) {
					@Override
					public boolean isItemValid(ItemStack stack)
					{
						return !tileEntity.autoMode;
					}

					@Override
					public boolean canTakeStack(EntityPlayer player)
					{
						return !tileEntity.autoMode;
					}

					@SideOnly(Side.CLIENT)
					public boolean func_111238_b() {
						return !tileEntity.autoMode;
					}
				});
			}
		}
		for(slotY = 0; slotY < 3; slotY++)
		{
			for(int slotX = 0; slotX < 2; slotX++)
			{
				addSlotToContainer(new SlotOutput(tentity, slotX + slotY * 2 + 21, 116 + slotX * 18, 17 + slotY * 18));
			}
		}

		for(slotY = 0; slotY < 3; slotY++)
		{
			for(int slotX = 0; slotX < 9; slotX++)
			{
				addSlotToContainer(new Slot(inventory, slotX + slotY * 9 + 9, 8 + slotX * 18, 148 + slotY * 18));
			}
		}

		for(slotY = 0; slotY < 9; slotY++)
		{
			addSlotToContainer(new Slot(inventory, slotY, 8 + slotY * 18, 206));
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

			if(ChargeUtils.canBeDischarged(slotStack))
			{
				if(slotID != 0)
				{
					if(!mergeItemStack(slotStack, 0, 1, false))
					{
						return null;
					}
				}
				else {
					if(!mergeItemStack(slotStack, 34, inventorySlots.size(), true))
					{
						return null;
					}
				}
			}
			else if(slotStack.getItem() instanceof ItemCraftingFormula)
			{
				if(slotID != 1)
				{
					if(!mergeItemStack(slotStack, 1, 2, false))
					{
						return null;
					}
				}
				else {
					if(!mergeItemStack(slotStack, 34, inventorySlots.size(), true))
					{
						return null;
					}
				}
			}
			else if(slotID >= 2 && slotID <= 19)
			{
				if(!mergeItemStack(slotStack, 34, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if(tileEntity.formula == null || tileEntity.formula.isIngredient(tileEntity.getWorldObj(), slotStack))
			{
				if(!mergeItemStack(slotStack, 2, 20, false))
				{
					return null;
				}
			}
			else {
				if(slotID >= 34 && slotID <= 60)
				{
					if(!mergeItemStack(slotStack, 61, inventorySlots.size(), false))
					{
						return null;
					}
				}
				else if(slotID > 60)
				{
					if(!mergeItemStack(slotStack, 34, 60, false))
					{
						return null;
					}
				}
				else {
					if(!mergeItemStack(slotStack, 34, inventorySlots.size(), true))
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
