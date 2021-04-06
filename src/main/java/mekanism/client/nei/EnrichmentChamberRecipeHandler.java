package mekanism.client.nei;

import mekanism.client.gui.EnrichmentChamberGui;
import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.EnrichmentRecipe;
import mekanism.common.util.LangUtils;
import java.util.Collection;

public class EnrichmentChamberRecipeHandler extends MachineRecipeHandler
{
	@Override
	public String getRecipeName()
	{
		return LangUtils.localize("tile.MachineBlock.EnrichmentChamber.name");
	}

	@Override
	public String getRecipeId()
	{
		return "mekanism.chamber";
	}

	@Override
	public String getOverlayIdentifier()
	{
		return "chamber";
	}

	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.BLUE;
	}

	@Override
	public Collection<EnrichmentRecipe> getRecipes()
	{
		return Recipe.ENRICHMENT_CHAMBER.get().values();
	}

	@Override
	public Class getGuiClass()
	{
		return EnrichmentChamberGui.class;
	}
}
