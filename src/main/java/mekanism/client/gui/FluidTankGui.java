package mekanism.client.gui;

import mekanism.client.gui.element.ContainerEditModeGui;
import mekanism.client.gui.element.GuiFluidGauge;
import mekanism.client.gui.element.GuiFluidGauge.IFluidInfoHandler;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.inventory.container.FluidTankContainer;
import mekanism.common.tile.FluidTankTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FluidTankGui extends GuiMekanism
{
	public FluidTankTileEntity tileEntity;

	public FluidTankGui(InventoryPlayer inventory, FluidTankTileEntity tentity)
	{
		super(tentity, new FluidTankContainer(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new ContainerEditModeGui(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png")));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler()
		{
			@Override
			public FluidTank getTank()
			{
				return tileEntity.fluidTank;
			}
		}, GuiFluidGauge.Type.WIDE, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 48, 18));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 145, 18).with(SlotOverlay.INPUT));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, MekanismUtils.getResource(ResourceType.GUI, "BlankGui.png"), 145, 50).with(SlotOverlay.OUTPUT));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, ySize - 96 + 2, 0x404040);

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
