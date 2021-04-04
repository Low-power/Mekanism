package mekanism.generators.common;

import mekanism.api.MekanismConfig.generators;
import mekanism.common.Mekanism;
import mekanism.common.base.IGuiProvider;
import mekanism.common.inventory.container.FilterContainer;
import mekanism.common.inventory.container.NullContainer;
import mekanism.common.tile.ContainerTileEntity;
import mekanism.generators.common.inventory.container.BioGeneratorContainer;
import mekanism.generators.common.inventory.container.GasGeneratorContainer;
import mekanism.generators.common.inventory.container.HeatGeneratorContainer;
import mekanism.generators.common.inventory.container.ContainerNeutronCapture;
import mekanism.generators.common.inventory.container.ContainerReactorController;
import mekanism.generators.common.inventory.container.SolarGeneratorContainer;
import mekanism.generators.common.inventory.container.WindGeneratorContainer;
import mekanism.generators.common.tile.TileEntityAdvancedSolarGenerator;
import mekanism.generators.common.tile.BioGeneratorTileEntity;
import mekanism.generators.common.tile.GasGeneratorTileEntity;
import mekanism.generators.common.tile.HeatGeneratorTileEntity;
import mekanism.generators.common.tile.SolarGeneratorTileEntity;
import mekanism.generators.common.tile.WindGeneratorTileEntity;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import mekanism.generators.common.tile.reactor.TileEntityReactorFrame;
import mekanism.generators.common.tile.reactor.TileEntityReactorGlass;
import mekanism.generators.common.tile.reactor.TileEntityReactorLaserFocusMatrix;
import mekanism.generators.common.tile.reactor.TileEntityReactorLogicAdapter;
import mekanism.generators.common.tile.reactor.TileEntityReactorNeutronCapture;
import mekanism.generators.common.tile.reactor.TileEntityReactorPort;
import mekanism.generators.common.tile.turbine.TileEntityElectromagneticCoil;
import mekanism.generators.common.tile.turbine.TileEntityRotationalComplex;
import mekanism.generators.common.tile.turbine.TileEntitySaturatingCondenser;
import mekanism.generators.common.tile.turbine.TurbineCasingTileEntity;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import mekanism.generators.common.tile.turbine.TurbineValveTileEntity;
import mekanism.generators.common.tile.turbine.TurbineVentTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Common proxy for the Mekanism Generators module.
 * @author AidanBrady
 *
 */
public class GeneratorsCommonProxy implements IGuiProvider
{
	public static int GENERATOR_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

	/**
	 * Register normal tile entities
	 */
	public void registerRegularTileEntities()
	{
		GameRegistry.registerTileEntity(TileEntityReactorFrame.class, "ReactorFrame");
		GameRegistry.registerTileEntity(TileEntityReactorGlass.class, "ReactorGlass");
		GameRegistry.registerTileEntity(TileEntityReactorLaserFocusMatrix.class, "ReactorLaserFocus");
		GameRegistry.registerTileEntity(TileEntityReactorNeutronCapture.class, "ReactorNeutronCapture");
		GameRegistry.registerTileEntity(TileEntityReactorPort.class, "ReactorPort");
		GameRegistry.registerTileEntity(TileEntityReactorLogicAdapter.class, "ReactorLogicAdapter");
		GameRegistry.registerTileEntity(TileEntityRotationalComplex.class, "RotationalComplex");
		GameRegistry.registerTileEntity(TileEntityElectromagneticCoil.class, "ElectromagneticCoil");
		GameRegistry.registerTileEntity(TileEntitySaturatingCondenser.class, "SaturatingCondenser");
	}

	/**
	 * Register tile entities that have special models. Overwritten in client to register TESRs.
	 */
	public void registerSpecialTileEntities()
	{
		GameRegistry.registerTileEntity(TileEntityAdvancedSolarGenerator.class, "AdvancedSolarGenerator");
		GameRegistry.registerTileEntity(SolarGeneratorTileEntity.class, "SolarGenerator");
		GameRegistry.registerTileEntity(BioGeneratorTileEntity.class, "BioGenerator");
		GameRegistry.registerTileEntity(HeatGeneratorTileEntity.class, "HeatGenerator");
		GameRegistry.registerTileEntity(GasGeneratorTileEntity.class, "GasGenerator");
		GameRegistry.registerTileEntity(WindGeneratorTileEntity.class, "WindTurbine");
		GameRegistry.registerTileEntity(TileEntityReactorController.class, "ReactorController");
		GameRegistry.registerTileEntity(TileEntityTurbineRotor.class, "TurbineRod");
		GameRegistry.registerTileEntity(TurbineCasingTileEntity.class, "TurbineCasing");
		GameRegistry.registerTileEntity(TurbineValveTileEntity.class, "TurbineValve");
		GameRegistry.registerTileEntity(TurbineVentTileEntity.class, "TurbineVent");
	}

	/**
	 * Register and load client-only render information.
	 */
	public void registerRenderInformation() {}

	/**
	 * Set and load the mod's common configuration properties.
	 */
	public void loadConfiguration()
	{
		generators.advancedSolarGeneration = Mekanism.configuration.get("generation", "AdvancedSolarGeneration", 300D).getDouble();
		generators.bioGeneration = Mekanism.configuration.get("generation", "BioGeneration", 350D).getDouble();
		generators.heatGeneration = Mekanism.configuration.get("generation", "HeatGeneration", 150D).getDouble();
		generators.heatGenerationLava = Mekanism.configuration.get("generation", "HeatGenerationLava", 5D).getDouble();
		generators.heatGenerationNether = Mekanism.configuration.get("generation", "HeatGenerationNether", 100D).getDouble();
		generators.solarGeneration = Mekanism.configuration.get("generation", "SolarGeneration", 50D).getDouble();

		loadWindConfiguration();

		generators.turbineBladesPerCoil = Mekanism.configuration.get("generation", "TurbineBladesPerCoil", 4).getInt();
		generators.turbineVentGasFlow = Mekanism.configuration.get("generation", "TurbineVentGasFlow", 16000D).getDouble();
		generators.turbineDisperserGasFlow = Mekanism.configuration.get("generation", "TurbineDisperserGasFlow", 640D).getDouble();
		generators.condenserRate = Mekanism.configuration.get("generation", "TurbineCondenserFlowRate", 32000).getInt();

		if(Mekanism.configuration.hasChanged())
		{
			Mekanism.configuration.save();
		}
	}

	private void loadWindConfiguration() {
		generators.windGenerationMin = Mekanism.configuration.get("generation", "WindGenerationMin", 60D).getDouble();
		generators.windGenerationMax = Mekanism.configuration.get("generation", "WindGenerationMax", 480D).getDouble();

		//Ensure max > min to avoid division by zero later
		final int minY = Mekanism.configuration.get("generation", "WindGenerationMinY", 24).getInt();
		final int maxY = Mekanism.configuration.get("generation", "WindGenerationMaxY", 255).getInt();

		generators.windGenerationMinY = minY;
		generators.windGenerationMaxY = Math.max(minY + 1, maxY);
	}

	@Override
	public Object getClientGui(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public Container getServerGui(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		switch(ID)
		{
			case 0:
				return new HeatGeneratorContainer(player.inventory, (HeatGeneratorTileEntity)tileEntity);
			case 1:
				return new SolarGeneratorContainer(player.inventory, (SolarGeneratorTileEntity)tileEntity);
			case 3:
				return new GasGeneratorContainer(player.inventory, (GasGeneratorTileEntity)tileEntity);
			case 4:
				return new BioGeneratorContainer(player.inventory, (BioGeneratorTileEntity)tileEntity);
			case 5:
				return new WindGeneratorContainer(player.inventory, (WindGeneratorTileEntity)tileEntity);
			case 6:
				return new FilterContainer(player.inventory, (TurbineCasingTileEntity)tileEntity);
			case 7:
				return new NullContainer(player, (TurbineCasingTileEntity)tileEntity);
			case 10:
				return new ContainerReactorController(player.inventory, (TileEntityReactorController)tileEntity);
			case 11:
			case 12:
			case 13:
				return new NullContainer(player, (ContainerTileEntity)tileEntity);
			case 14:
				return new ContainerNeutronCapture(player.inventory, (TileEntityReactorNeutronCapture)tileEntity);
			case 15:
				return new NullContainer(player, (ContainerTileEntity)tileEntity);
		}
		return null;
	}
}
