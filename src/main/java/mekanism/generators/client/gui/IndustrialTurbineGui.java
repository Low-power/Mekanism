package mekanism.generators.client.gui;

import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.general;
import mekanism.api.MekanismConfig.generators;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.PowerBarGui;
import mekanism.client.gui.element.RateBarGui;
import mekanism.client.gui.element.RateBarGui.IRateInfoHandler;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.FilterContainer;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.GasTankTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.gui.element.TurbineTab;
import mekanism.generators.client.gui.element.TurbineTab.TurbineTabType;
import mekanism.generators.common.content.turbine.TurbineUpdateProtocol;
import mekanism.generators.common.tile.turbine.TurbineCasingTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class IndustrialTurbineGui extends GuiMekanism
{
	public TurbineCasingTileEntity tileEntity;

	public IndustrialTurbineGui(InventoryPlayer inventory, TurbineCasingTileEntity tentity)
	{
		super(tentity, new FilterContainer(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new TurbineTab(this, tileEntity, TurbineTabType.STAT, 6, MekanismUtils.getResource(ResourceType.GUI, "IndustrialTurbineGui.png")));
		guiElements.add(new PowerBarGui(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "IndustrialTurbineGui.png"), 164, 16));
		guiElements.add(new RateBarGui(this, new IRateInfoHandler()
		{
			@Override
			public String getTooltip()
			{
				return LangUtils.localize("gui.steamInput") + ": " + tileEntity.structure.lastSteamInput + " mB/t";
			}

			@Override
			public double getLevel()
			{
				double rate = tileEntity.structure.lowerVolume*(tileEntity.structure.clientDispersers*generators.turbineDisperserGasFlow);
				rate = Math.min(rate, tileEntity.structure.vents*generators.turbineVentGasFlow);
				if(rate == 0)
				{
					return 0;
				}
				return (double)tileEntity.structure.lastSteamInput/rate;
			}
		}, MekanismUtils.getResource(ResourceType.GUI, "IndustrialTurbineGui.png"), 40, 13));
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
		}, this, MekanismUtils.getResource(ResourceType.GUI, "IndustrialTurbineGui.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 5, 0x404040);
		double energyMultiplier = (general.maxEnergyPerSteam/TurbineUpdateProtocol.MAX_BLADES)*Math.min(tileEntity.structure.blades, tileEntity.structure.coils*generators.turbineBladesPerCoil);
		double rate = tileEntity.structure.lowerVolume*(tileEntity.structure.clientDispersers*generators.turbineDisperserGasFlow);
		rate = Math.min(rate, tileEntity.structure.vents*generators.turbineVentGasFlow);
		renderScaledText(LangUtils.localize("gui.production") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.structure.clientFlow*energyMultiplier), 53, 26, 0x00CD00, 106);
		renderScaledText(LangUtils.localize("gui.flowRate") + ": " + tileEntity.structure.clientFlow + " mB/t", 53, 35, 0x00CD00, 106);
		renderScaledText(LangUtils.localize("gui.capacity") + ": " + tileEntity.structure.getFluidCapacity() + " mB", 53, 44, 0x00CD00, 106);
		renderScaledText(LangUtils.localize("gui.maxFlow") + ": " + rate + " mB/t", 53, 53, 0x00CD00, 106);
		String name = chooseByMode(tileEntity.structure.dumpMode, LangUtils.localize("gui.idle"), LangUtils.localize("gui.dumping"), LangUtils.localize("gui.dumping_excess"));
		renderScaledText(name, 156-(int)(fontRendererObj.getStringWidth(name)*getNeededScale(name, 66)), 73, 0x404040, 66);
		if(xAxis >= 7 && xAxis <= 39 && yAxis >= 14 && yAxis <= 72)
		{
			drawCreativeTabHoveringText(tileEntity.structure.fluidStored != null ? LangUtils.localizeFluidStack(tileEntity.structure.fluidStored) + ": " + tileEntity.structure.fluidStored.amount + "mB" : LangUtils.localize("gui.empty"), xAxis, yAxis);
		}
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "IndustrialTurbineGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		int displayInt = chooseByMode(tileEntity.structure.dumpMode, 142, 150, 158);
		drawTexturedModalRect(guiWidth + 160, guiHeight + 73, 176, displayInt, 8, 8);
		if(tileEntity.getScaledFluidLevel(58) > 0)
		{
			displayGauge(7, 14, tileEntity.getScaledFluidLevel(58), tileEntity.structure.fluidStored, 0);
			displayGauge(23, 14, tileEntity.getScaledFluidLevel(58), tileEntity.structure.fluidStored, 1);
		}
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	public void displayGauge(int xPos, int yPos, int scale, FluidStack fluid, int side /*0-left, 1-right*/)
	{
		if(fluid == null)
		{
			return;
		}

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		int start = 0;

		while(true)
		{
			int renderRemaining = 0;

			if(scale > 16)
			{
				renderRemaining = 16;
				scale -= 16;
			}
			else {
				renderRemaining = scale;
				scale = 0;
			}

			mc.renderEngine.bindTexture(MekanismRenderer.getBlocksTexture());
			drawTexturedModelRectFromIcon(guiWidth + xPos, guiHeight + yPos + 58 - renderRemaining - start, fluid.getFluid().getIcon(), 16, 16 - (16 - renderRemaining));
			start+=16;

			if(renderRemaining == 0 || scale == 0)
			{
				break;
			}
		}

		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "IndustrialTurbineGui.png"));
		drawTexturedModalRect(guiWidth + xPos, guiHeight + yPos, 176, side == 0 ? 0 : 54, 16, 54);
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);

		int xAxis = (x - (width - xSize) / 2);
		int yAxis = (y - (height - ySize) / 2);

		if(xAxis > 160 && xAxis < 169 && yAxis > 73 && yAxis < 82)
		{
			ArrayList data = new ArrayList();
			data.add(Byte.valueOf((byte)0));

			Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
			SoundHandler.playSound("gui.button.press");
		}
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
