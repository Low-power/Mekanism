package mekanism.generators.client.gui;

import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.gui.element.GuiFuelTab;
import mekanism.generators.client.gui.element.GuiHeatTab;
import mekanism.generators.client.gui.element.GuiStatTab;
import mekanism.generators.common.inventory.container.ContainerReactorController;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ReactorControllerGui extends GuiMekanism
{
	public TileEntityReactorController tileEntity;

	public ReactorControllerGui(InventoryPlayer inventory, final TileEntityReactorController tentity)
	{
		super(new ContainerReactorController(inventory, tentity));
		tileEntity = tentity;
		if(tileEntity.isFormed())
		{
			guiElements.add(new EnergyInfoGui(new IInfoHandler()
			{
				@Override
				public List<String> getInfo()
				{
					return tileEntity.isFormed() ? ListUtils.asList(
							LangUtils.localize("gui.storing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()),
							LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getReactor().getPassiveGeneration(false, true)) + "/t") : new ArrayList();
				}
			}, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
			guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 79, 38));
			guiElements.add(new GuiHeatTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
			guiElements.add(new GuiFuelTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
			guiElements.add(new GuiStatTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(tileEntity.getInventoryName(), 46, 6, 0x404040);
		if(tileEntity.getActive())
		{
			fontRendererObj.drawString(LangUtils.localize("gui.formed"), 8, 16, 0x404040);
		}
		else {
			fontRendererObj.drawString(LangUtils.localize("gui.incomplete"), 8, 16, 0x404040);
		}
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

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
