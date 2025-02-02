package mekanism.generators.client.gui;

import mekanism.api.Coord4D;
import mekanism.api.gas.GasTank;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiGasGauge;
import mekanism.client.gui.element.GuiGasGauge.IGasInfoHandler;
import mekanism.client.gui.element.GaugeGui.Type;
import mekanism.client.gui.element.ProgressGui;
import mekanism.client.gui.element.ProgressGui.IProgressInfoHandler;
import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.NullContainer;
import mekanism.common.network.SimpleGuiPacket.SimpleGuiMessage;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.gui.element.HeatTab;
import mekanism.generators.client.gui.element.GuiStatTab;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ReactorFuelGui extends GuiMekanism
{
	public TileEntityReactorController tileEntity;

	public GuiTextField injectionRateField;

	public ReactorFuelGui(InventoryPlayer inventory, final TileEntityReactorController tentity)
	{
		super(new NullContainer(inventory.player, tentity));
		tileEntity = tentity;
		guiElements.add(new EnergyInfoGui(new IInfoHandler()
		{
			@Override
			public List<String> getInfo()
			{
				return tileEntity.isFormed() ? ListUtils.asList(
						LangUtils.localize("gui.storing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()),
						LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getReactor().getPassiveGeneration(false, true)) + "/t") : new ArrayList();
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "TallGui.png")));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler()
		{
			@Override
			public GasTank getTank()
			{
				return tentity.deuteriumTank;
			}
		}, Type.SMALL, this, MekanismUtils.getResource(ResourceType.GUI, "TallGui.png"), 25, 64));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler()
		{
			@Override
			public GasTank getTank()
			{
				return tentity.fuelTank;
			}
		}, Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "TallGui.png"), 79, 50));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler()
		{
			@Override
			public GasTank getTank()
			{
				return tentity.tritiumTank;
			}
		}, Type.SMALL, this, MekanismUtils.getResource(ResourceType.GUI, "TallGui.png"), 133, 64));
		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getActive() ? 1 : 0;
			}
		}, ProgressBar.SMALL_RIGHT, this, MekanismUtils.getResource(ResourceType.GUI, "TallGui.png"), 45, 75));
		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getActive() ? 1 : 0;
			}
		}, ProgressBar.SMALL_LEFT, this, MekanismUtils.getResource(ResourceType.GUI, "TallGui.png"), 99, 75));
		guiElements.add(new HeatTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "TallGui.png")));
		guiElements.add(new GuiStatTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "TallGui.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRendererObj.drawString(tileEntity.getInventoryName(), 46, 6, 0x404040);
		String str = LangUtils.localize("gui.reactor.injectionRate") + ": " + (tileEntity.getReactor() == null ? "None" : tileEntity.getReactor().getInjectionRate());
		fontRendererObj.drawString(str, (xSize / 2) - (fontRendererObj.getStringWidth(str) / 2), 35, 0x404040);
		fontRendererObj.drawString("Edit Rate" + ":", 50, 117, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "TallGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		if(xAxis >= 6 && xAxis <= 20 && yAxis >= 6 && yAxis <= 20)
		{
			drawTexturedModalRect(guiWidth + 6, guiHeight + 6, 176, 0, 14, 14);
		}
		else {
			drawTexturedModalRect(guiWidth + 6, guiHeight + 6, 176, 14, 14, 14);
		}

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

		injectionRateField.drawTextBox();
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		injectionRateField.updateCursorCounter();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);

		injectionRateField.mouseClicked(mouseX, mouseY, button);

		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		if(button == 0)
		{
			if(xAxis >= 6 && xAxis <= 20 && yAxis >= 6 && yAxis <= 20)
			{
				SoundHandler.playSound("gui.button.press");
				Mekanism.packetHandler.sendToServer(new SimpleGuiMessage(Coord4D.get(tileEntity), 1, 10));
			}
		}
	}

	@Override
	public void keyTyped(char c, int i)
	{
		if(!injectionRateField.isFocused() || i == Keyboard.KEY_ESCAPE)
		{
			super.keyTyped(c, i);
		}

		if(i == Keyboard.KEY_RETURN)
		{
			if(injectionRateField.isFocused())
			{
				setInjection();
			}
		}

		if(Character.isDigit(c) || isTextboxKey(c, i))
		{
			injectionRateField.textboxKeyTyped(c, i);
		}
	}

	private void setInjection()
	{
		if(!injectionRateField.getText().isEmpty())
		{
			int toUse = Math.max(0, Integer.parseInt(injectionRateField.getText()));
			toUse -= toUse%2;
			ArrayList<Integer> data = new ArrayList<Integer>(2);
			data.add(Integer.valueOf(0));
			data.add(Integer.valueOf(toUse));

			Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));

			injectionRateField.setText("");
		}
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		String prevRad = injectionRateField != null ? injectionRateField.getText() : "";

		injectionRateField = new GuiTextField(fontRendererObj, guiWidth + 98, guiHeight + 115, 26, 11);
		injectionRateField.setMaxStringLength(2);
		injectionRateField.setText(prevRad);
	}
}
