package mekanism.client.gui;

import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.common.tile.ChanceMachineTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;

@SideOnly(Side.CLIENT)
public class PrecisionSawmillGui extends ChanceMachineGui
{
	public PrecisionSawmillGui(InventoryPlayer inventory, ChanceMachineTileEntity tentity)
	{
		super(inventory, tentity);
	}

	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.PURPLE;
	}
}
