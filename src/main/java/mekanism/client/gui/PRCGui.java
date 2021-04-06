package mekanism.client.gui;

import mekanism.api.gas.GasTank;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiFluidGauge;
import mekanism.client.gui.element.GuiFluidGauge.IFluidInfoHandler;
import mekanism.client.gui.element.GuiGasGauge;
import mekanism.client.gui.element.GuiGasGauge.IGasInfoHandler;
import mekanism.client.gui.element.GaugeGui;
import mekanism.client.gui.element.PowerBarGui;
import mekanism.client.gui.element.ProgressGui;
import mekanism.client.gui.element.ProgressGui.IProgressInfoHandler;
import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSideConfigurationTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiTransporterConfigTab;
import mekanism.client.gui.element.GuiUpgradeTab;
import mekanism.common.inventory.container.PRCContainer;
import mekanism.common.tile.PRCTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.FluidTank;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PRCGui extends GuiMekanism
{
	public PRCTileEntity tileEntity;

	public PRCGui(InventoryPlayer inventory, PRCTileEntity tentity)
	{
		super(tentity, new PRCContainer(inventory, tentity));
		tileEntity = tentity;

		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
		guiElements.add(new GuiSideConfigurationTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
		guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
		guiElements.add(new GuiUpgradeTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
		guiElements.add(new EnergyInfoGui(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				double extra = tileEntity.getRecipe() != null ? tileEntity.getRecipe().extraEnergy : 0;
				String multiplier = MekanismUtils.getEnergyDisplay(MekanismUtils.getEnergyPerTick(tileEntity, tileEntity.BASE_ENERGY_PER_TICK + extra));
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler() {
			@Override
			public FluidTank getTank()
			{
				return tileEntity.inputFluidTank;
			}
		}, GaugeGui.Type.STANDARD_YELLOW, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 5, 10));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.inputGasTank;
			}
		}, GaugeGui.Type.STANDARD_RED, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 28, 10));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.outputGasTank;
			}
		}, GaugeGui.Type.SMALL_BLUE, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 140, 40));
		guiElements.add(new PowerBarGui(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 164, 15));

		guiElements.add(new GuiSlot(SlotType.INPUT, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 53, 34));
		guiElements.add(new GuiSlot(SlotType.POWER, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 140, 18).with(SlotOverlay.POWER));
		guiElements.add(new GuiSlot(SlotType.OUTPUT, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 115, 34));

		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getScaledProgress();
			}
		}, getProgressType(), this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 75, 37));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 6, 0x404040);
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

		int xAxis = mouseX - guiWidth;
		int yAxis = mouseY - guiHeight;

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	public ProgressBar getProgressType()
	{
		return ProgressBar.MEDIUM;
	}
}
