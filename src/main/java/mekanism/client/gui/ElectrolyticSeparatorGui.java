package mekanism.client.gui;

import mekanism.api.Coord4D;
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
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiUpgradeTab;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ElectrolyticSeparatorContainer;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.ElectrolyticSeparatorTileEntity;
import mekanism.common.tile.GasTankTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.FluidTank;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ElectrolyticSeparatorGui extends GuiMekanism
{
	public ElectrolyticSeparatorTileEntity tileEntity;

	public ElectrolyticSeparatorGui(InventoryPlayer inventory, ElectrolyticSeparatorTileEntity tentity)
	{
		super(tentity, new ElectrolyticSeparatorContainer(inventory, tentity));

		tileEntity = tentity;

		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png")));
		guiElements.add(new GuiUpgradeTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png")));
		guiElements.add(new EnergyInfoGui(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String usage = MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + usage + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png")));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler() {
			@Override
			public FluidTank getTank()
			{
				return tileEntity.fluidTank;
			}
		}, GaugeGui.Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 5, 10));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.leftTank;
			}
		}, GaugeGui.Type.SMALL, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 58, 18));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.rightTank;
			}
		}, GaugeGui.Type.SMALL, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 100, 18));
		guiElements.add(new PowerBarGui(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 164, 15));

		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 25, 34));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 58, 51));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 100, 51));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 142, 34).with(SlotOverlay.POWER));

		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.isActive ? 1 : 0;
			}
		}, ProgressBar.BI, this, MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"), 78, 29));
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);

		int xAxis = (x - (width - xSize) / 2);
		int yAxis = (y - (height - ySize) / 2);

		if(xAxis > 8 && xAxis < 17 && yAxis > 73 && yAxis < 82)
		{
			ArrayList data = new ArrayList();
			data.add(Byte.valueOf((byte)0));

			Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
			SoundHandler.playSound("gui.button.press");
		}
		else if(xAxis > 160 && xAxis < 169 && yAxis > 73 && yAxis < 82)
		{
			ArrayList data = new ArrayList();
			data.add(Byte.valueOf((byte)1));

			Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
			SoundHandler.playSound("gui.button.press");
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(tileEntity.getInventoryName(), 45, 6, 0x404040);

		String name = chooseByMode(tileEntity.dumpLeft, LangUtils.localize("gui.idle"), LangUtils.localize("gui.dumping"), LangUtils.localize("gui.dumping_excess"));
		renderScaledText(name, 21, 73, 0x404040, 66);

		name = chooseByMode(tileEntity.dumpRight, LangUtils.localize("gui.idle"), LangUtils.localize("gui.dumping"), LangUtils.localize("gui.dumping_excess"));
		renderScaledText(name, 156-(int)(fontRendererObj.getStringWidth(name)*getNeededScale(name, 66)), 73, 0x404040, 66);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "ElectrolyticSeparatorGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int displayInt = chooseByMode(tileEntity.dumpLeft, 52, 60, 68);
		drawTexturedModalRect(guiWidth + 8, guiHeight + 73, 176, displayInt, 8, 8);

		displayInt = chooseByMode(tileEntity.dumpRight, 52, 60, 68);
		drawTexturedModalRect(guiWidth + 160, guiHeight + 73, 176, displayInt, 8, 8);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	private <T> T chooseByMode(GasTankTileEntity.GasMode dumping, T idleOption, T dumpingOption, T dumpingExcessOption)
	{
		if(dumping.equals(GasTankTileEntity.GasMode.IDLE))
		{
			return idleOption;
		}
		else if(dumping.equals(GasTankTileEntity.GasMode.DUMPING))
		{
			return dumpingOption;
		}
		else if(dumping.equals(GasTankTileEntity.GasMode.DUMPING_EXCESS))
		{
			return dumpingExcessOption;
		}
		return idleOption; //should not happen;
	}
}
