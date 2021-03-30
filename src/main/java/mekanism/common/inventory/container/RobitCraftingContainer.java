package mekanism.common.inventory.container;

import mekanism.common.entity.Robit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;

public class RobitCraftingContainer extends ContainerWorkbench
{
	public Robit robit;
	
	public RobitCraftingContainer(InventoryPlayer inventory, Robit entity)
	{
		super(inventory, entity.worldObj, 0, 0, 0);
		
		robit = entity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return !robit.isDead;
	}
}
