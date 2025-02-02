package mekanism.client.gui;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.client.gui.SideConfigurationGui.GuiPos;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.inventory.container.NullContainer;
import mekanism.common.network.PacketConfigurationUpdate.ConfigurationPacket;
import mekanism.common.network.PacketConfigurationUpdate.ConfigurationUpdateMessage;
import mekanism.common.network.SimpleGuiPacket.SimpleGuiMessage;
import mekanism.common.tile.ContainerTileEntity;
import mekanism.common.tile.component.ConfigTileComponent;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class TransporterConfigGui extends GuiMekanism
{
	public Map<Integer, GuiPos> slotPosMap = new HashMap<Integer, GuiPos>();

	public ISideConfiguration configurable;

	public TransporterConfigGui(EntityPlayer player, ISideConfiguration tile)
	{
		super((ContainerTileEntity)tile, new NullContainer(player, (ContainerTileEntity)tile));

		ySize = 95;

		configurable = tile;

		slotPosMap.put(0, new GuiPos(54, 64));
		slotPosMap.put(1, new GuiPos(54, 34));
		slotPosMap.put(2, new GuiPos(54, 49));
		slotPosMap.put(3, new GuiPos(39, 64));
		slotPosMap.put(4, new GuiPos(39, 49));
		slotPosMap.put(5, new GuiPos(69, 49));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

		GL11.glColor4f(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "TransporterConfigGui.png"));

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		if(xAxis >= 6 && xAxis <= 20 && yAxis >= 6 && yAxis <= 20)
		{
			drawTexturedModalRect(guiWidth + 6, guiHeight + 6, 176 + 14, 0, 14, 14);
		}
		else {
			drawTexturedModalRect(guiWidth + 6, guiHeight + 6, 176 + 14, 14, 14, 14);
		}
		if(xAxis >= 156 && xAxis <= 170 && yAxis >= 6 && yAxis <= 20)
		{
			drawTexturedModalRect(guiWidth + 156, guiHeight + 6, 176 + 28, 0, 14, 14);
		}
		else {
			drawTexturedModalRect(guiWidth + 156, guiHeight + 6, 176 + 28, 14, 14, 14);
		}
		for(int i = 0; i < slotPosMap.size(); i++)
		{
			MekanismRenderer.resetColor();

			int x = slotPosMap.get(i).xPos;
			int y = slotPosMap.get(i).yPos;

			EnumColor color = configurable.getEjector().getInputColor(ForgeDirection.getOrientation(i));

			if(configurable.getConfig().getOutput(TransmissionType.ITEM, i) != ConfigTileComponent.EMPTY)
			{
				if(color != null)
				{
					MekanismRenderer.color(color);
				}
				if(xAxis >= x && xAxis <= x+14 && yAxis >= y && yAxis <= y+14)
				{
					drawTexturedModalRect(guiWidth + x, guiHeight + y, 176, 0, 14, 14);
				}
				else {
					drawTexturedModalRect(guiWidth + x, guiHeight + y, 176, 14, 14, 14);
				}
			}
			else {
				drawTexturedModalRect(guiWidth + x, guiHeight + y, 176, 28, 14, 14);
			}
		}

		MekanismRenderer.resetColor();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		String text = LangUtils.localize("gui.configuration.transporter");
		fontRendererObj.drawString(text, (xSize/2)-(fontRendererObj.getStringWidth(text)/2), 5, 0x404040);
		text = LangUtils.localize("gui.strictInput") + " (" + LangUtils.transOnOff(configurable.getEjector().hasStrictInput()) + ")";
		renderScaledText(text, 53, 17, 0x00CD00, 70);
		fontRendererObj.drawString(LangUtils.localize("gui.input"), 48, 81, 0x787878);
		fontRendererObj.drawString(LangUtils.localize("gui.output"), 114, 68, 0x787878);
		if(configurable.getEjector().getOutputColor() != null)
		{
			GL11.glPushMatrix();
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);

			mc.getTextureManager().bindTexture(MekanismRenderer.getBlocksTexture());
			itemRender.renderIcon(122, 49, MekanismRenderer.getColorIcon(configurable.getEjector().getOutputColor()), 16, 16);

			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
		for(int i = 0; i < slotPosMap.size(); i++)
		{
			int x = slotPosMap.get(i).xPos;
			int y = slotPosMap.get(i).yPos;

			EnumColor color = configurable.getEjector().getInputColor(ForgeDirection.getOrientation(i));

			if(configurable.getConfig().getOutput(TransmissionType.ITEM, i) != ConfigTileComponent.EMPTY)
			{
				if(xAxis >= x && xAxis <= x+14 && yAxis >= y && yAxis <= y+14)
				{
					drawCreativeTabHoveringText(color != null ? color.getName() : LangUtils.localize("gui.none"), xAxis, yAxis);
				}
			}
		}
		if(xAxis >= 122 && xAxis <= 138 && yAxis >= 49 && yAxis <= 65)
		{
			if(configurable.getEjector().getOutputColor() != null)
			{
				drawCreativeTabHoveringText(configurable.getEjector().getOutputColor().getName(), xAxis, yAxis);
			}
			else {
				drawCreativeTabHoveringText(LangUtils.localize("gui.none"), xAxis, yAxis);
			}
		}
		if(xAxis >= 156 && xAxis <= 170 && yAxis >= 6 && yAxis <= 20)
		{
			drawCreativeTabHoveringText(LangUtils.localize("gui.configuration.strictInput"), xAxis, yAxis);
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);

		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		TileEntity tile = (TileEntity)configurable;

		if(button == 0)
		{
			if(xAxis >= 6 && xAxis <= 20 && yAxis >= 6 && yAxis <= 20)
			{
				int guiId = Mekanism.proxy.getGuiId(tile.getBlockType(), tile.getBlockMetadata());
				SoundHandler.playSound("gui.button.press");
				Mekanism.packetHandler.sendToServer(new SimpleGuiMessage(Coord4D.get(tile), 0, guiId));
			}
			if(xAxis >= 156 && xAxis <= 170 && yAxis >= 6 && yAxis <= 20)
			{
				SoundHandler.playSound("gui.button.press");
				Mekanism.packetHandler.sendToServer(new ConfigurationUpdateMessage(ConfigurationPacket.STRICT_INPUT, Coord4D.get(tile), 0, 0, null));
			}
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && button == 0)
		{
			button = 2;
		}

		if(xAxis >= 122 && xAxis <= 138 && yAxis >= 49 && yAxis <= 65)
		{
			SoundHandler.playSound("gui.button.press");
			Mekanism.packetHandler.sendToServer(new ConfigurationUpdateMessage(ConfigurationPacket.EJECT_COLOR, Coord4D.get(tile), button, 0, null));
		}

		for(int i = 0; i < slotPosMap.size(); i++)
		{
			int x = slotPosMap.get(i).xPos;
			int y = slotPosMap.get(i).yPos;

			if(xAxis >= x && xAxis <= x+14 && yAxis >= y && yAxis <= y+14)
			{
				SoundHandler.playSound("gui.button.press");
				Mekanism.packetHandler.sendToServer(new ConfigurationUpdateMessage(ConfigurationPacket.INPUT_COLOR, Coord4D.get(tile), button, i, null));
			}
		}
	}
}
