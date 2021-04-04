package mekanism.common.integration;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.common.tile.TileEntityAdvancedBoundingBlock;
import mekanism.common.tile.BinTileEntity;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.EnergyCubeTileEntity;
import mekanism.common.tile.FactoryTileEntity;
import mekanism.common.tile.FluidTankTileEntity;
import mekanism.common.tile.GasTankTileEntity;
import mekanism.common.tile.TileEntityInductionCell;
import mekanism.common.tile.TileEntityInductionProvider;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import java.util.List;

@Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class WailaDataProvider implements IWailaDataProvider
{
	@Method(modid = "Waila")
	public static void register(IWailaRegistrar registrar)
	{
		WailaDataProvider provider = new WailaDataProvider();

		registrar.registerHeadProvider(provider, TileEntityInductionCell.class);
		registrar.registerHeadProvider(provider, TileEntityInductionProvider.class);
		registrar.registerHeadProvider(provider, FactoryTileEntity.class);
		registrar.registerHeadProvider(provider, TileEntityBoundingBlock.class);
		registrar.registerHeadProvider(provider, TileEntityAdvancedBoundingBlock.class);
		registrar.registerHeadProvider(provider, FluidTankTileEntity.class);
		registrar.registerHeadProvider(provider, GasTankTileEntity.class);
		registrar.registerHeadProvider(provider, BinTileEntity.class);
		registrar.registerHeadProvider(provider, EnergyCubeTileEntity.class);
	}

	@Override
	@Method(modid = "Waila")
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	@Method(modid = "Waila")
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileEntity tile = accessor.getTileEntity();
		if(tile instanceof TileEntityInductionCell)
		{
			currenttip.set(0, EnumColor.WHITE + ((TileEntityInductionCell)tile).getInventoryName());
		}
		else if(tile instanceof TileEntityInductionProvider)
		{
			currenttip.set(0, EnumColor.WHITE + ((TileEntityInductionProvider)tile).getInventoryName());
		}
		else if(tile instanceof FactoryTileEntity)
		{
			currenttip.set(0, EnumColor.WHITE + ((FactoryTileEntity)tile).getInventoryName());
		}
		else if(tile instanceof FluidTankTileEntity)
		{
			currenttip.set(0, EnumColor.WHITE + ((FluidTankTileEntity)tile).getInventoryName());
		}
		else if(tile instanceof GasTankTileEntity)
		{
			currenttip.set(0, EnumColor.WHITE + ((GasTankTileEntity)tile).getInventoryName());
		}
		else if(tile instanceof BinTileEntity)
		{
			currenttip.set(0, EnumColor.WHITE + ((BinTileEntity)tile).getInventoryName());
		}
		else if(tile instanceof EnergyCubeTileEntity)
		{
			currenttip.set(0, EnumColor.WHITE + ((EnergyCubeTileEntity)tile).getInventoryName());
		}
		else if(tile instanceof TileEntityBoundingBlock)
		{
			TileEntityBoundingBlock bound = (TileEntityBoundingBlock)tile;
			Coord4D coord = new Coord4D(bound.mainX, bound.mainY, bound.mainZ, tile.getWorldObj().provider.dimensionId);
			if(bound.receivedCoords && coord.getTileEntity(tile.getWorldObj()) instanceof IInventory)
			{
				currenttip.set(0, EnumColor.WHITE + ((IInventory)coord.getTileEntity(tile.getWorldObj())).getInventoryName());
			}
		}
		return currenttip;
	}

	@Override
	@Method(modid = "Waila")
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	@Method(modid = "Waila")
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	@Method(modid = "Waila")
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
	{
		return tag;
	}
}
