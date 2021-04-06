package mekanism.client.gui;

import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.common.tile.AdvancedElectricMachineTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;

@SideOnly(Side.CLIENT)
public class CombinerGui extends AdvancedElectricMachineGui
{
	public CombinerGui(InventoryPlayer inventory, AdvancedElectricMachineTileEntity tentity)
	{
		super(inventory, tentity);
	}

	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.STONE;
	}
}
