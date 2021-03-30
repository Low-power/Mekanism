package mekanism.client.gui;

import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CreditsGui extends GuiScreen
{
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 96 + 12, "Close"));
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(guibutton.id == 1)
		{
			mc.displayGuiScreen(null);
		}
	}

	public void writeText(String text, int yAxis)
	{
		drawString(fontRendererObj, text, width / 2 - 140, (height / 4 - 60) + 20 + yAxis, 0xa0a0a0);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, EnumColor.DARK_BLUE + "Mekanism" + EnumColor.GREY + " by aidancbrady", width / 2, (height / 4 - 60) + 20, 0xffffff);

		if(Mekanism.latestVersionNumber != null && !Mekanism.latestVersionNumber.equals("null"))
		{
			writeText(EnumColor.INDIGO + "Mekanism " + (Mekanism.versionNumber.comparedState(Version.get(Mekanism.latestVersionNumber)) == -1 ? EnumColor.DARK_RED : EnumColor.GREY) + Mekanism.versionNumber, 36);
		}
		else {
			writeText(EnumColor.INDIGO + "Mekanism " + EnumColor.GREY + Mekanism.versionNumber, 36);
		}

		int size = 36;

		for(IModule module : Mekanism.modulesLoaded)
		{
			size += 9;

			if(Mekanism.latestVersionNumber != null && !Mekanism.latestVersionNumber.equals("null"))
			{
				writeText(EnumColor.INDIGO + "Mekanism" + module.getName() + (module.getVersion().comparedState(Version.get(Mekanism.latestVersionNumber)) == -1 ? EnumColor.DARK_RED : EnumColor.GREY) + " " + module.getVersion(), size);
			}
			else {
				writeText(EnumColor.INDIGO + "Mekanism" + module.getName() + EnumColor.GREY + " " + module.getVersion(), size);
			}
		}

		//writeText(EnumColor.GREY + "Newest version: " + Mekanism.latestVersionNumber, size+9);
		writeText(EnumColor.GREY + "*Developed on Mac OS X 10.8 Mountain Lion", size+18);
		writeText(EnumColor.GREY + "*Code, textures, and ideas by aidancbrady", size+27);
		//writeText(EnumColor.GREY + "Recent news: " + EnumColor.DARK_BLUE + (!Mekanism.recentNews.contains("null") ? Mekanism.recentNews : "couldn't access."), size+36);

		super.drawScreen(mouseX, mouseY, partialTick);
	}
}
