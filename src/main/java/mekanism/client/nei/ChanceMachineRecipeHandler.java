package mekanism.client.nei;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawString;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.PowerBarGui;
import mekanism.client.gui.element.PowerBarGui.IPowerInfoHandler;
import mekanism.client.gui.element.ProgressGui;
import mekanism.client.gui.element.ProgressGui.IProgressInfoHandler;
import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.recipe.machines.ChanceMachineRecipe;
import mekanism.common.recipe.outputs.ChanceOutput;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import java.awt.Rectangle;
import java.util.Collection;

public abstract class ChanceMachineRecipeHandler extends BaseRecipeHandler
{
	private int ticksPassed;

	public abstract String getRecipeId();

	public abstract Collection<? extends ChanceMachineRecipe> getRecipes();

	public abstract ProgressBar getProgressType();

	@Override
	public void addGuiElements()
	{
		guiElements.add(new GuiSlot(SlotType.INPUT, this, MekanismUtils.getResource(ResourceType.GUI, stripTexture()), 55, 16));
		guiElements.add(new GuiSlot(SlotType.POWER, this, MekanismUtils.getResource(ResourceType.GUI, stripTexture()), 55, 52).with(SlotOverlay.POWER));
		guiElements.add(new GuiSlot(SlotType.OUTPUT_WIDE, this, MekanismUtils.getResource(ResourceType.GUI, stripTexture()), 111, 30));
		guiElements.add(new PowerBarGui(this, new IPowerInfoHandler() {
			@Override
			public double getLevel()
			{
				return ticksPassed <= 20 ? ticksPassed / 20F : 1F;
			}
		}, MekanismUtils.getResource(ResourceType.GUI, stripTexture()), 164, 15));
		guiElements.add(new ProgressGui(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return ticksPassed >= 20 ? (ticksPassed - 20) % 20 / 20F : 0F;
			}
		}, getProgressType(), this, MekanismUtils.getResource(ResourceType.GUI, stripTexture()), 77, 37));
	}

	@Override
	public void drawBackground(int i)
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		changeTexture(getGuiTexture());
		drawTexturedModalRect(12, 0, 28, 5, 144, 68);
		for(GuiElement e : guiElements)
		{
			e.renderBackground(0, 0, -16, -5);
		}
	}

	@Override
	public void drawExtras(int i)
	{
		CachedIORecipe recipe = (CachedIORecipe)arecipes.get(i);

		if(recipe.output.hasSecondary())
		{
			drawString(Math.round(recipe.output.secondaryChance*100) + "%", 116, 52, 0x404040, false);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		ticksPassed++;
	}

	@Override
	public void loadTransferRects()
	{
		transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(63, 34, 24, 7), getRecipeId(), new Object[0]));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if(outputId.equals(getRecipeId()))
		{
			for(ChanceMachineRecipe irecipe : getRecipes())
			{
				arecipes.add(new CachedIORecipe(irecipe));
			}
		}
		else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		for(ChanceMachineRecipe<?> irecipe : getRecipes())
		{
			if(irecipe.getOutput().hasPrimary() && NEIServerUtils.areStacksSameTypeCrafting(irecipe.getOutput().primaryOutput, result))
			{
				arecipes.add(new CachedIORecipe(irecipe));
			}
			else if(irecipe.getOutput().hasSecondary() && NEIServerUtils.areStacksSameTypeCrafting(irecipe.getOutput().secondaryOutput, result))
			{
				arecipes.add(new CachedIORecipe(irecipe));
			}
		}
	}

	@Override
	public String getGuiTexture()
	{
		return "mekanism:gui/GuiBasicMachine.png";
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient)
	{
		for(ChanceMachineRecipe<?> irecipe : getRecipes())
		{
			if(NEIServerUtils.areStacksSameTypeCrafting((ItemStack)irecipe.getInput().ingredient, ingredient))
			{
				arecipes.add(new CachedIORecipe(irecipe));
			}
		}
	}

	public class CachedIORecipe extends TemplateRecipeHandler.CachedRecipe
	{
		public PositionedStack input;
		public ChanceOutput output;

		@Override
		public PositionedStack getIngredient()
		{
			return input;
		}

		@Override
		public PositionedStack getResult()
		{
			if(output.hasPrimary())
			{
				return new PositionedStack(output.primaryOutput, 100, 30);
			}

			return null;
		}

		@Override
		public PositionedStack getOtherStack()
		{
			if(output.hasSecondary())
			{
				return new PositionedStack(output.secondaryOutput, 116, 30);
			}

			return null;
		}

		public CachedIORecipe(ItemStack itemstack, ChanceOutput chance)
		{
			input = new PositionedStack(itemstack, 40, 12);
			output = chance;
		}

		public CachedIORecipe(ChanceMachineRecipe<?> recipe)
		{
			this(recipe.getInput().ingredient, recipe.getOutput());
		}
	}
}
