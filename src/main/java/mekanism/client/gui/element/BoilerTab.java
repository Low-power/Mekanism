package mekanism.client.gui.element;

import mekanism.api.Coord4D;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.network.SimpleGuiPacket.SimpleGuiMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import codechicken.lib.vec.Rectangle4i;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class BoilerTab extends GuiElement
{
	private TileEntity tileEntity;
	private BoilerTabType tabType;
	private int yPos;

	public BoilerTab(IGuiWrapper gui, TileEntity tile, BoilerTabType type, int y, ResourceLocation def)
	{
		super(type.getResource(), gui, def);

		tileEntity = tile;
		tabType = type;
		yPos = y;
	}

	@Override
	public Rectangle4i getBounds(int guiWidth, int guiHeight)
	{
		return new Rectangle4i(guiWidth - 26, guiHeight + yPos, 26, 26);
	}

	@Override
	public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight)
	{
		mc.renderEngine.bindTexture(RESOURCE);

		guiObj.drawTexturedRect(guiWidth - 26, guiHeight + yPos, 0, 0, 26, 26);

		if(xAxis >= -21 && xAxis <= -3 && yAxis >= yPos+4 && yAxis <= yPos+22)
		{
			guiObj.drawTexturedRect(guiWidth - 21, guiHeight + yPos+4, 26, 0, 18, 18);
		}
		else {
			guiObj.drawTexturedRect(guiWidth - 21, guiHeight + yPos+4, 26, 18, 18, 18);
		}

		mc.renderEngine.bindTexture(defaultLocation);
	}

	@Override
	public void renderForeground(int xAxis, int yAxis)
	{
		mc.renderEngine.bindTexture(RESOURCE);

		if(xAxis >= -21 && xAxis <= -3 && yAxis >= yPos+4 && yAxis <= yPos+22)
		{
			displayTooltip(tabType.getDesc(), xAxis, yAxis);
		}

		mc.renderEngine.bindTexture(defaultLocation);
	}

	@Override
	public void preMouseClicked(int xAxis, int yAxis, int button) {}

	@Override
	public void mouseClicked(int xAxis, int yAxis, int button)
	{
		if(button == 0)
		{
			if(xAxis >= -21 && xAxis <= -3 && yAxis >= yPos+4 && yAxis <= yPos+22)
			{
				tabType.openGui(tileEntity);
				SoundHandler.playSound("gui.button.press");
			}
		}
	}

	public static enum BoilerTabType
	{
		MAIN("GasesTab.png", 54, "gui.main"),
		STAT("GuiStatsTab.png", 55, "gui.stats");

		private String path;
		private int guiId;
		private String desc;

		private BoilerTabType(String s, int id, String s1)
		{
			path = s;
			guiId = id;
			desc = s1;
		}

		public ResourceLocation getResource()
		{
			return MekanismUtils.getResource(ResourceType.GUI_ELEMENT, path);
		}

		public void openGui(TileEntity tile)
		{
			Mekanism.packetHandler.sendToServer(new SimpleGuiMessage(Coord4D.get(tile), 0, guiId));
		}

		public String getDesc()
		{
			return LangUtils.localize(desc);
		}
	}
}
