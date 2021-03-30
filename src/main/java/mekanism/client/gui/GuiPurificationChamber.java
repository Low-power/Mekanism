package mekanism.client.gui;

import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.common.tile.AdvancedElectricMachineTileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPurificationChamber extends AdvancedElectricMachineGui
{
	public GuiPurificationChamber(InventoryPlayer inventory, AdvancedElectricMachineTileEntity tentity)
	{
		super(inventory, tentity);
	}
	
	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.RED;
	}
}
