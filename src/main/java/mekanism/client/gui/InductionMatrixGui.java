package mekanism.client.gui;

import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.MatrixTab;
import mekanism.client.gui.element.MatrixTab.MatrixTabType;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.ContainerInductionMatrix;
import mekanism.common.tile.TileEntityInductionCasing;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class InductionMatrixGui extends GuiMekanism
{
	public TileEntityInductionCasing tileEntity;

	public InductionMatrixGui(InventoryPlayer inventory, TileEntityInductionCasing tentity)
	{
		super(tentity, new ContainerInductionMatrix(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new MatrixTab(this, tileEntity, MatrixTabType.STAT, 6, MekanismUtils.getResource(ResourceType.GUI, "InductionMatrixGui.png")));
		guiElements.add(new EnergyInfoGui(new IInfoHandler()
		{
			@Override
			public List<String> getInfo()
			{
				return ListUtils.asList(
						LangUtils.localize("gui.storing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()),
						LangUtils.localize("gui.input") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.structure.lastInput) + "/t",
						LangUtils.localize("gui.output") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.structure.lastOutput) + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "InductionMatrixGui.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 94) + 2, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.input") + ":", 53, 26, 0x00CD00);
		fontRendererObj.drawString(MekanismUtils.getEnergyDisplay(tileEntity.structure.lastInput) + "/t", 53, 35, 0x00CD00);
		fontRendererObj.drawString(LangUtils.localize("gui.output") + ":", 53, 44, 0x00CD00);
		fontRendererObj.drawString(MekanismUtils.getEnergyDisplay(tileEntity.structure.lastOutput) + "/t", 53, 53, 0x00CD00);

		if(xAxis >= 7 && xAxis <= 39 && yAxis >= 14 && yAxis <= 72)
		{
			drawCreativeTabHoveringText(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()), xAxis, yAxis);
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "InductionMatrixGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		if(tileEntity.getScaledEnergyLevel(58) > 0)
		{
			displayGauge(7, 14, tileEntity.getScaledEnergyLevel(58), 0);
			displayGauge(23, 14, tileEntity.getScaledEnergyLevel(58), 1);
		}
	}

	public void displayGauge(int xPos, int yPos, int scale, int side /*0-left, 1-right*/)
	{
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		int start = 0;

		while(true)
		{
			int renderRemaining = 0;

			if(scale > 16)
			{
				renderRemaining = 16;
				scale -= 16;
			}
			else {
				renderRemaining = scale;
				scale = 0;
			}

			mc.renderEngine.bindTexture(MekanismRenderer.getBlocksTexture());
			drawTexturedModelRectFromIcon(guiWidth + xPos, guiHeight + yPos + 58 - renderRemaining - start, MekanismRenderer.energyIcon, 16, 16 - (16 - renderRemaining));
			start+=16;

			if(renderRemaining == 0 || scale == 0)
			{
				break;
			}
		}

		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "InductionMatrixGui.png"));
		drawTexturedModalRect(guiWidth + xPos, guiHeight + yPos, 176, side == 0 ? 0 : 54, 16, 54);
	}
}
