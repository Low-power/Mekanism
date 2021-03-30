package mekanism.common.block;

import mekanism.api.gas.IGasItem;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlocks;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.base.ITierItem;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.BasicBlockTileEntity;
import mekanism.common.tile.TileEntityGasTank;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import java.util.Random;

public class GasTankBlock extends BlockContainer
{
	public GasTankBlock()
	{
		super(Material.iron);
		setBlockBounds(0.2F, 0F, 0.2F, 0.8F, 1F, 0.8F);
		setHardness(3.5F);
		setResistance(8F);
		setCreativeTab(Mekanism.tabMekanism);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon("mekanism:SteelCasing");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living_entity, ItemStack itemstack)
	{
		BasicBlockTileEntity tileEntity = (BasicBlockTileEntity)world.getTileEntity(x, y, z);

		int side = MathHelper.floor_double((double)(living_entity.rotationYaw * 4F / 360F) + 0.5D) & 3;
		int change = 3;

		switch(side)
		{
			case 0: change = 2; break;
			case 1: change = 5; break;
			case 2: change = 3; break;
			case 3: change = 4; break;
		}

		tileEntity.setFacing((short)change);
		tileEntity.redstone = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if(!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);

			if(tileEntity instanceof BasicBlockTileEntity)
			{
				((BasicBlockTileEntity)tileEntity).onNeighborChange(block);
			}
		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		return SecurityUtils.canAccess(player, tile) ? super.getPlayerRelativeBlockHardness(player, world, x, y, z) : 0F;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float playerX, float playerY, float playerZ)
	{
		if(world.isRemote)
		{
			return true;
		}

		TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);

		if(player.getCurrentEquippedItem() != null)
		{
			Item tool = player.getCurrentEquippedItem().getItem();

			if(MekanismUtils.hasUsableWrench(player, x, y, z))
			{
				if(SecurityUtils.canAccess(player, tileEntity))
				{
					if(player.isSneaking())
					{
						dismantleBlock(world, x, y, z, false);
						return true;
					}
					if(MekanismUtils.isBCWrench(tool))
					{
						((IToolWrench)tool).wrenchUsed(player, x, y, z);
					}
					int change = ForgeDirection.ROTATION_MATRIX[ForgeDirection.UP.ordinal()][tileEntity.facing];
					tileEntity.setFacing((short)change);
					world.notifyBlocksOfNeighborChange(x, y, z, this);
				}
				else {
					SecurityUtils.displayNoAccess(player);
				}
				return true;
			}
		}

		if(tileEntity != null)
		{
			if(!player.isSneaking())
			{
				if(SecurityUtils.canAccess(player, tileEntity))
				{
					player.openGui(Mekanism.instance, 10, world, x, y, z);
				}
				else {
					SecurityUtils.displayNoAccess(player);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		if(!player.capabilities.isCreativeMode && !world.isRemote && willHarvest)
		{
			float motion = 0.7F;
			double motionX = (world.rand.nextFloat() * motion) + (1F - motion) * 0.5D;
			double motionY = (world.rand.nextFloat() * motion) + (1F - motion) * 0.5D;
			double motionZ = (world.rand.nextFloat() * motion) + (1F - motion) * 0.5D;
			EntityItem item_entity = new EntityItem(world, x + motionX, y + motionY, z + motionZ, getPickBlock(null, world, x, y, z, player));
			world.spawnEntityInWorld(item_entity);
		}

		return world.setBlockToAir(x, y, z);
	}

	public ItemStack dismantleBlock(World world, int x, int y, int z, boolean returnBlock)
	{
		ItemStack itemStack = getPickBlock(null, world, x, y, z, null);

		world.setBlockToAir(x, y, z);

		if(!returnBlock)
		{
			float motion = 0.7F;
			double motionX = (world.rand.nextFloat() * motion) + (1F - motion) * 0.5D;
			double motionY = (world.rand.nextFloat() * motion) + (1F - motion) * 0.5D;
			double motionZ = (world.rand.nextFloat() * motion) + (1F - motion) * 0.5D;
			EntityItem item_entity = new EntityItem(world, x + motionX, y + motionY, z + motionZ, itemStack);
			world.spawnEntityInWorld(item_entity);
		}

		return itemStack;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public Item getItemDropped(int i, Random random, int j)
	{
		return null;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityGasTank();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
		ItemStack itemStack = new ItemStack(MekanismBlocks.GasTank);
		if(itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}
		if(tileEntity instanceof ISecurityTile)
		{
			ISecurityItem securityItem = (ISecurityItem)itemStack.getItem();
			if(securityItem.hasSecurity(itemStack))
			{
				securityItem.setOwner(itemStack, ((ISecurityTile)tileEntity).getSecurity().getOwner());
				securityItem.setSecurity(itemStack, ((ISecurityTile)tileEntity).getSecurity().getMode());
			}
		}
		ITierItem tierItem = (ITierItem)itemStack.getItem();
		tierItem.setBaseTier(itemStack, tileEntity.tier.getBaseTier());

		IGasItem storageTank = (IGasItem)itemStack.getItem();
		storageTank.setGas(itemStack, tileEntity.gasTank.getGas());

		ISustainedInventory inventory = (ISustainedInventory)itemStack.getItem();
		inventory.setInventory(((ISustainedInventory)tileEntity).getInventory(), itemStack);

		return itemStack;
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int par5)
	{
		TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
		return tileEntity.getRedstoneLevel();
	}

	@Override
	public ForgeDirection[] getValidRotations(World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		ForgeDirection[] valid = new ForgeDirection[6];
		if(tile instanceof BasicBlockTileEntity)
		{
			BasicBlockTileEntity basicTile = (BasicBlockTileEntity)tile;
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			{
				if(basicTile.canSetFacing(dir.ordinal()))
				{
					valid[dir.ordinal()] = dir;
				}
			}
		}
		return valid;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof BasicBlockTileEntity)
		{
			BasicBlockTileEntity basicTile = (BasicBlockTileEntity)tile;
			if(basicTile.canSetFacing(axis.ordinal()))
			{
				basicTile.setFacing((short)axis.ordinal());
				return true;
			}
		}
		return false;
	}
}
