package mekanism.client.gui;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.OreGas;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.EnergyInfoGui;
import mekanism.client.gui.element.GuiGasGauge;
import mekanism.client.gui.element.GuiGasGauge.IGasInfoHandler;
import mekanism.client.gui.element.GaugeGui;
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
import mekanism.common.inventory.container.ChemicalCrystallizerContainer;
import mekanism.common.recipe.machines.CrystallizerRecipe;
import mekanism.common.tile.ChemicalCrystallizerTileEntity;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ChemicalCrystallizerGui extends GuiMekanism
{
	public ChemicalCrystallizerTileEntity tileEntity;

	public Gas prevGas;

	public ItemStack renderStack;

	public int stackSwitch = 0;

	public int stackIndex = 0;

	public List<ItemStack> iterStacks = new ArrayList<ItemStack>();

	public ChemicalCrystallizerGui(InventoryPlayer inventory, ChemicalCrystallizerTileEntity tentity)
	{
		super(tentity, new ChemicalCrystallizerContainer(inventory, tentity));
		tileEntity = tentity;

		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png")));
		guiElements.add(new GuiUpgradeTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png")));
		guiElements.add(new PowerBarGui(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png"), 160, 23));
		guiElements.add(new GuiSideConfigurationTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png")));
		guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png")));
		guiElements.add(new EnergyInfoGui(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.energyUsage);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png")));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.inputTank;
			}
		}, GaugeGui.Type.STANDARD, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png"), 5, 4));
		guiElements.add(new GuiSlot(SlotType.EXTRA, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png"), 5, 64).with(SlotOverlay.PLUS));
		guiElements.add(new GuiSlot(SlotType.POWER, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png"), 154, 4).with(SlotOverlay.POWER));
		guiElements.add(new GuiSlot(SlotType.OUTPUT, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png"), 130, 56));

		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getScaledProgress();
			}
		}, ProgressBar.LARGE_RIGHT, this, MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png"), 51, 60));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(tileEntity.getInventoryName(), 37, 4, 0x404040);

		GasStack gas_stack = tileEntity.inputTank.getGas();
		if(gas_stack != null)
		{
			Gas gas = gas_stack.getGas();
			fontRendererObj.drawString(gas.getLocalizedName(), 29, 15, 0x00CD00);
			if(gas instanceof OreGas)
			{
				fontRendererObj.drawString("(" + ((OreGas)gas).getOreName() + ")", 29, 24, 0x00CD00);
			}
			else {
				CrystallizerRecipe recipe = tileEntity.getRecipe();
				if(recipe == null)
				{
					fontRendererObj.drawString("(" + LangUtils.localize("gui.noRecipe") + ")", 29, 24, 0x00CD00);
				}
				else {
					fontRendererObj.drawString("(" + recipe.recipeOutput.output.getDisplayName() + ")", 29, 24, 0x00CD00);
				}
			}
		}

		if(renderStack != null)
		{
			try {
				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_LIGHTING);
				itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), renderStack, 131, 14);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glPopMatrix();
			} catch(Exception e) {}
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "ChemicalCrystallizerGui.png"));
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	private Gas getInputGas()
	{
		GasStack gas_stack = tileEntity.inputTank.getGas();
		return gas_stack != null ? gas_stack.getGas() : null;
	}

	private void resetStacks()
	{
		iterStacks.clear();
		renderStack = null;
		stackSwitch = 0;
		stackIndex = -1;
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if(prevGas != getInputGas())
		{
			prevGas = getInputGas();

			boolean reset = false;

			if(prevGas == null || !(prevGas instanceof OreGas) || !((OreGas)prevGas).isClean())
			{
				reset = true;
				resetStacks();
			}

			if(!reset)
			{
				OreGas gas = (OreGas)prevGas;
				String oreDictName = "ore" + gas.getName().substring(5);

				updateStackList(oreDictName);
			}
		}

		if(stackSwitch > 0)
		{
			stackSwitch--;
		}

		if(stackSwitch == 0 && iterStacks != null && iterStacks.size() > 0)
		{
			stackSwitch = 20;

			if(stackIndex == -1 || stackIndex == iterStacks.size()-1)
			{
				stackIndex = 0;
			}
			else if(stackIndex < iterStacks.size()-1)
			{
				stackIndex++;
			}

			renderStack = iterStacks.get(stackIndex);
		}
		else if(iterStacks != null && iterStacks.size() == 0)
		{
			renderStack = null;
		}
	}

	private void updateStackList(String oreName)
	{
		if(iterStacks == null)
		{
			iterStacks = new ArrayList<ItemStack>();
		}
		else {
			iterStacks.clear();
		}

		List<String> keys = new ArrayList<String>();

		for(String s : OreDictionary.getOreNames())
		{
			if(oreName.equals(s) || oreName.equals("*"))
			{
				keys.add(s);
			}
			else if(oreName.endsWith("*") && !oreName.startsWith("*"))
			{
				if(s.startsWith(oreName.substring(0, oreName.length()-1)))
				{
					keys.add(s);
				}
			}
			else if(oreName.startsWith("*") && !oreName.endsWith("*"))
			{
				if(s.endsWith(oreName.substring(1)))
				{
					keys.add(s);
				}
			}
			else if(oreName.startsWith("*") && oreName.endsWith("*"))
			{
				if(s.contains(oreName.substring(1, oreName.length()-1)))
				{
					keys.add(s);
				}
			}
		}

		for(String key : keys)
		{
			for(ItemStack stack : OreDictionary.getOres(key))
			{
				ItemStack toAdd = stack.copy();

				if(!iterStacks.contains(stack) && toAdd.getItem() instanceof ItemBlock)
				{
					iterStacks.add(stack.copy());
				}
			}
		}

		stackSwitch = 0;
		stackIndex = -1;
	}
}
