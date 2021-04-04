package mekanism.client.gui;

import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyGauge;
import mekanism.client.gui.element.GuiEnergyGauge.IEnergyInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSideConfigurationTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiTransporterConfigTab;
import mekanism.common.inventory.container.EnergyCubeContainer;
import mekanism.common.tile.EnergyCubeTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class EnergyCubeGui extends GuiMekanism
{
	public EnergyCubeTileEntity tileEntity;

	public EnergyCubeGui(InventoryPlayer inventory, EnergyCubeTileEntity tentity)
	{
		super(tentity, new EnergyCubeContainer(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "EnergyCubeGui.png")));
		guiElements.add(new GuiSideConfigurationTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "EnergyCubeGui.png")));
		guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "EnergyCubeGui.png")));
		guiElements.add(new GuiEnergyGauge(new IEnergyInfoHandler()
		{
			@Override
			public IStrictEnergyStorage getEnergyStorage()
			{
				return tileEntity;
			}
		}, GuiEnergyGauge.Type.WIDE, this, MekanismUtils.getResource(ResourceType.GUI, "EnergyCubeGui.png"), 55, 18));
		guiElements.add(new EnergyInfoGui(new IInfoHandler()
		{
			@Override
			public List<String> getInfo()
			{
				return ListUtils.asList(
						LangUtils.localize("gui.storing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()),
						LangUtils.localize("gui.maxOutput") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput()) + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "EnergyCubeGui.png")));
		guiElements.add(new GuiSlot(SlotType.INPUT, this, MekanismUtils.getResource(ResourceType.GUI, "EnergyCubeGui.png"), 16, 34).with(SlotOverlay.MINUS));
		guiElements.add(new GuiSlot(SlotType.OUTPUT, this, MekanismUtils.getResource(ResourceType.GUI, "EnergyCubeGui.png"), 142, 34).with(SlotOverlay.PLUS));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, ySize - 96 + 2, 0x404040);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "EnergyCubeGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
