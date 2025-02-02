package mekanism.generators.client.gui;

import mekanism.api.EnumColor;
import mekanism.api.MekanismConfig.general;
import mekanism.api.MekanismConfig.generators;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.common.inventory.container.NullContainer;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.gui.element.TurbineTab;
import mekanism.generators.client.gui.element.TurbineTab.TurbineTabType;
import mekanism.generators.common.content.turbine.TurbineUpdateProtocol;
import mekanism.generators.common.tile.turbine.TurbineCasingTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TurbineStatsGui extends GuiMekanism
{
	public TurbineCasingTileEntity tileEntity;

	public TurbineStatsGui(InventoryPlayer inventory, TurbineCasingTileEntity tentity)
	{
		super(tentity, new NullContainer(inventory.player, tentity));
		tileEntity = tentity;
		guiElements.add(new TurbineTab(this, tileEntity, TurbineTabType.MAIN, 6, MekanismUtils.getResource(ResourceType.GUI, "NullGui.png")));
		guiElements.add(new EnergyInfoGui(new IInfoHandler()
		{
			@Override
			public List<String> getInfo()
			{
				double energyMultiplier = (general.maxEnergyPerSteam/TurbineUpdateProtocol.MAX_BLADES)*Math.min(tileEntity.structure.blades, tileEntity.structure.coils*generators.turbineBladesPerCoil);
				return ListUtils.asList(
						LangUtils.localize("gui.storing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()),
						LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.structure.clientFlow*energyMultiplier) + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "NullGui.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		String stats = LangUtils.localize("gui.turbineStats");
		String limiting = EnumColor.DARK_RED + " (" + LangUtils.localize("gui.limiting") + ")";
		fontRendererObj.drawString(stats, (xSize/2)-(fontRendererObj.getStringWidth(stats)/2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.tankVolume") + ": " + tileEntity.structure.lowerVolume, 8, 26, 0x404040);
		boolean dispersersLimiting = tileEntity.structure.lowerVolume*tileEntity.structure.clientDispersers*generators.turbineDisperserGasFlow <
				tileEntity.structure.vents*generators.turbineVentGasFlow;
		boolean ventsLimiting = tileEntity.structure.lowerVolume*tileEntity.structure.clientDispersers*generators.turbineDisperserGasFlow >
				tileEntity.structure.vents*generators.turbineVentGasFlow;
		fontRendererObj.drawString(LangUtils.localize("gui.steamFlow"), 8, 40, 0x797979);
		fontRendererObj.drawString(LangUtils.localize("gui.dispersers") + ": " + tileEntity.structure.clientDispersers + (dispersersLimiting ? limiting : ""), 14, 49, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.vents") + ": " + tileEntity.structure.vents + (ventsLimiting ? limiting : ""), 14, 58, 0x404040);
		boolean bladesLimiting = tileEntity.structure.coils*4 > tileEntity.structure.blades;
		boolean coilsLimiting = tileEntity.structure.coils*4 < tileEntity.structure.blades;
		fontRendererObj.drawString(LangUtils.localize("gui.production"), 8, 72, 0x797979);
		fontRendererObj.drawString(LangUtils.localize("gui.blades") + ": " + tileEntity.structure.blades + (bladesLimiting ? limiting : ""), 14, 81, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.coils") + ": " + tileEntity.structure.coils + (coilsLimiting ? limiting : ""), 14, 90, 0x404040);
		double energyMultiplier = (general.maxEnergyPerSteam/TurbineUpdateProtocol.MAX_BLADES)*Math.min(tileEntity.structure.blades, tileEntity.structure.coils*generators.turbineBladesPerCoil);
		double rate = tileEntity.structure.lowerVolume*(tileEntity.structure.clientDispersers*generators.turbineDisperserGasFlow);
		rate = Math.min(rate, tileEntity.structure.vents*generators.turbineVentGasFlow);
		fontRendererObj.drawString(LangUtils.localize("gui.maxProduction") + ": " + MekanismUtils.getEnergyDisplay(rate*energyMultiplier), 8, 104, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.maxWaterOutput") + ": " + tileEntity.structure.condensers*generators.condenserRate + " mB/t", 8, 113, 0x404040);

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
