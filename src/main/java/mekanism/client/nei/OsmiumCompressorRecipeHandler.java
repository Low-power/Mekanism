package mekanism.client.nei;

import mekanism.api.gas.Gas;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.OsmiumCompressorGui;
import mekanism.client.gui.element.ProgressGui.ProgressBar;
import mekanism.common.MekanismItems;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.OsmiumCompressorRecipe;
import mekanism.common.util.LangUtils;
import net.minecraft.item.ItemStack;
import java.util.Collection;
import java.util.List;

public class OsmiumCompressorRecipeHandler extends AdvancedMachineRecipeHandler
{
	@Override
	public String getRecipeName()
	{
		return LangUtils.localize("tile.MachineBlock.OsmiumCompressor.name");
	}

	@Override
	public String getRecipeId()
	{
		return "mekanism.compressor";
	}

	@Override
	public String getOverlayIdentifier()
	{
		return "compressor";
	}

	@Override
	public Collection<OsmiumCompressorRecipe> getRecipes()
	{
		return Recipe.OSMIUM_COMPRESSOR.get().values();
	}

	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.RED;
	}

	@Override
	public List<ItemStack> getFuelStacks(Gas gasType)
	{
		return ListUtils.asList(new ItemStack(MekanismItems.Ingot, 1, 1));
	}

	@Override
	public Class getGuiClass()
	{
		return OsmiumCompressorGui.class;
	}
}
