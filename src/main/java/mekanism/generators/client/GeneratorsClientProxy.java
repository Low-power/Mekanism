package mekanism.generators.client;

import mekanism.generators.client.gui.BioGeneratorGui;
import mekanism.generators.client.gui.GasGeneratorGui;
import mekanism.generators.client.gui.HeatGeneratorGui;
import mekanism.generators.client.gui.IndustrialTurbineGui;
import mekanism.generators.client.gui.NeutronCaptureGui;
import mekanism.generators.client.gui.ReactorControllerGui;
import mekanism.generators.client.gui.ReactorFuelGui;
import mekanism.generators.client.gui.ReactorHeatGui;
import mekanism.generators.client.gui.GuiReactorLogicAdapter;
import mekanism.generators.client.gui.ReactorStatsGui;
import mekanism.generators.client.gui.SolarGeneratorGui;
import mekanism.generators.client.gui.TurbineStatsGui;
import mekanism.generators.client.gui.WindGeneratorGui;
import mekanism.generators.client.render.RenderAdvancedSolarGenerator;
import mekanism.generators.client.render.RenderBioGenerator;
import mekanism.generators.client.render.RenderGasGenerator;
import mekanism.generators.client.render.RenderHeatGenerator;
import mekanism.generators.client.render.IndustrialTurbineRenderer;
import mekanism.generators.client.render.ReactorRenderer;
import mekanism.generators.client.render.RenderSolarGenerator;
import mekanism.generators.client.render.RenderTurbineRotor;
import mekanism.generators.client.render.RenderWindGenerator;
import mekanism.generators.client.render.item.GeneratorsItemRenderer;
import mekanism.generators.common.GeneratorsBlocks;
import mekanism.generators.common.GeneratorsCommonProxy;
import mekanism.generators.common.tile.TileEntityAdvancedSolarGenerator;
import mekanism.generators.common.tile.BioGeneratorTileEntity;
import mekanism.generators.common.tile.GasGeneratorTileEntity;
import mekanism.generators.common.tile.HeatGeneratorTileEntity;
import mekanism.generators.common.tile.SolarGeneratorTileEntity;
import mekanism.generators.common.tile.WindGeneratorTileEntity;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import mekanism.generators.common.tile.reactor.TileEntityReactorLogicAdapter;
import mekanism.generators.common.tile.reactor.TileEntityReactorNeutronCapture;
import mekanism.generators.common.tile.turbine.TurbineCasingTileEntity;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import mekanism.generators.common.tile.turbine.TurbineValveTileEntity;
import mekanism.generators.common.tile.turbine.TurbineVentTileEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GeneratorsClientProxy extends GeneratorsCommonProxy
{
	@Override
	public void registerSpecialTileEntities()
	{
		ClientRegistry.registerTileEntity(TileEntityAdvancedSolarGenerator.class, "AdvancedSolarGenerator", new RenderAdvancedSolarGenerator());
		ClientRegistry.registerTileEntity(SolarGeneratorTileEntity.class, "SolarGenerator", new RenderSolarGenerator());
		ClientRegistry.registerTileEntity(BioGeneratorTileEntity.class, "BioGenerator", new RenderBioGenerator());
		ClientRegistry.registerTileEntity(HeatGeneratorTileEntity.class, "HeatGenerator", new RenderHeatGenerator());
		ClientRegistry.registerTileEntity(GasGeneratorTileEntity.class, "GasGenerator", new RenderGasGenerator());
		ClientRegistry.registerTileEntity(WindGeneratorTileEntity.class, "WindTurbine", new RenderWindGenerator());
		ClientRegistry.registerTileEntity(TileEntityReactorController.class, "ReactorController", new ReactorRenderer());
		ClientRegistry.registerTileEntity(TileEntityTurbineRotor.class, "TurbineRod", new RenderTurbineRotor());
		ClientRegistry.registerTileEntity(TurbineCasingTileEntity.class, "TurbineCasing", new IndustrialTurbineRenderer());
		ClientRegistry.registerTileEntity(TurbineValveTileEntity.class, "TurbineValve", new IndustrialTurbineRenderer());
		ClientRegistry.registerTileEntity(TurbineVentTileEntity.class, "TurbineVent", new IndustrialTurbineRenderer());
	}

	@Override
	public void registerRenderInformation()
	{
		//Register block handler
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());

		//Register item handler
		GeneratorsItemRenderer handler = new GeneratorsItemRenderer();

		MinecraftForge.EVENT_BUS.register(this);

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GeneratorsBlocks.Generator), handler);

		System.out.println("[MekanismGenerators] Render registrations complete.");
	}

	@Override
	public GuiScreen getClientGui(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		switch(ID)
		{
			case 0:
				return new HeatGeneratorGui(player.inventory, (HeatGeneratorTileEntity)tileEntity);
			case 1:
				return new SolarGeneratorGui(player.inventory, (SolarGeneratorTileEntity)tileEntity);
			case 3:
				return new GasGeneratorGui(player.inventory, (GasGeneratorTileEntity)tileEntity);
			case 4:
				return new BioGeneratorGui(player.inventory, (BioGeneratorTileEntity)tileEntity);
			case 5:
				return new WindGeneratorGui(player.inventory, (WindGeneratorTileEntity)tileEntity);
			case 6:
				return new IndustrialTurbineGui(player.inventory, (TurbineCasingTileEntity)tileEntity);
			case 7:
				return new TurbineStatsGui(player.inventory, (TurbineCasingTileEntity)tileEntity);
			case 10:
				return new ReactorControllerGui(player.inventory, (TileEntityReactorController)tileEntity);
			case 11:
				return new ReactorHeatGui(player.inventory, (TileEntityReactorController)tileEntity);
			case 12:
				return new ReactorFuelGui(player.inventory, (TileEntityReactorController)tileEntity);
			case 13:
				return new ReactorStatsGui(player.inventory, (TileEntityReactorController)tileEntity);
			case 14:
				return new NeutronCaptureGui(player.inventory, (TileEntityReactorNeutronCapture)tileEntity);
			case 15:
				return new GuiReactorLogicAdapter(player.inventory, (TileEntityReactorLogicAdapter)tileEntity);
		}

		return null;
	}

	@SubscribeEvent
	public void onStitch(TextureStitchEvent.Pre event)
	{
		IndustrialTurbineRenderer.resetDisplayInts();
	}
}
