package mekanism.client.gui;

import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyGauge;
import mekanism.client.gui.element.GuiEnergyGauge.IEnergyInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.MatrixTab;
import mekanism.client.gui.element.MatrixTab.MatrixTabType;
import mekanism.client.gui.element.RateBarGui;
import mekanism.client.gui.element.RateBarGui.IRateInfoHandler;
import mekanism.common.inventory.container.NullContainer;
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
public class MatrixStatsGui extends GuiMekanism
{
	public TileEntityInductionCasing tileEntity;

	public MatrixStatsGui(InventoryPlayer inventory, TileEntityInductionCasing tentity)
	{
		super(tentity, new NullContainer(inventory.player, tentity));
		tileEntity = tentity;
		guiElements.add(new MatrixTab(this, tileEntity, MatrixTabType.MAIN, 6, MekanismUtils.getResource(ResourceType.GUI, "NullGui.png")));
		guiElements.add(new GuiEnergyGauge(new IEnergyInfoHandler()
		{
			@Override
			public IStrictEnergyStorage getEnergyStorage()
			{
				return tileEntity;
			}
		}, GuiEnergyGauge.Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "NullGui.png"), 6, 13));
		guiElements.add(new RateBarGui(this, new IRateInfoHandler()
		{
			@Override
			public String getTooltip()
			{
				return LangUtils.localize("gui.receiving") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.structure.lastInput) + "/t";
			}

			@Override
			public double getLevel()
			{
				return tileEntity.structure.lastInput/tileEntity.structure.transferCap;
			}
		}, MekanismUtils.getResource(ResourceType.GUI, "NullGui.png"), 30, 13));
		guiElements.add(new RateBarGui(this, new IRateInfoHandler()
		{
			@Override
			public String getTooltip()
			{
				return LangUtils.localize("gui.outputting") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.structure.lastOutput) + "/t";
			}

			@Override
			public double getLevel()
			{
				return tileEntity.structure.lastOutput/tileEntity.structure.transferCap;
			}
		}, MekanismUtils.getResource(ResourceType.GUI, "NullGui.png"), 38, 13));
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
		}, this, MekanismUtils.getResource(ResourceType.GUI, "NullGui.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		String stats = LangUtils.localize("gui.matrixStats");
		fontRendererObj.drawString(stats, (xSize/2)-(fontRendererObj.getStringWidth(stats)/2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.input") + ":", 53, 26, 0x797979);
		fontRendererObj.drawString(MekanismUtils.getEnergyDisplay(tileEntity.structure.lastInput) + "/" + MekanismUtils.getEnergyDisplay(tileEntity.structure.transferCap), 59, 35, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.output") + ":", 53, 46, 0x797979);
		fontRendererObj.drawString(MekanismUtils.getEnergyDisplay(tileEntity.structure.lastOutput) + "/" + MekanismUtils.getEnergyDisplay(tileEntity.structure.transferCap), 59, 55, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.dimensions") + ":", 8, 82, 0x797979);
		fontRendererObj.drawString(tileEntity.structure.volWidth + " x " + tileEntity.structure.volHeight + " x " + tileEntity.structure.volLength, 14, 91, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.constituents") + ":", 8, 102, 0x797979);
		fontRendererObj.drawString(tileEntity.clientCells + " " + LangUtils.localize("gui.cells"), 14, 111, 0x404040);
		fontRendererObj.drawString(tileEntity.clientProviders + " " + LangUtils.localize("gui.providers"), 14, 120, 0x404040);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "NullGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
