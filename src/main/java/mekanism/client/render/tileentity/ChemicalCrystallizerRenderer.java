package mekanism.client.render.tileentity;

import mekanism.client.model.ModelChemicalCrystallizer;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.tile.ChemicalCrystallizerTileEntity;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ChemicalCrystallizerRenderer extends TileEntitySpecialRenderer
{
	private ModelChemicalCrystallizer model = new ModelChemicalCrystallizer();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		renderAModelAt((ChemicalCrystallizerTileEntity)tileEntity, x, y, z, partialTick);
	}

	private void renderAModelAt(ChemicalCrystallizerTileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "ChemicalCrystallizer.png"));

		switch(tileEntity.facing)
		{
			case 2: GL11.glRotatef(0, 0F, 1F, 0F); break;
			case 3: GL11.glRotatef(180, 0F, 1F, 0F); break;
			case 4: GL11.glRotatef(90, 0F, 1F, 0F); break;
			case 5: GL11.glRotatef(270, 0F, 1F, 0F); break;
		}

		if(tileEntity.isActive)
		{
			tileEntity.spinSpeed = Math.min(1, tileEntity.spinSpeed+0.01F);
		}
		else {
			tileEntity.spinSpeed = Math.max(0, tileEntity.spinSpeed-0.02F);
		}

		tileEntity.spin = (tileEntity.spin + (tileEntity.spinSpeed*0.1F)) % 1;

		GL11.glRotatef(180F, 0F, 0F, 1F);
		model.render(0.0625F);
		GL11.glPopMatrix();

		MekanismRenderer.machineRenderer.renderAModelAt(tileEntity, x, y, z, partialTick);
	}
}
