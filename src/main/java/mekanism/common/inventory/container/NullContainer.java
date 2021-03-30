package mekanism.common.inventory.container;

import mekanism.common.tile.ContainerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class NullContainer extends Container
{
	private ContainerTileEntity tileEntity;

	public NullContainer(EntityPlayer player, ContainerTileEntity tile)
	{
		tileEntity = tile;

		if(tileEntity != null)
		{
			tileEntity.open(player);
			tileEntity.openInventory();
		}
	}

	public NullContainer(ContainerTileEntity tile)
	{
		tileEntity = tile;
	}

	public NullContainer() {}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		if(tileEntity != null)
		{
			tileEntity.close(player);
			tileEntity.closeInventory();
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		if(tileEntity != null)
		{
			return tileEntity.isUseableByPlayer(player);
		}
		return true;
	}
}
