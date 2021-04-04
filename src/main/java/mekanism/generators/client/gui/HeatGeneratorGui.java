package mekanism.generators.client.gui;

import mekanism.api.MekanismConfig.general;
import mekanism.api.util.ListUtils;
import mekanism.api.util.UnitDisplayUtils;
import mekanism.api.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiFluidGauge;
import mekanism.client.gui.element.GuiFluidGauge.IFluidInfoHandler;
import mekanism.client.gui.element.GaugeGui.Type;
import mekanism.client.gui.element.GuiHeatInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.common.inventory.container.HeatGeneratorContainer;
import mekanism.generators.common.tile.HeatGeneratorTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.FluidTank;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class HeatGeneratorGui extends GuiMekanism
{
	public HeatGeneratorTileEntity tileEntity;

	public HeatGeneratorGui(InventoryPlayer inventory, HeatGeneratorTileEntity tentity)
	{
		super(new HeatGeneratorContainer(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "HeatGeneratorGui.png")));
		guiElements.add(new EnergyInfoGui(new IInfoHandler()
		{
			@Override
			public List<String> getInfo()
			{
				return ListUtils.asList(
						LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.producingEnergy) + "/t",
						LangUtils.localize("gui.maxOutput") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput()) + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "HeatGeneratorGui.png")));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler() {
			@Override
			public FluidTank getTank()
			{
				return tileEntity.lavaTank;
			}
		}, Type.WIDE, this, MekanismUtils.getResource(ResourceType.GUI, "HeatGeneratorGui.png"), 55, 18));
		guiElements.add(new GuiPowerBar(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "HeatGeneratorGui.png"), 164, 15));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "HeatGeneratorGui.png"), 16, 34));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "HeatGeneratorGui.png"), 142, 34).with(SlotOverlay.POWER));
		guiElements.add(new GuiHeatInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				TemperatureUnit unit = TemperatureUnit.values()[general.tempUnit.ordinal()];
				String transfer = UnitDisplayUtils.getDisplayShort(tileEntity.lastTransferLoss, false, unit);
				String environment = UnitDisplayUtils.getDisplayShort(tileEntity.lastEnvironmentLoss, false, unit);
				return ListUtils.asList(LangUtils.localize("gui.transferred") + ": " + transfer + "/t", LangUtils.localize("gui.dissipated") + ": " + environment + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "HeatGeneratorGui.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRendererObj.drawString(tileEntity.getInventoryName(), 45, 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "HeatGeneratorGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
