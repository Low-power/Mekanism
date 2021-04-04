package mekanism.client.gui;

import mekanism.api.gas.GasTank;
import mekanism.client.gui.element.GuiGasGauge;
import mekanism.client.gui.element.GuiGasGauge.IGasInfoHandler;
import mekanism.client.gui.element.GaugeGui.Type;
import mekanism.common.inventory.container.NullContainer;
import mekanism.common.tile.TileEntityAmbientAccumulator;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class AmbientAccumulatorGui extends GuiMekanism
{
	TileEntityAmbientAccumulator tileEntity;

	public AmbientAccumulatorGui(EntityPlayer player, TileEntityAmbientAccumulator tile)
	{
		super(tile, new NullContainer(player, tile));
		tileEntity = tile;

		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.collectedGas;
			}
		}, Type.WIDE, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 26, 16));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(tileEntity.getInventoryName(), 45, 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
