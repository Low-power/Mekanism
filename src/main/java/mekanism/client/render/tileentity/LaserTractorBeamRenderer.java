package mekanism.client.render.tileentity;

import mekanism.client.model.ModelLaserAmplifier;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.tile.LaserTractorBeamTileEntity;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class LaserTractorBeamRenderer extends TileEntitySpecialRenderer
{
	private ModelLaserAmplifier model = new ModelLaserAmplifier();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		renderAModelAt((LaserTractorBeamTileEntity)tileEntity, x, y, z, partialTick);
	}

	private void renderAModelAt(LaserTractorBeamTileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);

		bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "LaserTractorBeam.png"));

		switch(tileEntity.facing)
		{
			case 0:
				GL11.glTranslatef(0F, -2F, 0F);
				GL11.glRotatef(180F, 1F, 0F, 0F);
				break;
			case 5:
				GL11.glTranslatef(0F, -1F, 0F);
				GL11.glTranslatef(1F, 0F, 0F);
				GL11.glRotatef(90, 0F, 0F, -1F);
				break;
			case 4:
				GL11.glTranslatef(0F, -1F, 0F);
				GL11.glTranslatef(-1F, 0F, 0F);
				GL11.glRotatef(90, 0F, 0F, 1F);
				break;
			case 2:
				GL11.glTranslatef(0F, -1F, 0F);
				GL11.glTranslatef(0F, 0F, -1F);
				GL11.glRotatef(90, -1F, 0F, 0F);
				break;
			case 3:
				GL11.glTranslatef(0F, -1F, 0F);
				GL11.glTranslatef(0F, 0F, 1F);
				GL11.glRotatef(90, 1F, 0F, 0F);
				break;
		}

		GL11.glRotatef(180F, 0F, 0F, 1F);
		MekanismRenderer.blendOn();
		model.render(0.0625F);
		MekanismRenderer.blendOff();
		GL11.glPopMatrix();
	}
}
