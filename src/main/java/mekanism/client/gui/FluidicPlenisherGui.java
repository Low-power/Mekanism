package mekanism.client.gui;

import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiFluidGauge;
import mekanism.client.gui.element.GuiFluidGauge.IFluidInfoHandler;
import mekanism.client.gui.element.GaugeGui;
import mekanism.client.gui.element.PowerBarGui;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiUpgradeTab;
import mekanism.common.inventory.container.FluidicPlenisherContainer;
import mekanism.common.tile.FluidicPlenisherTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import java.util.List;

@SideOnly(Side.CLIENT)
public class FluidicPlenisherGui extends GuiMekanism
{
	public FluidicPlenisherTileEntity tileEntity;

	public ResourceLocation guiLocation = MekanismUtils.getResource(ResourceType.GUI, "ElectricPumpGui.png");

	public FluidicPlenisherGui(InventoryPlayer inventory, FluidicPlenisherTileEntity tentity)
	{
		super(tentity, new FluidicPlenisherContainer(inventory, tentity));
		tileEntity = tentity;

		guiElements.add(new GuiSlot(SlotType.NORMAL, this, guiLocation, 27, 19));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, guiLocation, 27, 50));
		guiElements.add(new GuiSlot(SlotType.POWER, this, guiLocation, 142, 34).with(SlotOverlay.POWER));
		guiElements.add(new PowerBarGui(this, tileEntity, guiLocation, 164, 15));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler() {
			@Override
			public FluidTank getTank()
			{
				return tileEntity.fluidTank;
			}
		}, GaugeGui.Type.STANDARD, this, guiLocation, 6, 13));
		guiElements.add(new EnergyInfoGui(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.energyPerTick);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, guiLocation));
		guiElements.add(new GuiRedstoneControl(this, tileEntity, guiLocation));
		guiElements.add(new GuiUpgradeTab(this, tileEntity, guiLocation));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize / 2) - (fontRendererObj.getStringWidth(tileEntity.getInventoryName()) / 2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 94) + 2, 0x404040);
		fontRendererObj.drawString(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()), 51, 26, 0x00CD00);
		fontRendererObj.drawString(LangUtils.localize("gui.finished") + ": " + LangUtils.transYesNo(tileEntity.finishedCalc), 51, 35, 0x00CD00);
		FluidStack fluid_stack = tileEntity.fluidTank.getFluid();
		fontRendererObj.drawString(fluid_stack != null ? String.format("%s: %d", LangUtils.localizeFluidStack(fluid_stack), fluid_stack.amount) : LangUtils.localize("gui.noFluid"), 51, 44, 0x00CD00);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(guiLocation);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
