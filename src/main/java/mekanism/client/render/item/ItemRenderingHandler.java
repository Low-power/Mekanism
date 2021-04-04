package mekanism.client.render.item;

import mekanism.api.EnumColor;
import mekanism.api.energy.IEnergizedItem;
import mekanism.client.ClientProxy;
import mekanism.client.MekanismClient;
import mekanism.client.model.ModelArmoredJetpack;
import mekanism.client.model.ModelAtomicDisassembler;
import mekanism.client.model.EnergyCubeModel;
import mekanism.client.model.EnergyCubeModel.ModelEnergyCore;
import mekanism.client.model.ModelFlamethrower;
import mekanism.client.model.ModelFreeRunners;
import mekanism.client.model.ModelGasMask;
import mekanism.client.model.ModelGasTank;
import mekanism.client.model.ModelJetpack;
import mekanism.client.model.ModelObsidianTNT;
import mekanism.client.model.ModelRobit;
import mekanism.client.model.ModelScubaTank;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.RenderGlowPanel;
import mekanism.client.render.TransmitterPartRenderer;
import mekanism.client.render.entity.RenderBalloon;
import mekanism.client.render.tileentity.RenderBin;
import mekanism.client.render.tileentity.EnergyCubeRenderer;
import mekanism.client.render.tileentity.FluidTankRenderer;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.SideData.IOState;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.EnergyCubeTier;
import mekanism.common.Tier.FluidTankTier;
import mekanism.common.base.IEnergyCube;
import mekanism.common.block.BasicBlock.BasicType;
import mekanism.common.block.Machine.MachineType;
import mekanism.common.inventory.InventoryBin;
import mekanism.common.item.ItemAtomicDisassembler;
import mekanism.common.item.BalloonItem;
import mekanism.common.item.BasicBlockItem;
import mekanism.common.item.GasTankItem;
import mekanism.common.item.MachineItem;
import mekanism.common.item.ItemFlamethrower;
import mekanism.common.item.ItemFreeRunners;
import mekanism.common.item.ItemGasMask;
import mekanism.common.item.RobitItem;
import mekanism.common.item.ItemScubaTank;
import mekanism.common.item.ItemWalkieTalkie;
import mekanism.common.multipart.ItemGlowPanel;
import mekanism.common.multipart.ItemPartTransmitter;
import mekanism.common.multipart.TransmitterType;
import mekanism.common.tile.BinTileEntity;
import mekanism.common.tile.FluidTankTileEntity;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ItemRenderingHandler implements IItemRenderer
{
	private Minecraft mc = Minecraft.getMinecraft();

	public ModelRobit robit = new ModelRobit();
	public ModelChest personalChest = new ModelChest();
	public EnergyCubeModel energyCube = new EnergyCubeModel();
	public ModelEnergyCore energyCore = new ModelEnergyCore();
	public ModelGasTank gasTank = new ModelGasTank();
	public ModelObsidianTNT obsidianTNT = new ModelObsidianTNT();
	public ModelJetpack jetpack = new ModelJetpack();
	public ModelArmoredJetpack armoredJetpack = new ModelArmoredJetpack();
	public ModelGasMask gasMask = new ModelGasMask();
	public ModelScubaTank scubaTank = new ModelScubaTank();
	public ModelFreeRunners freeRunners = new ModelFreeRunners();
	public ModelAtomicDisassembler atomicDisassembler = new ModelAtomicDisassembler();
	public ModelFlamethrower flamethrower = new ModelFlamethrower();

	private final RenderBalloon balloonRenderer = new RenderBalloon();
	private final RenderBin binRenderer = (RenderBin)TileEntityRendererDispatcher.instance.mapSpecialRenderers.get(BinTileEntity.class);
	private final FluidTankRenderer portableTankRenderer = (FluidTankRenderer)TileEntityRendererDispatcher.instance.mapSpecialRenderers.get(FluidTankTileEntity.class);
	private final RenderItem renderItem = (RenderItem)RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		if(item.getItem() == MekanismItems.WalkieTalkie)
		{
			return type != ItemRenderType.INVENTORY;
		}

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item_stack, Object... data)
	{
		RenderBlocks renderBlocks = (RenderBlocks)data[0];

		if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}

		Item item = item_stack.getItem();
		if(item instanceof IEnergyCube)
		{
			EnergyCubeTier tier = ((IEnergyCube)item).getEnergyCubeTier(item_stack);
			IEnergizedItem energized = (IEnergizedItem)item;
			mc.renderEngine.bindTexture(EnergyCubeRenderer.baseTexture);

			GL11.glRotatef(180F, 0F, 0F, 1F);
			GL11.glRotatef(270F, 0F, -1F, 0F);
			GL11.glTranslatef(0F, -1F, 0F);

			MekanismRenderer.blendOn();
			energyCube.render(0.0625F, tier, mc.renderEngine);
			for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				mc.renderEngine.bindTexture(EnergyCubeRenderer.baseTexture);
				energyCube.renderSide(0.0625F, side, side == ForgeDirection.NORTH ? IOState.OUTPUT : IOState.INPUT, tier, mc.renderEngine);
			}
			MekanismRenderer.blendOff();

			GL11.glPushMatrix();
			GL11.glTranslated(0.0, 1.0, 0.0);
			mc.renderEngine.bindTexture(EnergyCubeRenderer.coreTexture);

			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			MekanismRenderer.glowOn();

			EnumColor c = tier.getBaseTier().getColor();

			GL11.glPushMatrix();
			GL11.glScalef(0.4F, 0.4F, 0.4F);
			GL11.glColor4f(c.getColor(0), c.getColor(1), c.getColor(2), (float)(energized.getEnergy(item_stack)/energized.getMaxEnergy(item_stack)));
			GL11.glTranslatef(0, (float)Math.sin(Math.toRadians((MekanismClient.ticksPassed + MekanismRenderer.getPartialTick()) * 3)) / 7, 0);
			GL11.glRotatef((MekanismClient.ticksPassed + MekanismRenderer.getPartialTick()) * 4, 0, 1, 0);
			GL11.glRotatef(36F + (MekanismClient.ticksPassed + MekanismRenderer.getPartialTick()) * 4, 0, 1, 1);
			energyCore.render(0.0625F);
			GL11.glPopMatrix();

			MekanismRenderer.glowOff();

			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
			GL11.glDisable(GL11.GL_BLEND);

			GL11.glPopMatrix();
		}
		else if(BasicType.get(item_stack) == BasicType.INDUCTION_CELL || BasicType.get(item_stack) == BasicType.INDUCTION_PROVIDER)
		{
			MekanismRenderer.renderCustomItem((RenderBlocks)data[0], item_stack);
		}
		else if(BasicType.get(item_stack) == BasicType.BIN)
		{
			GL11.glRotatef(270, 0F, 1F, 0F);
			MekanismRenderer.renderCustomItem((RenderBlocks)data[0], item_stack);
			GL11.glRotatef(-270, 0F, 1F, 0F);
			if(binRenderer == null || binRenderer.func_147498_b()/*getFontRenderer()*/ == null)
			{
				return;
			}

			InventoryBin inv = new InventoryBin(item_stack);
			ForgeDirection side = ForgeDirection.getOrientation(2);

			String amount = "";
			item_stack = inv.getStack();

			if(item_stack != null)
			{
				amount = Integer.toString(inv.getItemCount());
			}

			MekanismRenderer.glowOn();

			if(item_stack != null)
			{
				GL11.glPushMatrix();

				item = item_stack.getItem();
				if(!(item instanceof ItemBlock) || Block.getBlockFromItem(item).getRenderType() != 0)
				{
					GL11.glRotatef(180, 0, 0, 1);
					GL11.glTranslatef(-1.02F, -0.2F, 0);

					if(type == ItemRenderType.INVENTORY)
					{
						GL11.glTranslatef(-0.45F, -0.4F, 0F);
					}
				}

				if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.ENTITY)
				{
					GL11.glTranslatef(-0.22F, -0.2F, -0.22F);
				}

				GL11.glTranslated(0.73, 0.08, 0.44);
				GL11.glRotatef(90, 0, 1, 0);

				float scale = 0.03125F;
				float scaler = 0.9F;

				GL11.glScalef(scale*scaler, scale*scaler, 0);

				TextureManager renderEngine = mc.renderEngine;

				GL11.glDisable(GL11.GL_LIGHTING);

				renderItem.renderItemAndEffectIntoGUI(binRenderer.func_147498_b()/*getFontRenderer()*/, renderEngine, item_stack, 0, 0);

				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glPopMatrix();
			}
			MekanismRenderer.glowOff();

			if(amount != "")
			{
				float maxScale = 0.02F;

				GL11.glPushMatrix();

				GL11.glPolygonOffset(-10, -10);
				GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);

				float displayWidth = 1 - (2 / 16);
				float displayHeight = 1 - (2 / 16);
				GL11.glTranslatef(0, -0.31F, 0);

				if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.ENTITY)
				{
					GL11.glTranslated(-0.5, -0.4, -0.5);
				}

				GL11.glTranslatef(0, 0.9F, 1);
				GL11.glRotatef(90, 0, 1, 0);
				GL11.glRotatef(90, 1, 0, 0);

				GL11.glTranslatef(displayWidth / 2, 1F, displayHeight / 2);
				GL11.glRotatef(-90, 1, 0, 0);

				FontRenderer fontRenderer = binRenderer.func_147498_b();//getFontRenderer();

				int requiredWidth = Math.max(fontRenderer.getStringWidth(amount), 1);
				int lineHeight = fontRenderer.FONT_HEIGHT + 2;
				int requiredHeight = lineHeight * 1;
				float scaler = 0.4F;
				float scaleX = (displayWidth / requiredWidth);
				float scale = scaleX * scaler;

				if(maxScale > 0)
				{
					scale = Math.min(scale, maxScale);
				}

				GL11.glScalef(scale, -scale, scale);
				GL11.glDepthMask(false);

				int offsetX;
				int offsetY;
				int realHeight = (int)Math.floor(displayHeight / scale);
				int realWidth = (int)Math.floor(displayWidth / scale);

				offsetX = (realWidth - requiredWidth) / 2;
				offsetY = (realHeight - requiredHeight) / 2;

				GL11.glDisable(GL11.GL_LIGHTING);
				fontRenderer.drawString("\u00a7f" + amount, offsetX - (realWidth / 2), 1 + offsetY - (realHeight / 2), 1);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthMask(true);
				GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

				GL11.glPopMatrix();
			}
		}
		else if(Block.getBlockFromItem(item) == MekanismBlocks.GasTank)
		{
			GL11.glPushMatrix();
			BaseTier tier = ((GasTankItem)item).getBaseTier(item_stack);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, tier.getName() + "GasTank.png"));
			GL11.glRotatef(180F, 0F, 0F, 1F);
			GL11.glRotatef(90F, 0F, 1F, 0F);
			GL11.glTranslatef(0F, -1F, 0F);
			gasTank.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(Block.getBlockFromItem(item) == MekanismBlocks.ObsidianTNT)
		{
			GL11.glPushMatrix();
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "ObsidianTNT.png"));
			GL11.glRotatef(180F, 0F, 0F, 1F);
			GL11.glRotatef(180F, 0F, -1F, 0F);
			GL11.glTranslatef(0F, -1F, 0F);
			obsidianTNT.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(item instanceof ItemWalkieTalkie)
		{
			if(((ItemWalkieTalkie)item).getOn(item_stack))
			{
				MekanismRenderer.glowOn();
			}

			MekanismRenderer.renderItem(item_stack);

			if(((ItemWalkieTalkie)item).getOn(item_stack))
			{
				MekanismRenderer.glowOff();
			}
		}
		else if(MachineType.get(item_stack) == MachineType.PERSONAL_CHEST)
		{
			GL11.glPushMatrix();
			MachineItem chest = (MachineItem)item;

			GL11.glRotatef(90F, 0F, 1F, 0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			GL11.glTranslatef(0, 1F, 1F);
			GL11.glScalef(1F, -1F, -1F);

			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "PersonalChest.png"));

			personalChest.renderAll();
			GL11.glPopMatrix();
		}
		else if(item instanceof RobitItem)
		{
			GL11.glPushMatrix();
			GL11.glRotatef(180, 0F, 0F, 1F);
			GL11.glRotatef(90, 0F, -1F, 0F);
			GL11.glTranslatef(0F, -1.5F, 0F);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "Robit.png"));
			robit.render(0.08F);
			GL11.glPopMatrix();
		}
		else if(item == MekanismItems.Jetpack)
		{
			GL11.glPushMatrix();
			GL11.glRotatef(180, 0F, 0F, 1F);
			GL11.glRotatef(90, 0F, -1F, 0F);
			GL11.glTranslatef(0.2F, -0.35F, 0F);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "Jetpack.png"));
			jetpack.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(item == MekanismItems.ArmoredJetpack)
		{
			GL11.glPushMatrix();
			GL11.glRotatef(180, 0F, 0F, 1F);
			GL11.glRotatef(90, 0F, -1F, 0F);
			GL11.glTranslatef(0.2F, -0.35F, 0F);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "Jetpack.png"));
			armoredJetpack.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(item instanceof ItemGasMask)
		{
			GL11.glPushMatrix();
			GL11.glRotatef(180, 0F, 0F, 1F);
			GL11.glRotatef(90, 0F, -1F, 0F);
			GL11.glTranslatef(0.1F, 0.2F, 0F);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "ScubaSet.png"));
			gasMask.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(item instanceof ItemScubaTank)
		{
			GL11.glPushMatrix();
			GL11.glRotatef(180, 0F, 0F, 1F);
			GL11.glRotatef(90, 0F, -1F, 0F);
			GL11.glScalef(1.6F, 1.6F, 1.6F);
			GL11.glTranslatef(0.2F, -0.5F, 0F);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "ScubaSet.png"));
			scubaTank.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(item instanceof ItemFreeRunners)
		{
			GL11.glPushMatrix();
			GL11.glRotatef(180, 0F, 0F, 1F);
			GL11.glRotatef(90, 0F, -1F, 0F);
			GL11.glScalef(2F, 2F, 2F);
			GL11.glTranslatef(0.2F, -1.43F, 0.12F);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "FreeRunners.png"));
			freeRunners.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(item instanceof BalloonItem)
		{
			GL11.glPushMatrix();
			if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
			{
				GL11.glScalef(2.5F, 2.5F, 2.5F);
				GL11.glTranslatef(0.2F, 0, 0.1F);
				GL11.glRotatef(15, -1, 0, 1);
				balloonRenderer.render(((BalloonItem)item).getColor(item_stack), 0, 1.9F, 0);
			}
			else {
				balloonRenderer.render(((BalloonItem)item).getColor(item_stack), 0, 1, 0);
			}
			GL11.glPopMatrix();
		}
		else if(item instanceof ItemAtomicDisassembler)
		{
			GL11.glPushMatrix();
			GL11.glScalef(1.4F, 1.4F, 1.4F);
			GL11.glRotatef(180, 0F, 0F, 1F);

			if(type == ItemRenderType.EQUIPPED)
			{
				GL11.glRotatef(-45, 0F, 1F, 0F);
				GL11.glRotatef(50, 1F, 0F, 0F);
				GL11.glScalef(2F, 2F, 2F);
				GL11.glTranslatef(0F, -0.4F, 0.4F);
			}
			else if(type == ItemRenderType.INVENTORY)
			{
				GL11.glRotatef(225, 0F, 1F, 0F);
				GL11.glRotatef(45, -1F, 0F, -1F);
				GL11.glScalef(0.6F, 0.6F, 0.6F);
				GL11.glTranslatef(0F, -0.2F, 0F);
			}
			else {
				GL11.glRotatef(45, 0F, 1F, 0F);
				GL11.glTranslatef(0F, -0.7F, 0F);
			}

			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "AtomicDisassembler.png"));
			atomicDisassembler.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(item instanceof ItemPartTransmitter)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(-0.5, -0.5, -0.5);
			MekanismRenderer.blendOn();
			GL11.glDisable(GL11.GL_CULL_FACE);
			TransmitterPartRenderer.getInstance().renderItem(TransmitterType.values()[item_stack.getItemDamage()]);
			GL11.glEnable(GL11.GL_CULL_FACE);
			MekanismRenderer.blendOff();
			GL11.glPopMatrix();
		}
		else if(item instanceof ItemGlowPanel)
		{
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			GL11.glTranslated(-0.5, -0.5, -0.5);
			double d = 0.15;
			GL11.glTranslated(d, d, d);
			GL11.glScaled(2, 2, 2);
			GL11.glTranslated(0.4-2*d, -2*d, -2*d);
			GL11.glDisable(GL11.GL_CULL_FACE);
			RenderHelper.disableStandardItemLighting();
			RenderGlowPanel.getInstance().renderItem(item_stack.getItemDamage());
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		else if(item instanceof ItemFlamethrower)
		{
			GL11.glPushMatrix();
			GL11.glRotatef(160, 0F, 0F, 1F);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "Flamethrower.png"));
			GL11.glTranslatef(0F, -1F, 0F);
			GL11.glRotatef(135, 0F, 1F, 0F);
			GL11.glRotatef(-20, 0F, 0F, 1F);
			if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
			{
				if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
				{
					GL11.glRotatef(55, 0F, 1F, 0F);
				}
				else {
					GL11.glTranslatef(0F, 0.5F, 0F);
				}
				GL11.glScalef(2.5F, 2.5F, 2.5F);
				GL11.glTranslatef(0F, -1F, -0.5F);
			}
			else if(type == ItemRenderType.INVENTORY)
			{
				GL11.glTranslatef(-0.6F, 0F, 0F);
				GL11.glRotatef(45, 0F, 1F, 0F);
			}
			flamethrower.render(0.0625F);
			GL11.glPopMatrix();
		}
		else if(MachineType.get(item_stack) == MachineType.FLUID_TANK)
		{
			GL11.glPushMatrix();
			GL11.glRotatef(270F, 0F, -1F, 0F);
			mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "FluidTank.png"));
			MachineItem machine_item = (MachineItem)item;
			FluidStack fluid_stack = machine_item.getFluidStack(item_stack);
			float targetScale = (float)(fluid_stack != null ? fluid_stack.amount : 0) / machine_item.getCapacity(item_stack);
			FluidTankTier tier = FluidTankTier.values()[machine_item.getBaseTier(item_stack).ordinal()];
			Fluid fluid = fluid_stack != null ? fluid_stack.getFluid() : null;
			portableTankRenderer.render(tier, fluid, targetScale, false, null, -0.5, -0.5, -0.5);
			GL11.glPopMatrix();
		} else if(item instanceof MachineItem) {
			MachineType machine = MachineType.get(item_stack);
			if(machine == MachineType.BASIC_FACTORY || machine == MachineType.ADVANCED_FACTORY || machine == MachineType.ELITE_FACTORY) {
				GL11.glRotatef(-90F, 0F, 1F, 0F);
				MekanismRenderer.renderCustomItem(((RenderBlocks)data[0]), item_stack);
			} else {
				RenderingRegistry.instance().renderInventoryBlock((RenderBlocks)data[0], Block.getBlockFromItem(item), item_stack.getItemDamage(), ClientProxy.MACHINE_RENDER_ID);
			}
		} else if(item instanceof BasicBlockItem) {
			RenderingRegistry.instance().renderInventoryBlock((RenderBlocks)data[0], Block.getBlockFromItem(item), item_stack.getItemDamage(), ClientProxy.BASIC_RENDER_ID);
		}
	}
}
