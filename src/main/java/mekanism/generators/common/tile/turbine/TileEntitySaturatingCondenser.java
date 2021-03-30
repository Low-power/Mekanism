package mekanism.generators.common.tile.turbine;

import mekanism.common.tile.BasicBlockTileEntity;

public class TileEntitySaturatingCondenser extends BasicBlockTileEntity
{
	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public void onUpdate() {}
}
