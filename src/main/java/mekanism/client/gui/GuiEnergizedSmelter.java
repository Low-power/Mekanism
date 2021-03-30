package mekanism.client.gui;

import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.common.tile.ElectricMachineTileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnergizedSmelter extends ElectricMachineGui
{
	public GuiEnergizedSmelter(InventoryPlayer inventory, ElectricMachineTileEntity tentity)
	{
		super(inventory, tentity);
	}
	
	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.GREEN;
	}
}
