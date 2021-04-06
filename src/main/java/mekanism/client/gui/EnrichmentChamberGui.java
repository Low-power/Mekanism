package mekanism.client.gui;

import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.common.tile.ElectricMachineTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;

@SideOnly(Side.CLIENT)
public class EnrichmentChamberGui extends ElectricMachineGui
{
	public EnrichmentChamberGui(InventoryPlayer inventory, ElectricMachineTileEntity tentity)
	{
		super(inventory, tentity);
	}

	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.BLUE;
	}
}
