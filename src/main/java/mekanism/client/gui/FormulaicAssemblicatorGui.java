package mekanism.client.gui;

import mekanism.api.Coord4D;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.PowerBarGui;
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
import mekanism.common.inventory.container.FormulaicAssemblicatorContainer;
import mekanism.common.item.ItemCraftingFormula;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.FormulaicAssemblicatorTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class FormulaicAssemblicatorGui extends GuiMekanism
{
	public FormulaicAssemblicatorTileEntity tileEntity;

	public ResourceLocation guiLocation = MekanismUtils.getResource(ResourceType.GUI, "FormulaicAssemblicatorGui.png");

	public FormulaicAssemblicatorGui(InventoryPlayer inventory, FormulaicAssemblicatorTileEntity tentity)
	{
		super(tentity, new FormulaicAssemblicatorContainer(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new GuiUpgradeTab(this, tileEntity, guiLocation));
		guiElements.add(new GuiRedstoneControl(this, tileEntity, guiLocation));
		guiElements.add(new GuiSideConfigurationTab(this, tileEntity, guiLocation));
		guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity, guiLocation));
		guiElements.add(new PowerBarGui(this, tileEntity, guiLocation, 159, 15));
		guiElements.add(new EnergyInfoGui(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.energyPerTick);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, guiLocation));
		guiElements.add(new GuiSlot(SlotType.POWER, this, guiLocation, 151, 75).with(SlotOverlay.POWER));
		ySize+=64;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
		if(xAxis >= 44 && xAxis <= 60 && yAxis >= 75 && yAxis <= 91)
		{
			drawCreativeTabHoveringText(LangUtils.localize("gui.fillEmpty"), xAxis, yAxis);
		}
		if(xAxis >= 7 && xAxis <= 21 && yAxis >= 45 && yAxis <= 59)
		{
			drawCreativeTabHoveringText(LangUtils.localize("gui.encodeFormula"), xAxis, yAxis);
		}
		if(xAxis >= 71 && xAxis <= 87 && yAxis >= 75 && yAxis <= 91)
		{
			drawCreativeTabHoveringText(LangUtils.localize("gui.craftSingle"), xAxis, yAxis);
		}
		if(xAxis >= 89 && xAxis <= 105 && yAxis >= 75 && yAxis <= 91)
		{
			drawCreativeTabHoveringText(LangUtils.localize("gui.craftAvailable"), xAxis, yAxis);
		}
		if(xAxis >= 107 && xAxis <= 123 && yAxis >= 75 && yAxis <= 91)
		{
			drawCreativeTabHoveringText(LangUtils.localize("gui.autoModeToggle") + " " + LangUtils.transOnOff(!tileEntity.autoMode), xAxis, yAxis);
		}
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
		int xAxis = mouseX - guiWidth;
		int yAxis = mouseY - guiHeight;
		if(!tileEntity.autoMode)
		{
			if(xAxis >= 44 && xAxis <= 60 && yAxis >= 75 && yAxis <= 91)
			{
				drawTexturedModalRect(guiWidth + 44, guiHeight + 75, 176 + 62, 0, 16, 16);
			}
			else {
				drawTexturedModalRect(guiWidth + 44, guiHeight + 75, 176 + 62, 16, 16, 16);
			}
		}
		else {
			drawTexturedModalRect(guiWidth + 44, guiHeight + 75, 176 + 62, 32, 16, 16);
		}
		if(!tileEntity.autoMode && tileEntity.isRecipe)
		{
			if(canEncode())
			{
				if(xAxis >= 7 && xAxis <= 21 && yAxis >= 45 && yAxis <= 59)
				{
					drawTexturedModalRect(guiWidth + 7, guiHeight + 45, 176, 0, 14, 14);
				}
				else {
					drawTexturedModalRect(guiWidth + 7, guiHeight + 45, 176, 14, 14, 14);
				}
			}
			else {
				drawTexturedModalRect(guiWidth + 7, guiHeight + 45, 176, 28, 14, 14);
			}
			if(xAxis >= 71 && xAxis <= 87 && yAxis >= 75 && yAxis <= 91)
			{
				drawTexturedModalRect(guiWidth + 71, guiHeight + 75, 176 + 14, 0, 16, 16);
			}
			else {
				drawTexturedModalRect(guiWidth + 71, guiHeight + 75, 176 + 14, 16, 16, 16);
			}
			if(xAxis >= 89 && xAxis <= 105 && yAxis >= 75 && yAxis <= 91)
			{
				drawTexturedModalRect(guiWidth + 89, guiHeight + 75, 176 + 30, 0, 16, 16);
			}
			else {
				drawTexturedModalRect(guiWidth + 89, guiHeight + 75, 176 + 30, 16, 16, 16);
			}
		}
		else {
			drawTexturedModalRect(guiWidth + 7, guiHeight + 45, 176, 28, 14, 14);
			drawTexturedModalRect(guiWidth + 71, guiHeight + 75, 176 + 14, 32, 16, 16);
			drawTexturedModalRect(guiWidth + 89, guiHeight + 75, 176 + 30, 32, 16, 16);
		}
		if(tileEntity.formula != null)
		{
			if(xAxis >= 107 && xAxis <= 123 && yAxis >= 75 && yAxis <= 91)
			{
				drawTexturedModalRect(guiWidth + 107, guiHeight + 75, 176 + 46, 0, 16, 16);
			}
			else {
				drawTexturedModalRect(guiWidth + 107, guiHeight + 75, 176 + 46, 16, 16, 16);
			}
		}
		else {
			drawTexturedModalRect(guiWidth + 107, guiHeight + 75, 176 + 46, 32, 16, 16);
		}
		if(tileEntity.operatingTicks > 0)
		{
			int display = (int)((double)tileEntity.operatingTicks*22/(double)tileEntity.ticksRequired);
			drawTexturedModalRect(guiWidth + 86, guiHeight + 43, 176, 48, display, 16);
		}
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiSlot.png"));
		drawTexturedModalRect(guiWidth + 90, guiHeight + 25, tileEntity.isRecipe ? 2 : 20, 39, 14, 12);
		if(tileEntity.formula != null)
		{
			for(int i = 0; i < 9; i++)
			{
				ItemStack stack = tileEntity.formula.input[i];
				if(stack != null)
				{
					Slot slot = (Slot)inventorySlots.inventorySlots.get(i+20);
					GL11.glPushMatrix();
					if(slot.getStack() == null || !slot.getStack().isItemEqual(stack))
					{
						drawGradientRect(guiWidth + slot.xDisplayPosition, guiHeight + slot.yDisplayPosition, guiWidth + slot.xDisplayPosition + 16, guiHeight + slot.yDisplayPosition + 16, -2137456640, -2137456640);
					}
					GL11.glEnable(GL11.GL_LIGHTING);
					MekanismRenderer.blendOn();
					GL11.glColor4f(1F, 1F, 1F, 0.4F);
					itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), stack, guiWidth + slot.xDisplayPosition, guiHeight + slot.yDisplayPosition);
					MekanismRenderer.blendOff();
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glPopMatrix();
				}
			}
		}

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	private boolean canEncode()
	{
		return tileEntity.formula == null && tileEntity.inventory[2] != null && tileEntity.inventory[2].getItem() instanceof ItemCraftingFormula &&
				((ItemCraftingFormula)tileEntity.inventory[2].getItem()).getInventory(tileEntity.inventory[2]) == null;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);

		if(button == 0)
		{
			int xAxis = (mouseX - (width - xSize) / 2);
			int yAxis = (mouseY - (height - ySize) / 2);

			if(!tileEntity.autoMode)
			{
				if(xAxis >= 44 && xAxis <= 60 && yAxis >= 75 && yAxis <= 91)
				{
					SoundHandler.playSound("gui.button.press");
					ArrayList data = new ArrayList();
					data.add(Integer.valueOf(4));

					Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
				}
				if(tileEntity.isRecipe)
				{
					if(canEncode())
					{
						if(xAxis >= 7 && xAxis <= 21 && yAxis >= 45 && yAxis <= 59)
						{
							SoundHandler.playSound("gui.button.press");
							ArrayList data = new ArrayList();
							data.add(Integer.valueOf(1));
							Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
						}
					}
					if(xAxis >= 71 && xAxis <= 87 && yAxis >= 75 && yAxis <= 91)
					{
						SoundHandler.playSound("gui.button.press");
						ArrayList data = new ArrayList();
						data.add(Integer.valueOf(2));
						Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
					}
					if(xAxis >= 89 && xAxis <= 105 && yAxis >= 75 && yAxis <= 91)
					{
						SoundHandler.playSound("gui.button.press");
						ArrayList data = new ArrayList();
						data.add(Integer.valueOf(3));
						Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
					}
				}
			}
			if(tileEntity.formula != null)
			{
				if(xAxis >= 107 && xAxis <= 123 && yAxis >= 75 && yAxis <= 91)
				{
					SoundHandler.playSound("gui.button.press");
					ArrayList data = new ArrayList();
					data.add(Integer.valueOf(0));
					Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
				}
			}
		}
	}
}
