package mekanism.generators.client.render;

import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.model.ModelSolarGenerator;
import mekanism.generators.common.tile.SolarGeneratorTileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class RenderSolarGenerator extends TileEntitySpecialRenderer
{
	private ModelSolarGenerator model = new ModelSolarGenerator();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		renderAModelAt((SolarGeneratorTileEntity)tileEntity, x, y, z, partialTick);
	}

	private void renderAModelAt(SolarGeneratorTileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);

		bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "SolarGenerator.png"));

		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.render(0.0625F);
		GL11.glPopMatrix();
	}
}
