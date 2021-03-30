package mekanism.generators.common;

import mekanism.generators.common.block.Generator;
import mekanism.generators.common.block.Reactor;
import mekanism.generators.common.item.GeneratorItem;
import mekanism.generators.common.item.ReactorItem;
import net.minecraft.block.Block;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("MekanismGenerators")
public class GeneratorsBlocks
{
	public static final Block Generator = new Generator().setBlockName("Generator");
	public static final Block Reactor = new Reactor().setBlockName("Reactor");
	public static final Block ReactorGlass = new Reactor().setBlockName("ReactorGlass");

	public static void register()
	{
		GameRegistry.registerBlock(Generator, GeneratorItem.class, "Generator");
		GameRegistry.registerBlock(Reactor, ReactorItem.class, "Reactor");
		GameRegistry.registerBlock(ReactorGlass, ReactorItem.class, "ReactorGlass");
	}
}
