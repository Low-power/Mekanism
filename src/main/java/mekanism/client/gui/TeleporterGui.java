package mekanism.client.gui;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.client.gui.element.PowerBarGui;
import mekanism.client.gui.element.PowerBarGui.IPowerInfoHandler;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiScrollList;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.frequency.Frequency;
import mekanism.common.frequency.FrequencyManager;
import mekanism.common.inventory.container.NullContainer;
import mekanism.common.inventory.container.TeleporterContainer;
import mekanism.common.item.PortableTeleporter;
import mekanism.common.network.PortableTeleporterPacket.PortableTeleporterMessage;
import mekanism.common.network.PortableTeleporterPacket.PortableTeleporterPacketType;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TeleporterTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TeleporterGui extends GuiMekanism
{
	public ResourceLocation resource;

	public TeleporterTileEntity tileEntity;
	public ItemStack itemStack;

	//public EntityPlayer player;

	public GuiButton publicButton;
	public GuiButton privateButton;

	public GuiButton setButton;
	public GuiButton deleteButton;

	public GuiButton teleportButton;

	public GuiScrollList scrollList;

	public GuiTextField frequencyField;

	public boolean privateMode;

	public Frequency clientFreq;
	public byte clientStatus;

	public List<Frequency> clientPublicCache = new ArrayList<Frequency>();
	public List<Frequency> clientPrivateCache = new ArrayList<Frequency>();

	public boolean isInit = true;

	public TeleporterGui(InventoryPlayer inventory, TeleporterTileEntity tentity)
	{
		super(tentity, new TeleporterContainer(inventory, tentity));
		tileEntity = tentity;
		resource = MekanismUtils.getResource(ResourceType.GUI, "TeleporterGui.png");

		guiElements.add(new GuiRedstoneControl(this, tileEntity, resource));
		guiElements.add(new PowerBarGui(this, new IPowerInfoHandler() {
			@Override
			public String getTooltip()
			{
				return MekanismUtils.getEnergyDisplay(getEnergy());
			}
			@Override
			public double getLevel()
			{
				return getEnergy()/getMaxEnergy();
			}
		}, resource, 158, 26));
		guiElements.add(new GuiSlot(SlotType.NORMAL, this, resource, 152, 6).with(SlotOverlay.POWER));
		guiElements.add(scrollList = new GuiScrollList(this, resource, 28, 37, 120, 4));
		if(tileEntity.frequency != null)
		{
			privateMode = !tileEntity.frequency.publicFreq;
		}
		ySize+=64;
	}

	public TeleporterGui(EntityPlayer player, ItemStack stack)
	{
		super(new NullContainer());
		itemStack = stack;
		//this.player = player;
		resource = MekanismUtils.getResource(ResourceType.GUI, "PortableTeleporterGui.png");
		guiElements.add(new PowerBarGui(this, new IPowerInfoHandler() {
			@Override
			public String getTooltip()
			{
				return MekanismUtils.getEnergyDisplay(getEnergy());
			}
			@Override
			public double getLevel()
			{
				return getEnergy()/getMaxEnergy();
			}
		}, resource, 158, 26));
		guiElements.add(scrollList = new GuiScrollList(this, resource, 28, 37, 120, 4));
		PortableTeleporter item = (PortableTeleporter)itemStack.getItem();
		if(item.getFrequency(stack) != null)
		{
			privateMode = item.isPrivateMode(itemStack);
			setFrequency(item.getFrequency(stack));
		}
		else {
			Mekanism.packetHandler.sendToServer(new PortableTeleporterMessage(PortableTeleporterPacketType.DATA_REQUEST, clientFreq));
		}
		ySize = 175;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		buttonList.clear();
		publicButton = new GuiButton(0, guiWidth + 27, guiHeight + 14, 60, 20, LangUtils.localize("gui.public"));
		privateButton = new GuiButton(1, guiWidth + 89, guiHeight + 14, 60, 20, LangUtils.localize("gui.private"));
		setButton = new GuiButton(2, guiWidth + 27, guiHeight + 116, 60, 20, LangUtils.localize("gui.set"));
		deleteButton = new GuiButton(3, guiWidth + 89, guiHeight + 116, 60, 20, LangUtils.localize("gui.delete"));
		if(itemStack != null)
		{
			teleportButton = new GuiButton(4, guiWidth + 42, guiHeight + 140, 92, 20, LangUtils.localize("gui.teleport"));
		}
		frequencyField = new GuiTextField(fontRendererObj, guiWidth + 50, guiHeight + 104, 86, 11);
		frequencyField.setMaxStringLength(FrequencyManager.MAX_FREQ_LENGTH);
		frequencyField.setEnableBackgroundDrawing(false);
		updateButtons();

		buttonList.add(publicButton);
		buttonList.add(privateButton);
		buttonList.add(setButton);
		buttonList.add(deleteButton);
		if(itemStack != null)
		{
			buttonList.add(teleportButton);
			if(!isInit)
			{
				Mekanism.packetHandler.sendToServer(new PortableTeleporterMessage(PortableTeleporterPacketType.DATA_REQUEST, clientFreq));
			}
			else {
				isInit = false;
			}
		}
	}

	public void setFrequency(String freq)
	{
		if(freq.isEmpty())
		{
			return;
		}
		if(tileEntity != null)
		{
			ArrayList data = new ArrayList();
			data.add(Integer.valueOf(0));
			data.add(freq);
			data.add(Boolean.valueOf(!privateMode));
			Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
		}
		else {
			Frequency newFreq = new Frequency(freq, null).setPublic(!privateMode);
			Mekanism.packetHandler.sendToServer(new PortableTeleporterMessage(PortableTeleporterPacketType.SET_FREQ, newFreq));
		}
	}

	public void updateButtons()
	{
/*
		if(getOwner() == null)
		{
			return;
		}
*/
		List<String> text = new ArrayList<String>();
		if(privateMode)
		{
			for(Frequency freq : getPrivateCache())
			{
				text.add(freq.name);
			}
		}
		else {
			for(Frequency freq : getPublicCache())
			{
				text.add(freq.name + " (" + freq.owner + ")");
			}
		}
		scrollList.setText(text);
		if(privateMode)
		{
			publicButton.enabled = true;
			privateButton.enabled = false;
		}
		else {
			publicButton.enabled = false;
			privateButton.enabled = true;
		}
		if(scrollList.hasSelection())
		{
			Frequency freq = privateMode ? getPrivateCache().get(scrollList.selected) : getPublicCache().get(scrollList.selected);
			if(getFrequency() == null || !getFrequency().equals(freq))
			{
				setButton.enabled = true;
			}
			else {
				setButton.enabled = false;
			}
/*
			if(getOwner().equals(freq.owner))
			{
				deleteButton.enabled = true;
			}
			else {
				deleteButton.enabled = false;
			}*/
		}
		else {
			setButton.enabled = false;
			deleteButton.enabled = false;
		}
		if(itemStack != null)
		{
			if(clientFreq != null && clientStatus == 1)
			{
				teleportButton.enabled = true;
			}
			else {
				teleportButton.enabled = false;
			}
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		updateButtons();
		frequencyField.updateCursorCounter();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);

		updateButtons();

		frequencyField.mouseClicked(mouseX, mouseY, button);
		if(button == 0)
		{
			int xAxis = (mouseX - (width - xSize) / 2);
			int yAxis = (mouseY - (height - ySize) / 2);
			if(xAxis >= 137 && xAxis <= 148 && yAxis >= 103 && yAxis <= 114)
			{
				setFrequency(frequencyField.getText());
				frequencyField.setText("");
				SoundHandler.playSound("gui.button.press");
			}
		}
	}

	@Override
	public void keyTyped(char c, int i)
	{
		if(!frequencyField.isFocused() || i == Keyboard.KEY_ESCAPE)
		{
			super.keyTyped(c, i);
		}
		if(i == Keyboard.KEY_RETURN)
		{
			if(frequencyField.isFocused())
			{
				setFrequency(frequencyField.getText());
				frequencyField.setText("");
			}
		}

		if(Character.isDigit(c) || Character.isLetter(c) || isTextboxKey(c, i) || FrequencyManager.SPECIAL_CHARS.contains(c))
		{
			frequencyField.textboxKeyTyped(c, i);
		}
		updateButtons();
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		super.actionPerformed(guibutton);

		if(guibutton.id == 0)
		{
			privateMode = false;
		}
		else if(guibutton.id == 1)
		{
			privateMode = true;
		}
		else if(guibutton.id == 2)
		{
			int selection = scrollList.getSelection();
			if(selection != -1)
			{
				Frequency freq = privateMode ? getPrivateCache().get(selection) : getPublicCache().get(selection);
				setFrequency(freq.name);
			}
		}
		else if(guibutton.id == 3)
		{
			int selection = scrollList.getSelection();
			if(selection != -1)
			{
				Frequency freq = privateMode ? getPrivateCache().get(selection) : getPublicCache().get(selection);
				if(tileEntity != null)
				{
					ArrayList data = new ArrayList();
					data.add(Integer.valueOf(1));
					data.add(freq.name);
					data.add(Boolean.valueOf(freq.publicFreq));
					Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
				}
				else {
					Mekanism.packetHandler.sendToServer(new PortableTeleporterMessage(PortableTeleporterPacketType.DEL_FREQ, freq));
					Mekanism.packetHandler.sendToServer(new PortableTeleporterMessage(PortableTeleporterPacketType.DATA_REQUEST, null));
				}
				scrollList.selected = -1;
			}
		}
		else if(guibutton.id == 4)
		{
			if(clientFreq != null && clientStatus == 1)
			{
				mc.setIngameFocus();
				Mekanism.packetHandler.sendToServer(new PortableTeleporterMessage(PortableTeleporterPacketType.TELEPORT, clientFreq));
			}
		}
		updateButtons();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX-(width-xSize)/2);
		int yAxis = (mouseY-(height-ySize)/2);

		fontRendererObj.drawString(getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(getInventoryName())/2), 4, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.freq") + ":", 32, 81, 0x404040);
		fontRendererObj.drawString(" " + (getFrequency() != null ? getFrequency().name : EnumColor.DARK_RED + LangUtils.localize("gui.none")), 32 + fontRendererObj.getStringWidth(LangUtils.localize("gui.freq") + ":"), 81, 0x797979);
		String str = LangUtils.localize("gui.set") + ":";
		renderScaledText(str, 27, 104, 0x404040, 20);
		if(xAxis >= 6 && xAxis <= 24 && yAxis >= 6 && yAxis <= 24)
		{
			if(getFrequency() == null)
			{
				drawCreativeTabHoveringText(EnumColor.DARK_RED + LangUtils.localize("gui.teleporter.noFreq"), xAxis, yAxis);
			}
			else {
				drawCreativeTabHoveringText(getStatusDisplay(), xAxis, yAxis);
			}
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(resource);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width-xSize)/2;
		int guiHeight = (height-ySize)/2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		if(xAxis >= 137 && xAxis <= 148 && yAxis >= 103 && yAxis <= 114)
		{
			drawTexturedModalRect(guiWidth + 137, guiHeight + 103, xSize, 0, 11, 11);
		}
		else {
			drawTexturedModalRect(guiWidth + 137, guiHeight + 103, xSize, 11, 11, 11);
		}
		int y;
		if(getFrequency() == null) y = 94;
		else switch(getStatus()) {
			case 2:
				y = 22;
				break;
			case 3:
				y = 40;
				break;
			case 4:
				y = 58;
				break;
			default:
				y = 76;
				break;
		}
		drawTexturedModalRect(guiWidth + 6, guiHeight + 6, 176, y, 18, 18);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
		frequencyField.drawTextBox();
	}

	public String getStatusDisplay()
	{
		switch(getStatus())
		{
			case 1:
				return EnumColor.DARK_GREEN + LangUtils.localize("gui.teleporter.ready");
			case 2:
				return EnumColor.DARK_RED + LangUtils.localize("gui.teleporter.noFrame");
			case 3:
				return EnumColor.DARK_RED + LangUtils.localize("gui.teleporter.noLink");
			case 4:
				return EnumColor.DARK_RED + LangUtils.localize("gui.teleporter.needsEnergy");
		}
		return EnumColor.DARK_RED + LangUtils.localize("gui.teleporter.noLink");
	}

	private byte getStatus()
	{
		return tileEntity != null ? tileEntity.status : clientStatus;
	}

	private List<Frequency> getPublicCache()
	{
		return tileEntity != null ? tileEntity.publicCache : clientPublicCache;
	}

	private List<Frequency> getPrivateCache()
	{
		return tileEntity != null ? tileEntity.privateCache : clientPrivateCache;
	}

	private Frequency getFrequency()
	{
		return tileEntity != null ? tileEntity.frequency : clientFreq;
	}

	private String getInventoryName()
	{
		return tileEntity != null ? tileEntity.getInventoryName() : itemStack.getDisplayName();
	}

	private double getEnergy()
	{
		if(itemStack != null)
		{
			return ((PortableTeleporter)itemStack.getItem()).getEnergy(itemStack);
		}
		return tileEntity.getEnergy();
	}

	private double getMaxEnergy()
	{
		if(itemStack != null)
		{
			return ((PortableTeleporter)itemStack.getItem()).getMaxEnergy(itemStack);
		}
		return tileEntity.getMaxEnergy();
	}
}
