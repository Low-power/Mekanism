package mekanism.client.nei;

import mekanism.client.gui.ChemicalCrystallizerGui;
import mekanism.client.gui.ChemicalDissolutionChamberGui;
import mekanism.client.gui.ChemicalInfuserGui;
import mekanism.client.gui.ChemicalInjectionChamberGui;
import mekanism.client.gui.ChemicalOxidizerGui;
import mekanism.client.gui.ChemicalWasherGui;
import mekanism.client.gui.CombinerGui;
import mekanism.client.gui.CrusherGui;
import mekanism.client.gui.ElectrolyticSeparatorGui;
import mekanism.client.gui.EnrichmentChamberGui;
import mekanism.client.gui.MetallurgicInfuserGui;
import mekanism.client.gui.OsmiumCompressorGui;
import mekanism.client.gui.PRCGui;
import mekanism.client.gui.PrecisionSawmillGui;
import mekanism.client.gui.PurificationChamberGui;
import mekanism.client.gui.RotaryCondensentratorGui;
import mekanism.client.gui.SolarNeutronActivatorGui;
import mekanism.client.gui.ThermalEvaporationControllerGui;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;
import net.minecraft.item.ItemStack;

public class NEIMekanismConfig implements IConfigureNEI
{
	@Override
	public void loadConfig()
	{
		API.registerRecipeHandler(new EnrichmentChamberRecipeHandler());
		API.registerUsageHandler(new EnrichmentChamberRecipeHandler());

		API.registerRecipeHandler(new OsmiumCompressorRecipeHandler());
		API.registerUsageHandler(new OsmiumCompressorRecipeHandler());

		API.registerRecipeHandler(new CrusherRecipeHandler());
		API.registerUsageHandler(new CrusherRecipeHandler());

		API.registerRecipeHandler(new CombinerRecipeHandler());
		API.registerUsageHandler(new CombinerRecipeHandler());

		API.registerRecipeHandler(new MetallurgicInfuserRecipeHandler());
		API.registerUsageHandler(new MetallurgicInfuserRecipeHandler());

		API.registerRecipeHandler(new PurificationChamberRecipeHandler());
		API.registerUsageHandler(new PurificationChamberRecipeHandler());

		API.registerRecipeHandler(new ChemicalInjectionChamberRecipeHandler());
		API.registerUsageHandler(new ChemicalInjectionChamberRecipeHandler());

		API.registerRecipeHandler(new ChemicalOxidizerRecipeHandler());
		API.registerUsageHandler(new ChemicalOxidizerRecipeHandler());

		API.registerRecipeHandler(new ChemicalInfuserRecipeHandler());
		API.registerUsageHandler(new ChemicalInfuserRecipeHandler());

		API.registerRecipeHandler(new RotaryCondensentratorRecipeHandler());
		API.registerUsageHandler(new RotaryCondensentratorRecipeHandler());

		API.registerRecipeHandler(new ElectrolyticSeparatorRecipeHandler());
		API.registerUsageHandler(new ElectrolyticSeparatorRecipeHandler());

		API.registerRecipeHandler(new PrecisionSawmillRecipeHandler());
		API.registerUsageHandler(new PrecisionSawmillRecipeHandler());

		API.registerRecipeHandler(new ThermalEvaporationRecipeHandler());
		API.registerUsageHandler(new ThermalEvaporationRecipeHandler());

		API.registerRecipeHandler(new ChemicalDissolutionChamberRecipeHandler());
		API.registerUsageHandler(new ChemicalDissolutionChamberRecipeHandler());

		API.registerRecipeHandler(new ChemicalWasherRecipeHandler());
		API.registerUsageHandler(new ChemicalWasherRecipeHandler());

		API.registerRecipeHandler(new ChemicalCrystallizerRecipeHandler());
		API.registerUsageHandler(new ChemicalCrystallizerRecipeHandler());

		API.registerRecipeHandler(new PRCRecipeHandler());
		API.registerUsageHandler(new PRCRecipeHandler());

		API.registerRecipeHandler(new SolarNeutronRecipeHandler());
		API.registerUsageHandler(new SolarNeutronRecipeHandler());

		API.registerRecipeHandler(new ShapedMekanismRecipeHandler());
		API.registerUsageHandler(new ShapedMekanismRecipeHandler());

		API.registerRecipeHandler(new ShapelessMekanismRecipeHandler());
		API.registerUsageHandler(new ShapelessMekanismRecipeHandler());

		API.setGuiOffset(EnrichmentChamberGui.class, 16, 6);
		API.setGuiOffset(OsmiumCompressorGui.class, 16, 6);
		API.setGuiOffset(CrusherGui.class, 16, 6);
		API.setGuiOffset(CombinerGui.class, 16, 6);
		API.setGuiOffset(PurificationChamberGui.class, 16, 6);
		API.setGuiOffset(ChemicalInjectionChamberGui.class, 16, 6);
		API.setGuiOffset(MetallurgicInfuserGui.class, 5, 15);
		API.setGuiOffset(ChemicalOxidizerGui.class, ChemicalOxidizerRecipeHandler.xOffset, ChemicalOxidizerRecipeHandler.yOffset);
		API.setGuiOffset(ChemicalInfuserGui.class, ChemicalInfuserRecipeHandler.xOffset, ChemicalInfuserRecipeHandler.yOffset);
		API.setGuiOffset(RotaryCondensentratorGui.class, RotaryCondensentratorRecipeHandler.xOffset, RotaryCondensentratorRecipeHandler.yOffset);
		API.setGuiOffset(ElectrolyticSeparatorGui.class, ElectrolyticSeparatorRecipeHandler.xOffset, ElectrolyticSeparatorRecipeHandler.yOffset);
		API.setGuiOffset(PrecisionSawmillGui.class, 16, 6);
		API.setGuiOffset(ThermalEvaporationControllerGui.class, ThermalEvaporationRecipeHandler.xOffset, ThermalEvaporationRecipeHandler.yOffset);
		API.setGuiOffset(ChemicalDissolutionChamberGui.class, ChemicalDissolutionChamberRecipeHandler.xOffset, ChemicalDissolutionChamberRecipeHandler.yOffset);
		API.setGuiOffset(ChemicalWasherGui.class, ChemicalWasherRecipeHandler.xOffset, ChemicalWasherRecipeHandler.yOffset);
		API.setGuiOffset(ChemicalCrystallizerGui.class, ChemicalCrystallizerRecipeHandler.xOffset, ChemicalCrystallizerRecipeHandler.yOffset);
		API.setGuiOffset(PRCGui.class, PRCRecipeHandler.xOffset, PRCRecipeHandler.yOffset);
		API.setGuiOffset(ThermalEvaporationControllerGui.class, ThermalEvaporationRecipeHandler.xOffset, ThermalEvaporationRecipeHandler.yOffset);
		API.setGuiOffset(SolarNeutronActivatorGui.class, SolarNeutronRecipeHandler.xOffset, SolarNeutronRecipeHandler.yOffset);

		GuiContainerManager.addSlotClickHandler(new MekanismSlotClickHandler());

		API.registerNEIGuiHandler(new ElementBoundHandler());

		API.hideItem(new ItemStack(MekanismBlocks.BoundingBlock));
		API.hideItem(new ItemStack(MekanismItems.ItemProxy));
	}

	@Override
	public String getName()
	{
		return "Mekanism NEI Plugin";
	}

	@Override
	public String getVersion()
	{
		return "8.0.0";
	}
}
