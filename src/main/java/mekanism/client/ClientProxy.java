package mekanism.client;

import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.client;
import mekanism.api.MekanismConfig.general;
import mekanism.api.Pos3D;
import mekanism.client.SparkleAnimation.INodeChecker;
import mekanism.client.entity.EntityLaser;
import mekanism.client.gui.AmbientAccumulatorGui;
import mekanism.client.gui.BoilerStatsGui;
import mekanism.client.gui.ChemicalCrystallizerGui;
import mekanism.client.gui.ChemicalDissolutionChamberGui;
import mekanism.client.gui.ChemicalInfuserGui;
import mekanism.client.gui.ChemicalInjectionChamberGui;
import mekanism.client.gui.ChemicalOxidizerGui;
import mekanism.client.gui.ChemicalWasherGui;
import mekanism.client.gui.CombinerGui;
import mekanism.client.gui.CreditsGui;
import mekanism.client.gui.CrusherGui;
import mekanism.client.gui.GuiDictionary;
import mekanism.client.gui.DigitalMinerGui;
import mekanism.client.gui.DynamicTankGui;
import mekanism.client.gui.ElectricPumpGui;
import mekanism.client.gui.ElectrolyticSeparatorGui;
import mekanism.client.gui.EnergizedSmelterGui;
import mekanism.client.gui.EnergyCubeGui;
import mekanism.client.gui.EnrichmentChamberGui;
import mekanism.client.gui.FactoryGui;
import mekanism.client.gui.FluidTankGui;
import mekanism.client.gui.FluidicPlenisherGui;
import mekanism.client.gui.FormulaicAssemblicatorGui;
import mekanism.client.gui.FuelwoodHeaterGui;
import mekanism.client.gui.GasTankGui;
import mekanism.client.gui.InductionMatrixGui;
import mekanism.client.gui.LaserAmplifierGui;
import mekanism.client.gui.LaserTractorBeamGui;
import mekanism.client.gui.MatrixStatsGui;
import mekanism.client.gui.MetallurgicInfuserGui;
import mekanism.client.gui.OredictionificatorGui;
import mekanism.client.gui.OsmiumCompressorGui;
import mekanism.client.gui.PRCGui;
import mekanism.client.gui.PersonalChestGui;
import mekanism.client.gui.PrecisionSawmillGui;
import mekanism.client.gui.PurificationChamberGui;
import mekanism.client.gui.QuantumEntangloporterGui;
import mekanism.client.gui.ResistiveHeaterGui;
import mekanism.client.gui.GuiRobitCrafting;
import mekanism.client.gui.GuiRobitInventory;
import mekanism.client.gui.RobitMainGui;
import mekanism.client.gui.GuiRobitRepair;
import mekanism.client.gui.GuiRobitSmelting;
import mekanism.client.gui.RotaryCondensentratorGui;
import mekanism.client.gui.GuiSeismicReader;
import mekanism.client.gui.SeismicVibratorGui;
import mekanism.client.gui.SideConfigurationGui;
import mekanism.client.gui.SolarNeutronActivatorGui;
import mekanism.client.gui.TeleporterGui;
import mekanism.client.gui.ThermalEvaporationControllerGui;
import mekanism.client.gui.ThermoelectricBoilerGui;
import mekanism.client.gui.TransporterConfigGui;
import mekanism.client.gui.UpgradeManagementGui;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.RenderGlowPanel;
import mekanism.client.render.TransmitterPartRenderer;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.render.block.BasicRenderingHandler;
import mekanism.client.render.block.CTMRenderingHandler;
import mekanism.client.render.block.MachineRenderingHandler;
import mekanism.client.render.block.PlasticRenderingHandler;
import mekanism.client.render.entity.RenderBalloon;
import mekanism.client.render.entity.RenderFlame;
import mekanism.client.render.entity.RenderObsidianTNTPrimed;
import mekanism.client.render.entity.RenderRobit;
import mekanism.client.render.item.ItemRenderingHandler;
import mekanism.client.render.tileentity.RenderBin;
import mekanism.client.render.tileentity.ChargepadRenderer;
import mekanism.client.render.tileentity.ChemicalCrystallizerRenderer;
import mekanism.client.render.tileentity.ChemicalDissolutionChamberRenderer;
import mekanism.client.render.tileentity.ChemicalInfuserRenderer;
import mekanism.client.render.tileentity.ChemicalOxidizerRenderer;
import mekanism.client.render.tileentity.ChemicalWasherRenderer;
import mekanism.client.render.tileentity.ConfigurableMachineRenderer;
import mekanism.client.render.tileentity.DigitalMinerRenderer;
import mekanism.client.render.tileentity.RenderDynamicTank;
import mekanism.client.render.tileentity.ElectricPumpRenderer;
import mekanism.client.render.tileentity.ElectrolyticSeparatorRenderer;
import mekanism.client.render.tileentity.EnergyCubeRenderer;
import mekanism.client.render.tileentity.FluidTankRenderer;
import mekanism.client.render.tileentity.FluidicPlenisherRenderer;
import mekanism.client.render.tileentity.GasTankRenderer;
import mekanism.client.render.tileentity.LaserRenderer;
import mekanism.client.render.tileentity.LaserAmplifierRenderer;
import mekanism.client.render.tileentity.LaserTractorBeamRenderer;
import mekanism.client.render.tileentity.LogisticalSorterRenderer;
import mekanism.client.render.tileentity.MetallurgicInfuserRenderer;
import mekanism.client.render.tileentity.RenderObsidianTNT;
import mekanism.client.render.tileentity.RenderPersonalChest;
import mekanism.client.render.tileentity.RenderPressurizedReactionChamber;
import mekanism.client.render.tileentity.QuantumEntangloporterRenderer;
import mekanism.client.render.tileentity.RenderResistiveHeater;
import mekanism.client.render.tileentity.RotaryCondensentratorRenderer;
import mekanism.client.render.tileentity.SeismicVibratorRenderer;
import mekanism.client.render.tileentity.SolarNeutronActivatorRenderer;
import mekanism.client.render.tileentity.TeleporterRenderer;
import mekanism.client.render.tileentity.RenderThermalEvaporationController;
import mekanism.client.render.tileentity.RenderThermoelectricBoiler;
import mekanism.common.CommonProxy;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.block.Machine.MachineType;
import mekanism.common.entity.EntityBabySkeleton;
import mekanism.common.entity.Balloon;
import mekanism.common.entity.EntityFlame;
import mekanism.common.entity.EntityObsidianTNT;
import mekanism.common.entity.Robit;
import mekanism.common.inventory.InventoryPersonalChest;
import mekanism.common.item.PortableTeleporter;
import mekanism.common.item.ItemSeismicReader;
import mekanism.common.multiblock.MultiblockManager;
import mekanism.common.network.PortableTeleporterPacket.PortableTeleporterMessage;
import mekanism.common.tile.AdvancedElectricMachineTileEntity;
import mekanism.common.tile.AdvancedFactoryTileEntity;
import mekanism.common.tile.TileEntityAmbientAccumulator;
import mekanism.common.tile.BinTileEntity;
import mekanism.common.tile.TileEntityBoilerCasing;
import mekanism.common.tile.TileEntityBoilerValve;
import mekanism.common.tile.ChargepadTileEntity;
import mekanism.common.tile.ChemicalCrystallizerTileEntity;
import mekanism.common.tile.ChemicalDissolutionChamberTileEntity;
import mekanism.common.tile.ChemicalInfuserTileEntity;
import mekanism.common.tile.TileEntityChemicalInjectionChamber;
import mekanism.common.tile.ChemicalOxidizerTileEntity;
import mekanism.common.tile.ChemicalWasherTileEntity;
import mekanism.common.tile.TileEntityCombiner;
import mekanism.common.tile.TileEntityCrusher;
import mekanism.common.tile.DigitalMinerTileEntity;
import mekanism.common.tile.TileEntityDynamicTank;
import mekanism.common.tile.TileEntityDynamicValve;
import mekanism.common.tile.ElectricMachineTileEntity;
import mekanism.common.tile.ElectricPumpTileEntity;
import mekanism.common.tile.ElectrolyticSeparatorTileEntity;
import mekanism.common.tile.EliteFactoryTileEntity;
import mekanism.common.tile.TileEntityEnergizedSmelter;
import mekanism.common.tile.EnergyCubeTileEntity;
import mekanism.common.tile.TileEntityEnrichmentChamber;
import mekanism.common.tile.FactoryTileEntity;
import mekanism.common.tile.FluidTankTileEntity;
import mekanism.common.tile.FluidicPlenisherTileEntity;
import mekanism.common.tile.FormulaicAssemblicatorTileEntity;
import mekanism.common.tile.FuelwoodHeaterTileEntity;
import mekanism.common.tile.GasTankTileEntity;
import mekanism.common.tile.TileEntityInductionCasing;
import mekanism.common.tile.TileEntityInductionCell;
import mekanism.common.tile.TileEntityInductionPort;
import mekanism.common.tile.TileEntityInductionProvider;
import mekanism.common.tile.LaserTileEntity;
import mekanism.common.tile.LaserAmplifierTileEntity;
import mekanism.common.tile.LaserTractorBeamTileEntity;
import mekanism.common.tile.LogisticalSorterTileEntity;
import mekanism.common.tile.MetallurgicInfuserTileEntity;
import mekanism.common.tile.TileEntityMultiblock;
import mekanism.common.tile.TileEntityObsidianTNT;
import mekanism.common.tile.OredictionificatorTileEntity;
import mekanism.common.tile.TileEntityOsmiumCompressor;
import mekanism.common.tile.PRCTileEntity;
import mekanism.common.tile.PersonalChestTileEntity;
import mekanism.common.tile.TileEntityPrecisionSawmill;
import mekanism.common.tile.TileEntityPurificationChamber;
import mekanism.common.tile.QuantumEntangloporterTileEntity;
import mekanism.common.tile.ResistiveHeaterTileEntity;
import mekanism.common.tile.RotaryCondensentratorTileEntity;
import mekanism.common.tile.SeismicVibratorTileEntity;
import mekanism.common.tile.SolarNeutronActivatorTileEntity;
import mekanism.common.tile.TileEntityStructuralGlass;
import mekanism.common.tile.TeleporterTileEntity;
import mekanism.common.tile.TileEntityThermalEvaporationController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;

/**
 * Client proxy for the Mekanism mod.
 * @author AidanBrady
 *
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void loadConfiguration()
	{
		super.loadConfiguration();

		client.enablePlayerSounds = Mekanism.configuration.get("client", "EnablePlayerSounds", true).getBoolean();
		client.enableMachineSounds = Mekanism.configuration.get("client", "EnableMachineSounds", true).getBoolean();
		client.holidays = Mekanism.configuration.get("client", "Holidays", true).getBoolean();
		client.baseSoundVolume = (float)Mekanism.configuration.get("client", "SoundVolume", 1D).getDouble();
		client.machineEffects = Mekanism.configuration.get("client", "MachineEffects", true).getBoolean();
		client.oldTransmitterRender = Mekanism.configuration.get("client", "OldTransmitterRender", false).getBoolean();
		client.replaceSoundsWhenResuming = Mekanism.configuration.get("client", "ReplaceSoundsWhenResuming", true,
				"If true, will reduce lagging between player sounds. Setting to false will reduce GC load").getBoolean();
		client.renderCTM = Mekanism.configuration.get("client", "CTMRenderer", true).getBoolean();
		client.enableAmbientLighting = Mekanism.configuration.get("client", "EnableAmbientLighting", true).getBoolean();
		client.ambientLightingLevel = Mekanism.configuration.get("client", "AmbientLightingLevel", 15).getInt();
		client.opaqueTransmitters = Mekanism.configuration.get("client", "OpaqueTransmitterRender", false).getBoolean();

		if(Mekanism.configuration.hasChanged())
		{
			Mekanism.configuration.save();
		}
	}

	@Override
	public int getArmorIndex(String string)
	{
		return RenderingRegistry.addNewArmourRendererPrefix(string);
	}

	@Override
	public void openPersonalChest(EntityPlayer player, int id, int windowId, boolean isBlock, int x, int y, int z)
	{
		PersonalChestTileEntity tileEntity = (PersonalChestTileEntity)player.worldObj.getTileEntity(x, y, z);

		if(id == 0)
		{
			if(isBlock)
			{
				FMLClientHandler.instance().displayGuiScreen(player, new PersonalChestGui(player.inventory, tileEntity));
				player.openContainer.windowId = windowId;
			}
			else {
				ItemStack stack = player.getCurrentEquippedItem();

				if(MachineType.get(stack) == MachineType.PERSONAL_CHEST)
				{
					InventoryPersonalChest inventory = new InventoryPersonalChest(player);
					FMLClientHandler.instance().displayGuiScreen(player, new PersonalChestGui(player.inventory, inventory));
					player.openContainer.windowId = windowId;
				}
			}
		}
	}

	@Override
	public void registerSpecialTileEntities()
	{
		ClientRegistry.registerTileEntity(TileEntityEnrichmentChamber.class, "EnrichmentChamber", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(TileEntityOsmiumCompressor.class, "OsmiumCompressor", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(TileEntityCombiner.class, "Combiner", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(TileEntityCrusher.class, "Crusher", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(FactoryTileEntity.class, "SmeltingFactory", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(AdvancedFactoryTileEntity.class, "AdvancedSmeltingFactory", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(EliteFactoryTileEntity.class, "UltimateSmeltingFactory", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(TileEntityPurificationChamber.class, "PurificationChamber", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(TileEntityEnergizedSmelter.class, "EnergizedSmelter", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(MetallurgicInfuserTileEntity.class, "MetallurgicInfuser", new MetallurgicInfuserRenderer());
		ClientRegistry.registerTileEntity(TileEntityObsidianTNT.class, "ObsidianTNT", new RenderObsidianTNT());
		ClientRegistry.registerTileEntity(GasTankTileEntity.class, "GasTank", new GasTankRenderer());
		ClientRegistry.registerTileEntity(EnergyCubeTileEntity.class, "EnergyCube", new EnergyCubeRenderer());
		ClientRegistry.registerTileEntity(ElectricPumpTileEntity.class, "ElectricPump", new ElectricPumpRenderer());
		ClientRegistry.registerTileEntity(PersonalChestTileEntity.class, "ElectricChest", new RenderPersonalChest()); //TODO rename
		ClientRegistry.registerTileEntity(TileEntityDynamicTank.class, "DynamicTank", new RenderDynamicTank());
		ClientRegistry.registerTileEntity(TileEntityDynamicValve.class, "DynamicValve", new RenderDynamicTank());
		ClientRegistry.registerTileEntity(ChargepadTileEntity.class, "Chargepad", new ChargepadRenderer());
		ClientRegistry.registerTileEntity(LogisticalSorterTileEntity.class, "LogisticalSorter", new LogisticalSorterRenderer());
		ClientRegistry.registerTileEntity(BinTileEntity.class, "Bin", new RenderBin());
		ClientRegistry.registerTileEntity(DigitalMinerTileEntity.class, "DigitalMiner", new DigitalMinerRenderer());
		ClientRegistry.registerTileEntity(RotaryCondensentratorTileEntity.class, "RotaryCondensentrator", new RotaryCondensentratorRenderer());
		ClientRegistry.registerTileEntity(TeleporterTileEntity.class, "MekanismTeleporter", new TeleporterRenderer());
		ClientRegistry.registerTileEntity(ChemicalOxidizerTileEntity.class, "ChemicalOxidizer", new ChemicalOxidizerRenderer());
		ClientRegistry.registerTileEntity(ChemicalInfuserTileEntity.class, "ChemicalInfuser", new ChemicalInfuserRenderer());
		ClientRegistry.registerTileEntity(TileEntityChemicalInjectionChamber.class, "ChemicalInjectionChamber", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(ElectrolyticSeparatorTileEntity.class, "ElectrolyticSeparator", new ElectrolyticSeparatorRenderer());
		ClientRegistry.registerTileEntity(TileEntityThermalEvaporationController.class, "SalinationController", new RenderThermalEvaporationController()); //TODO rename
		ClientRegistry.registerTileEntity(TileEntityPrecisionSawmill.class, "PrecisionSawmill", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(ChemicalDissolutionChamberTileEntity.class, "ChemicalDissolutionChamber", new ChemicalDissolutionChamberRenderer());
		ClientRegistry.registerTileEntity(ChemicalWasherTileEntity.class, "ChemicalWasher", new ChemicalWasherRenderer());
		ClientRegistry.registerTileEntity(ChemicalCrystallizerTileEntity.class, "ChemicalCrystallizer", new ChemicalCrystallizerRenderer());
		ClientRegistry.registerTileEntity(SeismicVibratorTileEntity.class, "SeismicVibrator", new SeismicVibratorRenderer());
		ClientRegistry.registerTileEntity(PRCTileEntity.class, "PressurizedReactionChamber", new RenderPressurizedReactionChamber());
		ClientRegistry.registerTileEntity(FluidTankTileEntity.class, "PortableTank", new FluidTankRenderer()); //TODO rename
		ClientRegistry.registerTileEntity(FluidicPlenisherTileEntity.class, "FluidicPlenisher", new FluidicPlenisherRenderer());
		ClientRegistry.registerTileEntity(LaserTileEntity.class, "Laser", new LaserRenderer());
		ClientRegistry.registerTileEntity(LaserAmplifierTileEntity.class, "LaserAmplifier", new LaserAmplifierRenderer());
		ClientRegistry.registerTileEntity(LaserTractorBeamTileEntity.class, "LaserTractorBeam", new LaserTractorBeamRenderer());
		ClientRegistry.registerTileEntity(SolarNeutronActivatorTileEntity.class, "SolarNeutronActivator", new SolarNeutronActivatorRenderer());
		GameRegistry.registerTileEntity(TileEntityAmbientAccumulator.class, "AmbientAccumulator");
		GameRegistry.registerTileEntity(TileEntityInductionCasing.class, "InductionCasing");
		GameRegistry.registerTileEntity(TileEntityInductionPort.class, "InductionPort");
		GameRegistry.registerTileEntity(TileEntityInductionCell.class, "InductionCell");
		GameRegistry.registerTileEntity(TileEntityInductionProvider.class, "InductionProvider");
		GameRegistry.registerTileEntity(OredictionificatorTileEntity.class, "Oredictionificator");
		GameRegistry.registerTileEntity(TileEntityStructuralGlass.class, "StructuralGlass");
		ClientRegistry.registerTileEntity(FormulaicAssemblicatorTileEntity.class, "FormulaicAssemblicator", new ConfigurableMachineRenderer());
		ClientRegistry.registerTileEntity(ResistiveHeaterTileEntity.class, "ResistiveHeater", new RenderResistiveHeater());
		ClientRegistry.registerTileEntity(TileEntityBoilerCasing.class, "BoilerCasing", new RenderThermoelectricBoiler());
		ClientRegistry.registerTileEntity(TileEntityBoilerValve.class, "BoilerValve", new RenderThermoelectricBoiler());
		ClientRegistry.registerTileEntity(QuantumEntangloporterTileEntity.class, "QuantumEntangloporter", new QuantumEntangloporterRenderer());
		GameRegistry.registerTileEntity(FuelwoodHeaterTileEntity.class, "FuelwoodHeater");
	}

	@Override
	public void registerRenderInformation()
	{
		TransmitterPartRenderer.init();
		RenderGlowPanel.init();

		//Register entity rendering handlers
		RenderingRegistry.registerEntityRenderingHandler(EntityObsidianTNT.class, new RenderObsidianTNTPrimed());
		RenderingRegistry.registerEntityRenderingHandler(Robit.class, new RenderRobit());
		RenderingRegistry.registerEntityRenderingHandler(Balloon.class, new RenderBalloon());
		RenderingRegistry.registerEntityRenderingHandler(EntityBabySkeleton.class, new RenderSkeleton());
		RenderingRegistry.registerEntityRenderingHandler(EntityFlame.class, new RenderFlame());

		//Register item handler
		ItemRenderingHandler handler = new ItemRenderingHandler();

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MekanismBlocks.EnergyCube), handler);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MekanismBlocks.MachineBlock), handler);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MekanismBlocks.MachineBlock2), handler);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MekanismBlocks.MachineBlock3), handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.Robit, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.WalkieTalkie, handler);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MekanismBlocks.GasTank), handler);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MekanismBlocks.ObsidianTNT), handler);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MekanismBlocks.BasicBlock), handler);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MekanismBlocks.BasicBlock2), handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.Jetpack, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.ArmoredJetpack, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.PartTransmitter, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.GasMask, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.ScubaTank, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.Balloon, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.FreeRunners, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.AtomicDisassembler, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.GlowPanel, handler);
		MinecraftForgeClient.registerItemRenderer(MekanismItems.Flamethrower, handler);

		//Register block handlers
		RenderingRegistry.registerBlockHandler(new MachineRenderingHandler());
		RenderingRegistry.registerBlockHandler(new BasicRenderingHandler());
		RenderingRegistry.registerBlockHandler(new PlasticRenderingHandler());
		RenderingRegistry.registerBlockHandler(new CTMRenderingHandler());

		Mekanism.logger.info("Render registrations complete.");
	}

	@Override
	public GuiScreen getClientGui(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		switch(ID)
		{
			case 0:
				return new GuiDictionary(player.inventory);
			case 1:
				return new CreditsGui();
			case 2:
				return new DigitalMinerGui(player.inventory, (DigitalMinerTileEntity)tileEntity);
			case 3:
				return new EnrichmentChamberGui(player.inventory, (ElectricMachineTileEntity)tileEntity);
			case 4:
				return new OsmiumCompressorGui(player.inventory, (AdvancedElectricMachineTileEntity)tileEntity);
			case 5:
				return new CombinerGui(player.inventory, (AdvancedElectricMachineTileEntity)tileEntity);
			case 6:
				return new CrusherGui(player.inventory, (ElectricMachineTileEntity)tileEntity);
			case 7:
				return new RotaryCondensentratorGui(player.inventory, (RotaryCondensentratorTileEntity)tileEntity);
			case 8:
				return new EnergyCubeGui(player.inventory, (EnergyCubeTileEntity)tileEntity);
			case 9:
				return new SideConfigurationGui(player, (ISideConfiguration)tileEntity);
			case 10:
				return new GasTankGui(player.inventory, (GasTankTileEntity)tileEntity);
			case 11:
				return new FactoryGui(player.inventory, (FactoryTileEntity)tileEntity);
			case 12:
				return new MetallurgicInfuserGui(player.inventory, (MetallurgicInfuserTileEntity)tileEntity);
			case 13:
				return new TeleporterGui(player.inventory, (TeleporterTileEntity)tileEntity);
			case 14:
				ItemStack itemStack = player.getCurrentEquippedItem();

				if(itemStack != null && itemStack.getItem() instanceof PortableTeleporter)
				{
					return new TeleporterGui(player, itemStack);
				}
			case 15:
				return new PurificationChamberGui(player.inventory, (AdvancedElectricMachineTileEntity)tileEntity);
			case 16:
				return new EnergizedSmelterGui(player.inventory, (ElectricMachineTileEntity)tileEntity);
			case 17:
				return new ElectricPumpGui(player.inventory, (ElectricPumpTileEntity)tileEntity);
			case 18:
				return new DynamicTankGui(player.inventory, (TileEntityDynamicTank)tileEntity);
			//EMPTY 19, 20
			case 21:
				Robit robit = (Robit)world.getEntityByID(x);

				if(robit != null)
				{
					return new RobitMainGui(player.inventory, robit);
				}
			case 22:
				robit = (Robit)world.getEntityByID(x);

				if(robit != null)
				{
					return new GuiRobitCrafting(player.inventory, robit);
				}
			case 23:
				robit = (Robit)world.getEntityByID(x);

				if(robit != null)
				{
					return new GuiRobitInventory(player.inventory, robit);
				}
			case 24:
				robit = (Robit)world.getEntityByID(x);

				if(robit != null)
				{
					return new GuiRobitSmelting(player.inventory, robit);
				}
			case 25:
				robit = (Robit)world.getEntityByID(x);

				if(robit != null)
				{
					return new GuiRobitRepair(player.inventory, robit);
				}
			case 29:
				return new ChemicalOxidizerGui(player.inventory, (ChemicalOxidizerTileEntity)tileEntity);
			case 30:
				return new ChemicalInfuserGui(player.inventory, (ChemicalInfuserTileEntity)tileEntity);
			case 31:
				return new ChemicalInjectionChamberGui(player.inventory, (AdvancedElectricMachineTileEntity)tileEntity);
			case 32:
				return new ElectrolyticSeparatorGui(player.inventory, (ElectrolyticSeparatorTileEntity)tileEntity);
			case 33:
				return new ThermalEvaporationControllerGui(player.inventory, (TileEntityThermalEvaporationController)tileEntity);
			case 34:
				return new PrecisionSawmillGui(player.inventory, (TileEntityPrecisionSawmill)tileEntity);
			case 35:
				return new ChemicalDissolutionChamberGui(player.inventory, (ChemicalDissolutionChamberTileEntity)tileEntity);
			case 36:
				return new ChemicalWasherGui(player.inventory, (ChemicalWasherTileEntity)tileEntity);
			case 37:
				return new ChemicalCrystallizerGui(player.inventory, (ChemicalCrystallizerTileEntity)tileEntity);
			case 38:
				ItemStack itemStack1 = player.getCurrentEquippedItem().copy();

				if(itemStack1 != null && itemStack1.getItem() instanceof ItemSeismicReader)
				{
					return new GuiSeismicReader(world, new Coord4D(player), itemStack1);
				}
			case 39:
				return new SeismicVibratorGui(player.inventory, (SeismicVibratorTileEntity)tileEntity);
			case 40:
				return new PRCGui(player.inventory, (PRCTileEntity)tileEntity);
			case 41:
				return new FluidTankGui(player.inventory, (FluidTankTileEntity)tileEntity);
			case 42:
				return new FluidicPlenisherGui(player.inventory, (FluidicPlenisherTileEntity)tileEntity);
			case 43:
				return new UpgradeManagementGui(player.inventory, (IUpgradeTile)tileEntity);
			case 44:
				return new LaserAmplifierGui(player.inventory, (LaserAmplifierTileEntity)tileEntity);
			case 45:
				return new LaserTractorBeamGui(player.inventory, (LaserTractorBeamTileEntity)tileEntity);
			case 46:
				return new QuantumEntangloporterGui(player.inventory, (QuantumEntangloporterTileEntity)tileEntity);
			case 47:
				return new SolarNeutronActivatorGui(player.inventory, (SolarNeutronActivatorTileEntity)tileEntity);
			case 48:
				return new AmbientAccumulatorGui(player, (TileEntityAmbientAccumulator)tileEntity);
			case 49:
				return new InductionMatrixGui(player.inventory, (TileEntityInductionCasing)tileEntity);
			case 50:
				return new MatrixStatsGui(player.inventory, (TileEntityInductionCasing)tileEntity);
			case 51:
				return new TransporterConfigGui(player, (ISideConfiguration)tileEntity);
			case 52:
				return new OredictionificatorGui(player.inventory, (OredictionificatorTileEntity)tileEntity);
			case 53:
				return new ResistiveHeaterGui(player.inventory, (ResistiveHeaterTileEntity)tileEntity);
			case 54:
				return new ThermoelectricBoilerGui(player.inventory, (TileEntityBoilerCasing)tileEntity);
			case 55:
				return new BoilerStatsGui(player.inventory, (TileEntityBoilerCasing)tileEntity);
			case 56:
				return new FormulaicAssemblicatorGui(player.inventory, (FormulaicAssemblicatorTileEntity)tileEntity);
			case 58:
				return new FuelwoodHeaterGui(player.inventory, (FuelwoodHeaterTileEntity)tileEntity);
		}
		return null;
	}

	@Override
	public void handleTeleporterUpdate(PortableTeleporterMessage message)
	{
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen instanceof TeleporterGui && ((TeleporterGui)screen).itemStack != null)
		{
			TeleporterGui teleporter = (TeleporterGui)screen;
			teleporter.clientStatus = message.status;
			teleporter.clientFreq = message.frequency;
			teleporter.clientPublicCache = message.publicCache;
			teleporter.clientPrivateCache = message.privateCache;
			teleporter.updateButtons();
		}
	}

	@Override
	public void addHitEffects(Coord4D coord, MovingObjectPosition mop)
	{
		if(Minecraft.getMinecraft().theWorld != null)
		{
			Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(coord.xCoord, coord.yCoord, coord.zCoord, mop);
		}
	}

	@Override
	public void doGenericSparkle(TileEntity tileEntity, INodeChecker checker)
	{
		new SparkleAnimation(tileEntity, checker).run();
	}

	@Override
	public void doMultiblockSparkle(final TileEntityMultiblock<?> tileEntity)
	{
		new SparkleAnimation(tileEntity, new INodeChecker() {
			@Override
			public boolean isNode(TileEntity tile)
			{
				return MultiblockManager.areEqual(tile, tileEntity);
			}
		}).run();
	}

	@Override
	public void loadUtilities()
	{
		super.loadUtilities();

		FMLCommonHandler.instance().bus().register(new ClientConnectionHandler());
		FMLCommonHandler.instance().bus().register(new ClientPlayerTracker());
		FMLCommonHandler.instance().bus().register(new ClientTickHandler());
		FMLCommonHandler.instance().bus().register(new RenderTickHandler());
		new MekanismKeyHandler();

		HolidayManager.init();
	}

	@Override
	public void preInit()
	{
		MekanismRenderer.init();
	}

	@Override
	public double getReach(EntityPlayer player)
	{
		return Minecraft.getMinecraft().playerController.getBlockReachDistance();
	}

	@Override
	public boolean isPaused()
	{
		if(FMLClientHandler.instance().getClient().isSingleplayer() && !FMLClientHandler.instance().getClient().getIntegratedServer().getPublic())
		{
			GuiScreen screen = FMLClientHandler.instance().getClient().currentScreen;

			if(screen != null && screen.doesGuiPauseGame())
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public File getMinecraftDir()
	{
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public void onConfigSync(boolean fromPacket)
	{
		super.onConfigSync(fromPacket);

		if(fromPacket && general.voiceServerEnabled && MekanismClient.voiceClient != null)
		{
			MekanismClient.voiceClient.start();
		}
	}

	@Override
	public EntityPlayer getPlayer(MessageContext context)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			return context.getServerHandler().playerEntity;
		}
		else {
			return Minecraft.getMinecraft().thePlayer;
		}
	}

	@Override
	public void renderLaser(World world, Pos3D from, Pos3D to, ForgeDirection direction, double energy)
	{
		Minecraft.getMinecraft().effectRenderer.addEffect(new EntityLaser(world, from, to, direction, energy));
	}

	@Override
	public FontRenderer getFontRenderer()
	{
		return Minecraft.getMinecraft().fontRenderer;
	}
}
