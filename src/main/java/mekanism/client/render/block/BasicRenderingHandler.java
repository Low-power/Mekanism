package mekanism.client.render.block;

import mekanism.client.ClientProxy;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismBlocks;
import mekanism.common.block.BasicBlock.BasicType;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BasicRenderingHandler implements ISimpleBlockRenderingHandler
{
	private Minecraft mc = Minecraft.getMinecraft();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		GL11.glPushMatrix();
		GL11.glRotatef(90F, 0F, 1F, 0F);

		BasicType type = BasicType.get(block, metadata);
		if(type != null)
		{
			if(type == BasicType.STRUCTURAL_GLASS)
			{
				MekanismRenderer.blendOn();
			}
			GL11.glRotatef(180, 0F, 1F, 0F);
			MekanismRenderer.renderItem(renderer, metadata, block);
			if(type == BasicType.STRUCTURAL_GLASS)
			{
				MekanismRenderer.blendOff();
			}
		}

		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if(block == MekanismBlocks.BasicBlock || block == MekanismBlocks.BasicBlock2)
		{
			int metadata = world.getBlockMetadata(x, y, z);

			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBoundsFromBlock(block);

			return true;
		}

		return false;
	}

	@Override
	public int getRenderId()
	{
		return ClientProxy.BASIC_RENDER_ID;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}
}
