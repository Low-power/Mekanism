package mekanism.client.gui;

import mekanism.api.MekanismConfig.general;
import mekanism.api.util.ListUtils;
import mekanism.api.util.UnitDisplayUtils;
import mekanism.api.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiHeatInfo;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.inventory.container.FuelwoodHeaterContainer;
import mekanism.common.tile.FuelwoodHeaterTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class FuelwoodHeaterGui extends GuiMekanism
{
	public FuelwoodHeaterTileEntity tileEntity;

	public FuelwoodHeaterGui(InventoryPlayer inventory, FuelwoodHeaterTileEntity tentity)
	{
		super(tentity, new FuelwoodHeaterContainer(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "FuelwoodHeaterGui.png"), 14, 28));
		guiElements.add(new GuiHeatInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				TemperatureUnit unit = TemperatureUnit.values()[general.tempUnit.ordinal()];
				String environment = UnitDisplayUtils.getDisplayShort(tileEntity.lastEnvironmentLoss*unit.intervalSize, false, unit);
				return ListUtils.asList(LangUtils.localize("gui.dissipated") + ": " + environment + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "FuelwoodHeaterGui.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize / 2) - (fontRendererObj.getStringWidth(tileEntity.getInventoryName()) / 2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 94) + 2, 0x404040);
		renderScaledText(LangUtils.localize("gui.temp") + ": " + MekanismUtils.getTemperatureDisplay(tileEntity.temperature, TemperatureUnit.AMBIENT), 50, 25, 0x00CD00, 76);
		renderScaledText(LangUtils.localize("gui.fuel") + ": " + tileEntity.burnTime, 50, 41, 0x00CD00, 76);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "FuelwoodHeaterGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		if(tileEntity.burnTime > 0)
		{
			int displayInt = tileEntity.burnTime*13 / tileEntity.maxBurnTime;
			drawTexturedModalRect(guiWidth + 143, guiHeight + 30 + 12 - displayInt, 176, 12 - displayInt, 14, displayInt + 1);
		}
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
