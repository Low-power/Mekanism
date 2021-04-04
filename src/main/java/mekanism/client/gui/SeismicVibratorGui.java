package mekanism.client.gui;

import mekanism.api.MekanismConfig.usage;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.inventory.container.SeismicVibratorContainer;
import mekanism.common.tile.SeismicVibratorTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SeismicVibratorGui extends GuiMekanism
{
	public SeismicVibratorTileEntity tileEntity;

	public SeismicVibratorGui(InventoryPlayer inventory, SeismicVibratorTileEntity tentity)
	{
		super(tentity, new SeismicVibratorContainer(inventory, tentity));
		tileEntity = tentity;

		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "SeismicVibratorGui.png")));
		guiElements.add(new GuiPowerBar(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "SeismicVibratorGui.png"), 164, 15));
		guiElements.add(new EnergyInfoGui(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(usage.seismicVibratorUsage);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "SeismicVibratorGui.png")));

		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "SeismicVibratorGui.png"), 142, 34).with(SlotOverlay.POWER));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(tileEntity.getInventoryName(), 45, 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
		fontRendererObj.drawString(tileEntity.isActive ? LangUtils.localize("gui.vibrating") : LangUtils.localize("gui.idle"), 19, 26, 0x00CD00);
		fontRendererObj.drawString(LangUtils.localize("gui.chunk") + ": " + (tileEntity.xCoord >> 4) + ", " + (tileEntity.zCoord >> 4), 19, 35, 0x00CD00);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "SeismicVibratorGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int xAxis = mouseX - guiWidth;
		int yAxis = mouseY - guiHeight;

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
