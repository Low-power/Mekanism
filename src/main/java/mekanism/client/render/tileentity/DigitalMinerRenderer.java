package mekanism.client.render.tileentity;

import mekanism.client.model.ModelDigitalMiner;
import mekanism.client.render.MinerVisualRenderer;
import mekanism.common.tile.DigitalMinerTileEntity;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class DigitalMinerRenderer extends TileEntitySpecialRenderer
{
	private ModelDigitalMiner model = new ModelDigitalMiner();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		renderAModelAt((DigitalMinerTileEntity)tileEntity, x, y, z, partialTick);
	}

	private void renderAModelAt(DigitalMinerTileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);

		bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "DigitalMiner.png"));

		switch(tileEntity.facing)
		{
			case 2:
				GL11.glRotatef(0, 0F, 1F, 0F);
				GL11.glTranslatef(0F, 0F, -1F);
				break;
			case 3:
				GL11.glRotatef(180, 0F, 1F, 0F);
				GL11.glTranslatef(0F, 0F, -1F);
				break;
			case 4:
				GL11.glRotatef(90, 0F, 1F, 0F);
				GL11.glTranslatef(0F, 0F, -1F);
				break;
			case 5:
				GL11.glRotatef(270, 0F, 1F, 0F);
				GL11.glTranslatef(0F, 0F, -1F);
				break;
		}

		GL11.glRotatef(180F, 0F, 0F, 1F);
		model.render(0.0625F, tileEntity.isActive, field_147501_a.field_147553_e);
		GL11.glPopMatrix();
		if(tileEntity.clientRendering)
		{
			MinerVisualRenderer.render(tileEntity);
		}
	}
}
