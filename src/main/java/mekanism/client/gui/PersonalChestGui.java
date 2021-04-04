package mekanism.client.gui;

import mekanism.common.inventory.container.PersonalChestContainer;
import mekanism.common.tile.PersonalChestTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PersonalChestGui extends GuiMekanism
{
	public PersonalChestTileEntity tileEntity;

	public PersonalChestGui(InventoryPlayer inventory, PersonalChestTileEntity tentity)
	{
		super(tentity, new PersonalChestContainer(inventory, tentity, null, true));

		xSize+=26;
		ySize+=64;
		tileEntity = tentity;
	}

	public PersonalChestGui(InventoryPlayer inventory, IInventory inv)
	{
		super(new PersonalChestContainer(inventory, null, inv, false));

		xSize+=26;
		ySize+=64;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		fontRendererObj.drawString(LangUtils.localize("tile.MachineBlock.PersonalChest.name"), 8, 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "PersonalChestGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}
