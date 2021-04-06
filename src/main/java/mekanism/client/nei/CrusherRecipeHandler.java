package mekanism.client.nei;

import mekanism.client.gui.CrusherGui;
import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.CrusherRecipe;
import mekanism.common.util.LangUtils;
import java.util.Collection;

public class CrusherRecipeHandler extends MachineRecipeHandler
{
	@Override
	public String getRecipeName()
	{
		return LangUtils.localize("tile.MachineBlock.Crusher.name");
	}

	@Override
	public String getRecipeId()
	{
		return "mekanism.crusher";
	}

	@Override
	public String getOverlayIdentifier()
	{
		return "crusher";
	}

	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.CRUSH;
	}

	@Override
	public Collection<CrusherRecipe> getRecipes()
	{
		return Recipe.CRUSHER.get().values();
	}

	@Override
	public Class getGuiClass()
	{
		return CrusherGui.class;
	}
}
