package mekanism.client.gui;

import mekanism.api.Coord4D;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
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
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.MetallurgicInfuserContainer;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.MetallurgicInfuserTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class MetallurgicInfuserGui extends GuiMekanism
{
	public MetallurgicInfuserTileEntity tileEntity;

	public MetallurgicInfuserGui(InventoryPlayer inventory, MetallurgicInfuserTileEntity tentity)
	{
		super(tentity, new MetallurgicInfuserContainer(inventory, tentity));
		tileEntity = tentity;

		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png")));
		guiElements.add(new GuiUpgradeTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png")));
		guiElements.add(new GuiSideConfigurationTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png")));
		guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png")));
		guiElements.add(new PowerBarGui(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png"), 164, 15));
		guiElements.add(new EnergyInfoGui(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.energyPerTick);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png")));

		guiElements.add(new GuiSlot(SlotType.EXTRA, this, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png"), 16, 34));
		guiElements.add(new GuiSlot(SlotType.INPUT, this, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png"), 50, 42));
		guiElements.add(new GuiSlot(SlotType.POWER, this, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png"), 142, 34).with(SlotOverlay.POWER));
		guiElements.add(new GuiSlot(SlotType.OUTPUT, this, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png"), 108, 42));

		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getScaledProgress();
			}
		}, ProgressBar.MEDIUM, this, MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png"), 70, 46));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(tileEntity.getInventoryName(), 45, 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);

		if(xAxis >= 7 && xAxis <= 11 && yAxis >= 17 && yAxis <= 69)
		{
			drawCreativeTabHoveringText(tileEntity.infuseStored.type != null ? tileEntity.infuseStored.type.getLocalizedName() + ": " + tileEntity.infuseStored.amount : LangUtils.localize("gui.empty"), xAxis, yAxis);
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "MetallurgicInfuserGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		if(tileEntity.infuseStored.type != null)
		{
			mc.renderEngine.bindTexture(MekanismRenderer.getBlocksTexture());
			int displayInt = tileEntity.getScaledInfuseLevel(52);
			drawTexturedRectFromIcon(guiWidth + 7, guiHeight + 17 + 52 - displayInt, tileEntity.infuseStored.type.icon, 4, displayInt);
		}

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);

		if(button == 0)
		{
			int xAxis = (x - (width - xSize) / 2);
			int yAxis = (y - (height - ySize) / 2);

			if(xAxis > 148 && xAxis < 168 && yAxis > 73 && yAxis < 82)
			{
				ArrayList data = new ArrayList();
				data.add(Integer.valueOf(0));

				Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
				SoundHandler.playSound("gui.button.press");
			}
		}
	}
}
