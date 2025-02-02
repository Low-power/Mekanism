package mekanism.generators.common.item;

import mekanism.api.EnumColor;
import mekanism.api.MekanismConfig.general;
import mekanism.api.energy.IEnergizedItem;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.base.ISustainedData;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.base.ISustainedTank;
import mekanism.common.integration.IC2ItemManager;
import mekanism.common.tile.BasicBlockTileEntity;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.block.Generator.GeneratorType;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import java.util.List;

/**
 * Item class for handling multiple generator block IDs.
 * 0: Heat Generator
 * 1: Solar Generator
 * 3: Hydrogen Generator
 * 4: Bio-Generator
 * 5: Advanced Solar Generator
 * 6: Wind Generator
 * 7: Turbine Rotor
 * 8: Rotational Complex
 * 9: Electromagnetic Coil
 * 10: Turbine Casing
 * 11: Turbine Valve
 * 12: Turbine Vent
 * 13: Saturating Condenser
 * @author AidanBrady
 *
 */

@InterfaceList({
	@Interface(iface = "ic2.api.item.ISpecialElectricItem", modid = "IC2")
})
public class GeneratorItem extends ItemBlock implements IEnergizedItem, ISpecialElectricItem, ISustainedInventory, ISustainedTank, IEnergyContainerItem
{
	public Block metaBlock;

	public GeneratorItem(Block block)
	{
		super(block);
		metaBlock = block;
		setHasSubtypes(true);
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		GeneratorType type = GeneratorType.getFromMetadata(stack.getItemDamage());
		return (type == null || type.maxEnergy == -1) ? 64 : 1;
	}

	@Override
	public int getMetadata(int i)
	{
		return i;
	}

	@Override
	public IIcon getIconFromDamage(int i)
	{
		return metaBlock.getIcon(2, i);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		if(GeneratorType.getFromMetadata(itemstack.getItemDamage()) == null)
		{
			return "KillMe!";
		}

		return getUnlocalizedName() + "." + GeneratorType.getFromMetadata(itemstack.getItemDamage()).name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
	{
		GeneratorType type = GeneratorType.getFromMetadata(itemstack.getItemDamage());
		if(type.maxEnergy > -1)
		{
			if(!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey))
			{
				list.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.INDIGO + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) + EnumColor.GREY + " " + LangUtils.localize("tooltip.forDetails") + ".");
				list.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.AQUA + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) + EnumColor.GREY + " " + LangUtils.localize("tooltip.and") + " " + EnumColor.AQUA + GameSettings.getKeyDisplayString(MekanismKeyHandler.modeSwitchKey.getKeyCode()) + EnumColor.GREY + " " + LangUtils.localize("tooltip.forDesc") + ".");
			}
			else if(!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.modeSwitchKey))
			{
				list.add(EnumColor.BRIGHT_GREEN + LangUtils.localize("tooltip.storedEnergy") + ": " + EnumColor.GREY + MekanismUtils.getEnergyDisplay(getEnergy(itemstack)));

				if(hasTank(itemstack))
				{
					if(getFluidStack(itemstack) != null)
					{
						list.add(EnumColor.PINK + FluidRegistry.getFluidName(getFluidStack(itemstack)) + ": " + EnumColor.GREY + getFluidStack(itemstack).amount + "mB");
					}
				}

				list.add(EnumColor.AQUA + LangUtils.localize("tooltip.inventory") + ": " + EnumColor.GREY + LangUtils.transYesNo(getInventory(itemstack) != null && getInventory(itemstack).tagCount() != 0));
			}
			else {
				list.addAll(MekanismUtils.splitTooltip(type.getDescription(), itemstack));
			}
		}
		else {
			if(!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey))
			{
				list.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.INDIGO + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) + EnumColor.GREY + " " + LangUtils.localize("tooltip.forDetails") + ".");
			}
			else {
				list.addAll(MekanismUtils.splitTooltip(type.getDescription(), itemstack));
			}
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean place = true;
		Block block = world.getBlock(x, y, z);

		if(stack.getItemDamage() == GeneratorType.ADVANCED_SOLAR_GENERATOR.meta)
		{
			if(!(block.isReplaceable(world, x, y, z) && world.isAirBlock(x, y+1, z)))
			{
				return false;
			}

			outer:
			for(int xPos = -1; xPos <= 1; xPos++)
			{
				for(int zPos =- 1; zPos <= 1; zPos++)
				{
					if(!world.isAirBlock(x+xPos, y+2, z+zPos) || y+2 > 255)
					{
						place = false;
						break outer;
					}
				}
			}
		}
		else if(stack.getItemDamage() == GeneratorType.WIND_GENERATOR.meta)
		{
			if(!block.isReplaceable(world, x, y, z))
			{
				return false;
			}

			outer:
			for(int yPos = y+1; yPos <= y+4; yPos++)
			{
				if(!world.isAirBlock(x, yPos, z) || yPos > 255)
				{
					place = false;
					break outer;
				}
			}
		}

		if(place && super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
		{
			BasicBlockTileEntity tileEntity = (BasicBlockTileEntity)world.getTileEntity(x, y, z);
			if(tileEntity instanceof TileEntityElectricBlock)
			{
				((TileEntityElectricBlock)tileEntity).electricityStored = getEnergy(stack);
			}

			if(tileEntity instanceof ISustainedInventory)
			{
				((ISustainedInventory)tileEntity).setInventory(getInventory(stack));
			}
			if(tileEntity instanceof ISustainedData)
			{
				if(stack.stackTagCompound != null)
				{
					((ISustainedData)tileEntity).readSustainedData(stack);
				}
			}

			if(tileEntity instanceof ISustainedTank)
			{
				if(hasTank(stack) && getFluidStack(stack) != null)
				{
					((ISustainedTank)tileEntity).setFluidStack(getFluidStack(stack), stack);
				}
			}

			return true;
		}

		return false;
	}

	@Override
	@Method(modid = "IC2")
	public boolean canProvideEnergy(ItemStack itemStack)
	{
		return canSend(itemStack);
	}

	@Override
	@Method(modid = "IC2")
	public Item getChargedItem(ItemStack itemStack)
	{
		return this;
	}

	@Override
	@Method(modid = "IC2")
	public Item getEmptyItem(ItemStack itemStack)
	{
		return this;
	}

	@Override
	@Method(modid = "IC2")
	public double getMaxCharge(ItemStack itemStack)
	{
		return 0;
	}

	@Override
	@Method(modid = "IC2")
	public int getTier(ItemStack itemStack)
	{
		return 4;
	}

	@Override
	@Method(modid = "IC2")
	public double getTransferLimit(ItemStack itemStack)
	{
		return 0;
	}

	@Override
	public void setInventory(NBTTagList nbtTags, Object... data)
	{
		if(data[0] instanceof ItemStack)
		{
			ItemStack itemStack = (ItemStack)data[0];

			if(itemStack.stackTagCompound == null)
			{
				itemStack.setTagCompound(new NBTTagCompound());
			}

			itemStack.stackTagCompound.setTag("Items", nbtTags);
		}
	}

	@Override
	public NBTTagList getInventory(Object... data)
	{
		if(data[0] instanceof ItemStack)
		{
			ItemStack itemStack = (ItemStack)data[0];

			if(itemStack.stackTagCompound == null)
			{
				return null;
			}

			return itemStack.stackTagCompound.getTagList("Items", 10);
		}

		return null;
	}

	@Override
	public void setFluidStack(FluidStack fluidStack, Object... data)
	{
		if(fluidStack == null || fluidStack.amount == 0 || fluidStack.getFluidID() == 0)
		{
			return;
		}

		if(data[0] instanceof ItemStack)
		{
			ItemStack itemStack = (ItemStack)data[0];

			if(itemStack.stackTagCompound == null)
			{
				itemStack.setTagCompound(new NBTTagCompound());
			}

			itemStack.stackTagCompound.setTag("fluidTank", fluidStack.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public FluidStack getFluidStack(Object... data)
	{
		if(data[0] instanceof ItemStack)
		{
			ItemStack itemStack = (ItemStack)data[0];

			if(itemStack.stackTagCompound == null)
			{
				return null;
			}

			if(itemStack.stackTagCompound.hasKey("fluidTank"))
			{
				return FluidStack.loadFluidStackFromNBT(itemStack.stackTagCompound.getCompoundTag("fluidTank"));
			}
		}

		return null;
	}

	@Override
	public boolean hasTank(Object... data)
	{
		return data[0] instanceof ItemStack && ((ItemStack)data[0]).getItem() instanceof ISustainedTank && (((ItemStack)data[0]).getItemDamage() == 2);
	}

	@Override
	public double getEnergy(ItemStack itemStack)
	{
		if(itemStack.stackTagCompound == null)
		{
			return 0;
		}

		return itemStack.stackTagCompound.getDouble("electricity");
	}

	@Override
	public void setEnergy(ItemStack itemStack, double amount)
	{
		if(itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		double electricityStored = Math.max(Math.min(amount, getMaxEnergy(itemStack)), 0);
		itemStack.stackTagCompound.setDouble("electricity", electricityStored);
	}

	@Override
	public double getMaxEnergy(ItemStack itemStack)
	{
		return GeneratorType.getFromMetadata(itemStack.getItemDamage()).maxEnergy;
	}

	@Override
	public double getMaxTransfer(ItemStack itemStack)
	{
		return getMaxEnergy(itemStack)*0.005;
	}

	@Override
	public boolean canReceive(ItemStack itemStack)
	{
		return false;
	}

	@Override
	public boolean canSend(ItemStack itemStack)
	{
		return GeneratorType.getFromMetadata(itemStack.getItemDamage()).maxEnergy != -1;
	}

	@Override
	public int receiveEnergy(ItemStack theItem, int energy, boolean simulate)
	{
		if(canReceive(theItem))
		{
			double energyNeeded = getMaxEnergy(theItem)-getEnergy(theItem);
			double toReceive = Math.min(energy* general.FROM_TE, energyNeeded);

			if(!simulate)
			{
				setEnergy(theItem, getEnergy(theItem) + toReceive);
			}

			return (int)Math.round(toReceive* general.TO_TE);
		}

		return 0;
	}

	@Override
	public int extractEnergy(ItemStack theItem, int energy, boolean simulate)
	{
		if(canSend(theItem))
		{
			double energyRemaining = getEnergy(theItem);
			double toSend = Math.min((energy* general.FROM_TE), energyRemaining);

			if(!simulate)
			{
				setEnergy(theItem, getEnergy(theItem) - toSend);
			}

			return (int)Math.round(toSend* general.TO_TE);
		}

		return 0;
	}

	@Override
	public int getEnergyStored(ItemStack theItem)
	{
		return (int)(getEnergy(theItem)* general.TO_TE);
	}

	@Override
	public int getMaxEnergyStored(ItemStack theItem)
	{
		return (int)(getMaxEnergy(theItem)* general.TO_TE);
	}

	@Override
	@Method(modid = "IC2")
	public IElectricItemManager getManager(ItemStack itemStack)
	{
		return IC2ItemManager.getManager(this);
	}
}
