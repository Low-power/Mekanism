package mekanism.client.gui;

import mekanism.api.gas.GasTank;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiGasGauge;
import mekanism.client.gui.element.GuiGasGauge.IGasInfoHandler;
import mekanism.client.gui.element.GaugeGui;
import mekanism.client.gui.element.ProgressGui;
import mekanism.client.gui.element.ProgressGui.IProgressInfoHandler;
import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiUpgradeTab;
import mekanism.common.inventory.container.ChemicalInfuserContainer;
import mekanism.common.tile.ChemicalInfuserTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ChemicalInfuserGui extends GuiMekanism
{
	public ChemicalInfuserTileEntity tileEntity;

	public ChemicalInfuserGui(InventoryPlayer inventory, ChemicalInfuserTileEntity tentity)
	{
		super(tentity, new ChemicalInfuserContainer(inventory, tentity));
		tileEntity = tentity;

		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png")));
		guiElements.add(new GuiUpgradeTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png")));
		guiElements.add(new EnergyInfoGui(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String usage = MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + usage + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this,  MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png")));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.leftTank;
			}
		}, GaugeGui.Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 25, 13));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.centerTank;
			}
		}, GaugeGui.Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 79, 4));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.rightTank;
			}
		}, GaugeGui.Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 133, 13));

		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 154, 4).with(SlotOverlay.POWER));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 154, 55).with(SlotOverlay.MINUS));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 4, 55).with(SlotOverlay.MINUS));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 79, 64).with(SlotOverlay.PLUS));

		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.isActive ? 1 : 0;
			}
		}, ProgressBar.SMALL_RIGHT, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 45, 38));
		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.isActive ? 1 : 0;
			}
		}, ProgressBar.SMALL_LEFT, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"), 99, 38));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(LangUtils.localize("gui.chemicalInfuser.short"), 5, 5, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
		if(xAxis >= 116 && xAxis <= 168 && yAxis >= 76 && yAxis <= 80)
		{
			drawCreativeTabHoveringText(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()), xAxis, yAxis);
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "ChemicalInfuserGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		int displayInt;

		displayInt = tileEntity.getScaledEnergyLevel(52);
		drawTexturedModalRect(guiWidth + 116, guiHeight + 76, 176, 0, displayInt, 4);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
