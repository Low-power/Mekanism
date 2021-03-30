package mekanism.common.tile.component;

import mekanism.common.Upgrade;
import mekanism.common.tile.ContainerTileEntity;

public class AdvancedUpgradeTileComponent extends TileComponentUpgrade
{
	public AdvancedUpgradeTileComponent(ContainerTileEntity tile, int slot)
	{
		super(tile, slot);

		setSupported(Upgrade.GAS);
	}
}
