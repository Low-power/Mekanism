package mekanism.client.render.tileentity;

import mekanism.client.model.ModelSeismicVibrator;
import mekanism.common.tile.SeismicVibratorTileEntity;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SeismicVibratorRenderer extends TileEntitySpecialRenderer
{
	private ModelSeismicVibrator model = new ModelSeismicVibrator();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		renderAModelAt((SeismicVibratorTileEntity)tileEntity, x, y, z, partialTick);
	}

	private void renderAModelAt(SeismicVibratorTileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);

		bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "SeismicVibrator" /*+ (tileEntity.isActive ? "On" : "")*/ + ".png"));

		switch(tileEntity.facing)
		{
			case 2: GL11.glRotatef(0, 0F, 1F, 0F); break;
			case 3: GL11.glRotatef(180, 0F, 1F, 0F); break;
			case 4: GL11.glRotatef(90, 0F, 1F, 0F); break;
			case 5: GL11.glRotatef(270, 0F, 1F, 0F); break;
		}

		float actualRate = (float)Math.sin((tileEntity.clientPiston + (tileEntity.isActive ? partialTick : 0))/5F);

		GL11.glRotatef(180F, 0F, 0F, 1F);
		model.renderWithPiston(Math.max(0, actualRate), 0.0625F);
		GL11.glPopMatrix();
	}
}
