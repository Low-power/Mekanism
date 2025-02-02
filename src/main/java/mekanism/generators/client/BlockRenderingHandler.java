package mekanism.generators.client;

import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.model.ModelAdvancedSolarGenerator;
import mekanism.generators.client.model.ModelBioGenerator;
import mekanism.generators.client.model.ModelGasGenerator;
import mekanism.generators.client.model.ModelHeatGenerator;
import mekanism.generators.client.model.ModelSolarGenerator;
import mekanism.generators.client.model.ModelWindGenerator;
import mekanism.generators.common.GeneratorsBlocks;
import mekanism.generators.common.block.Generator.GeneratorType;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BlockRenderingHandler implements ISimpleBlockRenderingHandler
{
	private Minecraft mc = Minecraft.getMinecraft();

	public ModelAdvancedSolarGenerator advancedSolarGenerator = new ModelAdvancedSolarGenerator();
	public ModelSolarGenerator solarGenerator = new ModelSolarGenerator();
	public ModelBioGenerator bioGenerator = new ModelBioGenerator();
	public ModelHeatGenerator heatGenerator = new ModelHeatGenerator();
	public ModelGasGenerator gasGenerator = new ModelGasGenerator();
	public ModelWindGenerator windGenerator = new ModelWindGenerator();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		GL11.glPushMatrix();
		GL11.glRotatef(90F, 0F, 1F, 0F);

		if(block == GeneratorsBlocks.Generator)
		{
			if(metadata == GeneratorType.BIO_GENERATOR.meta)
			{
				GL11.glRotatef(180F, 0F, 0F, 1F);
				GL11.glTranslated(0F, -1F, 0F);
				mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "BioGenerator.png"));
				bioGenerator.render(0.0625F);
			}
			else if(metadata == GeneratorType.ADVANCED_SOLAR_GENERATOR.meta)
			{
				GL11.glRotatef(180F, 0F, 0F, 1F);
				GL11.glRotatef(90F, 0F, 1F, 0F);
				GL11.glTranslatef(0F, 0.2F, 0F);
				mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "AdvancedSolarGenerator.png"));
				advancedSolarGenerator.render(0.022F);
			}
			else if(metadata == GeneratorType.SOLAR_GENERATOR.meta)
			{
				GL11.glRotatef(180F, 0F, 0F, 1F);
				GL11.glRotatef(90F, 0F, -1F, 0F);
				GL11.glTranslated(0F, -1F, 0F);
				mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "SolarGenerator.png"));
				solarGenerator.render(0.0625F);
			}
			else if(metadata == GeneratorType.HEAT_GENERATOR.meta)
			{
				GL11.glRotatef(180F, 0F, 0F, 1F);
				GL11.glTranslated(0F, -1F, 0F);
				mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "HeatGenerator.png"));
				heatGenerator.render(0.0625F, false, mc.renderEngine);
			}
			else if(metadata == GeneratorType.GAS_GENERATOR.meta)
			{
				GL11.glRotatef(180F, 0F, 1F, 1F);
				GL11.glRotatef(90F, -1F, 0F, 0F);
				GL11.glTranslated(0F, -1F, 0F);
				GL11.glRotatef(180F, 0F, 1F, 0F);
				mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "GasGenerator.png"));
				gasGenerator.render(0.0625F);
			}
			else if(metadata == GeneratorType.WIND_GENERATOR.meta)
			{
				GL11.glRotatef(180F, 0F, 0F, 1F);
				GL11.glRotatef(180F, 0F, 1F, 0F);
				GL11.glTranslatef(0F, 0.4F, 0F);
				mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "WindGenerator.png"));
				windGenerator.render(0.016F, 0);
			}
			else if(metadata != 2) {
				MekanismRenderer.renderItem(renderer, metadata, block);
			}
		}

		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		//Handled by CTMRenderingHandler
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int meta)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return GeneratorsClientProxy.GENERATOR_RENDER_ID;
	}
}
