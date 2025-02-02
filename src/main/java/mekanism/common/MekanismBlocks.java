package mekanism.common;

import static mekanism.common.block.BasicBlock.BasicBlockType.BASIC_BLOCK_1;
import static mekanism.common.block.BasicBlock.BasicBlockType.BASIC_BLOCK_2;
import static mekanism.common.block.Machine.MachineBlock.MACHINE_BLOCK_1;
import static mekanism.common.block.Machine.MachineBlock.MACHINE_BLOCK_2;
import static mekanism.common.block.Machine.MachineBlock.MACHINE_BLOCK_3;
import mekanism.common.block.BasicBlock;
import mekanism.common.block.BoundingBlock;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.block.EnergyCube;
import mekanism.common.block.GasTankBlock;
import mekanism.common.block.Machine;
import mekanism.common.block.BlockObsidianTNT;
import mekanism.common.block.BlockOre;
import mekanism.common.block.BlockPlastic;
import mekanism.common.block.BlockPlasticFence;
import mekanism.common.block.BlockSalt;
import mekanism.common.item.BasicBlockItem;
import mekanism.common.item.ItemBlockCardboardBox;
import mekanism.common.item.EnergyCubeItem;
import mekanism.common.item.GasTankItem;
import mekanism.common.item.MachineItem;
import mekanism.common.item.ItemBlockOre;
import mekanism.common.item.ItemBlockPlastic;
import net.minecraft.block.Block;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("Mekanism")
public class MekanismBlocks
{
	public static final Block BasicBlock = new BasicBlock(BASIC_BLOCK_1).setBlockName("BasicBlock");
	public static final Block BasicBlock2 = new BasicBlock(BASIC_BLOCK_2).setBlockName("BasicBlock2");
	public static final Block MachineBlock = new Machine(MACHINE_BLOCK_1).setBlockName("MachineBlock");
	public static final Block MachineBlock2 = new Machine(MACHINE_BLOCK_2).setBlockName("MachineBlock2");
	public static final Block MachineBlock3 = new Machine(MACHINE_BLOCK_3).setBlockName("MachineBlock3");
	public static final Block OreBlock = new BlockOre().setBlockName("OreBlock");
	public static final Block ObsidianTNT = new BlockObsidianTNT().setBlockName("ObsidianTNT").setCreativeTab(Mekanism.tabMekanism);
	public static final Block EnergyCube = new EnergyCube().setBlockName("EnergyCube");
	public static final Block BoundingBlock = (BoundingBlock)(new BoundingBlock().setBlockName("BoundingBlock"));
	public static final Block GasTank = new GasTankBlock().setBlockName("GasTank");
	public static final Block CardboardBox = new BlockCardboardBox().setBlockName("CardboardBox");
	public static final Block PlasticBlock = new BlockPlastic().setBlockName("PlasticBlock");
	public static final Block SlickPlasticBlock = new BlockPlastic().setBlockName("SlickPlasticBlock");
	public static final Block GlowPlasticBlock = new BlockPlastic().setBlockName("GlowPlasticBlock");
	public static final Block ReinforcedPlasticBlock = new BlockPlastic().setBlockName("ReinforcedPlasticBlock");
	public static final Block RoadPlasticBlock = new BlockPlastic().setBlockName("RoadPlasticBlock");
	public static final Block PlasticFence = new BlockPlasticFence().setBlockName("PlasticFence");
	public static final Block SaltBlock = new BlockSalt().setBlockName("SaltBlock");

	/**
	 * Adds and registers all blocks.
	 */
	public static void register()
	{
		GameRegistry.registerBlock(BasicBlock, BasicBlockItem.class, "BasicBlock");
		GameRegistry.registerBlock(BasicBlock2, BasicBlockItem.class, "BasicBlock2");
		GameRegistry.registerBlock(MachineBlock, MachineItem.class, "MachineBlock");
		GameRegistry.registerBlock(MachineBlock2, MachineItem.class, "MachineBlock2");
		GameRegistry.registerBlock(MachineBlock3, MachineItem.class, "MachineBlock3");
		GameRegistry.registerBlock(OreBlock, ItemBlockOre.class, "OreBlock");
		GameRegistry.registerBlock(EnergyCube, EnergyCubeItem.class, "EnergyCube");
		GameRegistry.registerBlock(ObsidianTNT, "ObsidianTNT");
		GameRegistry.registerBlock(BoundingBlock, "BoundingBlock");
		GameRegistry.registerBlock(GasTank, GasTankItem.class, "GasTank");
		GameRegistry.registerBlock(CardboardBox, ItemBlockCardboardBox.class, "CardboardBox");
		GameRegistry.registerBlock(PlasticBlock, ItemBlockPlastic.class, "PlasticBlock");
		GameRegistry.registerBlock(SlickPlasticBlock, ItemBlockPlastic.class, "SlickPlasticBlock");
		GameRegistry.registerBlock(GlowPlasticBlock, ItemBlockPlastic.class, "GlowPlasticBlock");
		GameRegistry.registerBlock(ReinforcedPlasticBlock, ItemBlockPlastic.class, "ReinforcedPlasticBlock");
		GameRegistry.registerBlock(RoadPlasticBlock, ItemBlockPlastic.class, "RoadPlasticBlock");
		GameRegistry.registerBlock(PlasticFence, ItemBlockPlastic.class, "PlasticFence");
		GameRegistry.registerBlock(SaltBlock, "SaltBlock");
	}
}
