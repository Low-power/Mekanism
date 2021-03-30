package mekanism.common.inventory.container;

import mekanism.common.entity.Robit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;

public class RobitRepairContainer extends ContainerRepair
{
	public Robit robit;
	
	public RobitRepairContainer(InventoryPlayer inventory, Robit entity)
	{
		super(inventory, entity.worldObj, 0, 0, 0, inventory.player);
		
		robit = entity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return !robit.isDead;
	}
}
