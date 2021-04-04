package mekanism.client.gui;

import mekanism.api.gas.GasStack;
import mekanism.api.Coord4D;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSideConfigurationTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiTransporterConfigTab;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.GasTankContainer;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.GasTankTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class GasTankGui extends GuiMekanism
{
	public GasTankTileEntity tileEntity;

	public GasTankGui(InventoryPlayer inventory, GasTankTileEntity tentity)
	{
		super(tentity, new GasTankContainer(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GasTankGui.png")));
		guiElements.add(new GuiSideConfigurationTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GasTankGui.png")));
		guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GasTankGui.png")));
		guiElements.add(new GuiSlot(SlotType.OUTPUT, this, MekanismUtils.getResource(ResourceType.GUI, "GasTankGui.png"), 7, 7).with(SlotOverlay.PLUS));
		guiElements.add(new GuiSlot(SlotType.INPUT, this, MekanismUtils.getResource(ResourceType.GUI, "GasTankGui.png"), 7, 39).with(SlotOverlay.MINUS));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		String capacityInfo = tileEntity.gasTank.getStored() + "/" + tileEntity.tier.storage;

		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize / 2) - (fontRendererObj.getStringWidth(tileEntity.getInventoryName()) / 2), 6, 0x404040);
		fontRendererObj.drawString(capacityInfo, 45, 40, 0x404040);
		GasStack gas_stack = tileEntity.gasTank.getGas();
		renderScaledText(LangUtils.localize("gui.gas") + ": " + (gas_stack != null ? gas_stack.getGas().getLocalizedName() : LangUtils.localize("gui.none")), 45, 49, 0x404040, 112);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, ySize - 96 + 2, 0x404040);

		String name = chooseByMode(tileEntity.dumping, LangUtils.localize("gui.idle"), LangUtils.localize("gui.dumping"), LangUtils.localize("gui.dumping_excess"));
		fontRendererObj.drawString(name, 156 - fontRendererObj.getStringWidth(name), 73, 0x404040);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GasTankGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int displayInt = chooseByMode(tileEntity.dumping, 10, 18, 26);
		drawTexturedModalRect(guiWidth + 160, guiHeight + 73, 176, displayInt, 8, 8);

		if(tileEntity.gasTank.getGas() != null)
		{
			int scale = (int)(((double)tileEntity.gasTank.getStored() / tileEntity.tier.storage) * 72);
			drawTexturedModalRect(guiWidth + 65, guiHeight + 17, 176, 0, scale, 10);
		}

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
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
			data.add(Integer.valueOf(0));

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
