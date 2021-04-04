package mekanism.common.tile;

import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.usage;
import mekanism.api.Range4D;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.ITubeConnection;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISustainedData;
import mekanism.common.base.ITankManager;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.block.Machine.MachineType;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.FluidContainerUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.PipeUtils;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;

public class RotaryCondensentratorTileEntity extends TileEntityElectricBlock implements IActiveState, ISustainedData, IFluidHandler, IGasHandler, ITubeConnection, IRedstoneControl, IUpgradeTile, IUpgradeInfoHandler, ITankManager
{
	public GasTank gasTank = new GasTank(MAX_FLUID);

	public FluidTank fluidTank = new FluidTank(MAX_FLUID);

	public static final int MAX_FLUID = 10000;

	/** 0: gas -> fluid; 1: fluid -> gas */
	public int mode;

	public int gasOutput = 256;

	public int updateDelay;

	public boolean isActive;

	public boolean clientActive;

	public double prevEnergy;

	public final double BASE_ENERGY_USAGE = usage.rotaryCondensentratorUsage;

	public double energyPerTick = BASE_ENERGY_USAGE;

	public double clientEnergyUsed;

	public TileComponentUpgrade upgradeComponent = new TileComponentUpgrade(this, 5);

	/** This machine's current RedstoneControl type. */
	public RedstoneControl controlType = RedstoneControl.DISABLED;

	public RotaryCondensentratorTileEntity()
	{
		super("RotaryCondensentrator", MachineType.ROTARY_CONDENSENTRATOR.baseEnergy);
		inventory = new ItemStack[6];
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(worldObj.isRemote)
		{
			if(updateDelay > 0)
			{
				updateDelay--;

				if(updateDelay == 0 && clientActive != isActive)
				{
					isActive = clientActive;
					MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
				}
			}
		}

		if(!worldObj.isRemote)
		{
			if(updateDelay > 0)
			{
				updateDelay--;

				if(updateDelay == 0 && clientActive != isActive)
				{
					Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
				}
			}

			ChargeUtils.discharge(4, this);

			GasStack gas_stack = gasTank.getGas();

			if(mode == 0)
			{
				if(inventory[1] != null && (gas_stack == null || gasTank.getStored() < gasTank.getMaxGas()))
				{
					gasTank.receive(GasTransmission.removeGas(inventory[1], gasTank.getGasType(), gasTank.getNeeded()), true);
				}

				if(inventory[2] != null)
				{
					if(inventory[2].getItem() instanceof IFluidContainerItem)
					{
						FluidContainerUtils.handleContainerItemFill(this, fluidTank, 2, 3);
					}
					else if(FluidContainerRegistry.isEmptyContainer(inventory[2]))
					{
						FluidContainerUtils.handleRegistryItemFill(this, fluidTank, 2, 3);
					}
				}

				FluidStack fluid_stack;
				if(getEnergy() >= energyPerTick && MekanismUtils.canFunction(this) && isValidGas(gas_stack) && ((fluid_stack = fluidTank.getFluid()) == null || (fluid_stack.amount < MAX_FLUID && gasEquals(gas_stack, fluid_stack))))
				{
					int operations = getUpgradedUsage();
					double prev = getEnergy();

					setActive(true);
					fluidTank.fill(new FluidStack(gas_stack.getGas().getFluid(), operations), true);
					gasTank.draw(operations, true);
					setEnergy(getEnergy() - energyPerTick*operations);
					clientEnergyUsed = prev-getEnergy();
				}
				else {
					if(prevEnergy >= getEnergy())
					{
						setActive(false);
					}
				}
			}
			else if(mode == 1)
			{
				if(inventory[0] != null && gas_stack != null)
				{
					gasTank.draw(GasTransmission.addGas(inventory[0], gas_stack), true);
				}

				if(gas_stack != null)
				{
					GasStack toSend = new GasStack(gas_stack.getGas(), Math.min(gas_stack.amount, gasOutput));

					TileEntity tileEntity = Coord4D.get(this).getFromSide(MekanismUtils.getLeft(facing)).getTileEntity(worldObj);

					if(tileEntity instanceof IGasHandler)
					{
						if(((IGasHandler)tileEntity).canReceiveGas(MekanismUtils.getLeft(facing).getOpposite(), gas_stack.getGas()))
						{
							gasTank.draw(((IGasHandler)tileEntity).receiveGas(MekanismUtils.getLeft(facing).getOpposite(), toSend, true), true);
						}
					}
				}

				if(inventory[2] != null)
				{
					if(inventory[2].getItem() instanceof IFluidContainerItem)
					{
						FluidContainerUtils.handleContainerItemEmpty(this, fluidTank, 2, 3);
					}
					else if(FluidContainerRegistry.isFilledContainer(inventory[2]))
					{
						FluidContainerUtils.handleRegistryItemEmpty(this, fluidTank, 2, 3);
					}
				}

				FluidStack fluid_stack;
				if(getEnergy() >= energyPerTick && MekanismUtils.canFunction(this) && isValidFluid(fluid_stack = fluidTank.getFluid()) && (gas_stack == null || (gasTank.getStored() < MAX_FLUID && gasEquals(gas_stack, fluid_stack))))
				{
					int operations = getUpgradedUsage();
					double prev = getEnergy();

					setActive(true);
					gasTank.receive(new GasStack(GasRegistry.getGas(fluid_stack.getFluid()), operations), true);
					fluidTank.drain(operations, true);
					setEnergy(getEnergy() - energyPerTick*operations);
					clientEnergyUsed = prev-getEnergy();
				}
				else {
					if(prevEnergy >= getEnergy())
					{
						setActive(false);
					}
				}
			}

			prevEnergy = getEnergy();
		}
	}

	public int getUpgradedUsage()
	{
		int possibleProcess = (int)Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED));

		if(mode == 0) //Gas to fluid
		{
			possibleProcess = Math.min(Math.min(gasTank.getStored(), fluidTank.getCapacity()-fluidTank.getFluidAmount()), possibleProcess);
		}
		else { //Fluid to gas
			possibleProcess = Math.min(Math.min(fluidTank.getFluidAmount(), gasTank.getNeeded()), possibleProcess);
		}

		possibleProcess = Math.min((int)(getEnergy()/energyPerTick), possibleProcess);

		return possibleProcess;
	}

	public boolean isValidGas(GasStack g)
	{
		if(g == null)
		{
			return false;
		}

		return g.getGas().hasFluid();
	}

	public boolean gasEquals(GasStack gas, FluidStack fluid)
	{
		if(fluid == null || gas == null || !gas.getGas().hasFluid())
		{
			return false;
		}

		return gas.getGas().getFluid() == fluid.getFluid();
	}

	public boolean isValidFluid(FluidStack f)
	{
		if(f == null)
		{
			return false;
		}

		return GasRegistry.getGas(f.getFluid()) != null;
	}

	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		if(!worldObj.isRemote)
		{
			int type = dataStream.readInt();

			if(type == 0)
			{
				mode = mode == 0 ? 1 : 0;
			}

			for(EntityPlayer player : playersUsing)
			{
				Mekanism.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), (EntityPlayerMP)player);
			}

			return;
		}

		super.handlePacketData(dataStream);

		if(worldObj.isRemote)
		{
			mode = dataStream.readInt();
			isActive = dataStream.readBoolean();
			controlType = RedstoneControl.values()[dataStream.readInt()];
			clientEnergyUsed = dataStream.readDouble();

			if(dataStream.readBoolean())
			{
				fluidTank.setFluid(new FluidStack(dataStream.readInt(), dataStream.readInt()));
			}
			else {
				fluidTank.setFluid(null);
			}

			if(dataStream.readBoolean())
			{
				gasTank.setGas(new GasStack(GasRegistry.getGas(dataStream.readInt()), dataStream.readInt()));
			}
			else {
				gasTank.setGas(null);
			}

			MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);

		data.add(Integer.valueOf(mode));
		data.add(Boolean.valueOf(isActive));
		data.add(Integer.valueOf(controlType.ordinal()));
		data.add(Double.valueOf(clientEnergyUsed));

		FluidStack fluid_stack = fluidTank.getFluid();
		if(fluid_stack != null)
		{
			data.add(Boolean.valueOf(true));
			data.add(Integer.valueOf(fluid_stack.getFluidID()));
			data.add(Integer.valueOf(fluid_stack.amount));
		}
		else {
			data.add(Boolean.valueOf(false));
		}

		GasStack gas_stack = gasTank.getGas();
		if(gas_stack != null)
		{
			data.add(Boolean.valueOf(true));
			data.add(Integer.valueOf(gas_stack.getGas().getID()));
			data.add(Integer.valueOf(gasTank.getStored()));
		}
		else {
			data.add(Boolean.valueOf(false));
		}

		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		mode = nbtTags.getInteger("mode");
		isActive = nbtTags.getBoolean("isActive");
		controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];

		gasTank.read(nbtTags.getCompoundTag("gasTank"));

		if(nbtTags.hasKey("fluidTank"))
		{
			fluidTank.readFromNBT(nbtTags.getCompoundTag("fluidTank"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		nbtTags.setInteger("mode", mode);
		nbtTags.setBoolean("isActive", isActive);
		nbtTags.setInteger("controlType", controlType.ordinal());
		nbtTags.setTag("gasTank", gasTank.write(new NBTTagCompound()));

		if(fluidTank.getFluid() != null)
		{
			nbtTags.setTag("fluidTank", fluidTank.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public boolean canSetFacing(int i)
	{
		return i != 0 && i != 1;
	}

	public int getScaledFluidLevel(int i)
	{
		FluidStack fluid_stack = fluidTank.getFluid();
		return fluid_stack != null ? fluid_stack.amount * i / MAX_FLUID : 0;
	}

	public int getScaledGasLevel(int i)
	{
		return gasTank.getGas() != null ? gasTank.getStored()*i / MAX_FLUID : 0;
	}

	@Override
	public void setActive(boolean active)
	{
		isActive = active;

		if(clientActive != active && updateDelay == 0)
		{
			Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));

			updateDelay = 10;
			clientActive = active;
		}
	}

	@Override
	public boolean getActive()
	{
		return isActive;
	}

	@Override
	public boolean renderUpdate()
	{
		return false;
	}

	@Override
	public boolean lightUpdate()
	{
		return true;
	}

	@Override
	public boolean canTubeConnect(ForgeDirection side)
	{
		return side == MekanismUtils.getLeft(facing);
	}

	@Override
	public int receiveGas(ForgeDirection side, GasStack stack, boolean doTransfer)
	{
		return gasTank.receive(stack, doTransfer);
	}

	@Override
	public int receiveGas(ForgeDirection side, GasStack stack)
	{
		return receiveGas(side, stack, true);
	}

	@Override
	public GasStack drawGas(ForgeDirection side, int amount, boolean doTransfer)
	{
		return gasTank.draw(amount, doTransfer);
	}

	@Override
	public GasStack drawGas(ForgeDirection side, int amount)
	{
		return drawGas(side, amount, true);
	}

	@Override
	public boolean canDrawGas(ForgeDirection side, Gas type)
	{
		return mode == 1 && side == MekanismUtils.getLeft(facing) ? gasTank.canDraw(type) : false;
	}

	@Override
	public boolean canReceiveGas(ForgeDirection side, Gas type)
	{
		return mode == 0 && side == MekanismUtils.getLeft(facing) ? gasTank.canReceive(type) : false;
	}

	@Override
	public void writeSustainedData(ItemStack itemStack)
	{
		FluidStack fluid_stack = fluidTank.getFluid();
		if(fluid_stack != null)
		{
			itemStack.stackTagCompound.setTag("fluidTank", fluid_stack.writeToNBT(new NBTTagCompound()));
		}
		GasStack gas_stack = gasTank.getGas();
		if(gas_stack != null)
		{
			itemStack.stackTagCompound.setTag("gasTank", gas_stack.write(new NBTTagCompound()));
		}
	}

	@Override
	public void readSustainedData(ItemStack itemStack)
	{
		fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(itemStack.stackTagCompound.getCompoundTag("fluidTank")));
		gasTank.setGas(GasStack.readFromNBT(itemStack.stackTagCompound.getCompoundTag("gasTank")));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(canFill(from, resource.getFluid()))
		{
			return fluidTank.fill(resource, doFill);
		}

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		FluidStack fluid_stack = fluidTank.getFluid();
		if(fluid_stack != null && fluid_stack.getFluid() == resource.getFluid())
		{
			return drain(from, resource.amount, doDrain);
		}

		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		FluidStack fluid_stack;
		return mode == 1 && from == MekanismUtils.getRight(facing) && ((fluid_stack = fluidTank.getFluid()) == null ? isValidFluid(new FluidStack(fluid, 1)) : fluid_stack.getFluid() == fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return mode == 0 && from == MekanismUtils.getRight(facing);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		if(from == MekanismUtils.getRight(facing))
		{
			return new FluidTankInfo[] {fluidTank.getInfo()};
		}

		return PipeUtils.EMPTY;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if(canDrain(from, null))
		{
			return fluidTank.drain(maxDrain, doDrain);
		}

		return null;
	}

	@Override
	public RedstoneControl getControlType()
	{
		return controlType;
	}

	@Override
	public void setControlType(RedstoneControl type)
	{
		controlType = type;
		MekanismUtils.saveChunk(this);
	}

	@Override
	public boolean canPulse()
	{
		return false;
	}

	@Override
	public TileComponentUpgrade getComponent() {
		return upgradeComponent;
	}

	@Override
	public void recalculateUpgradables(Upgrade upgrade)
	{
		super.recalculateUpgradables(upgrade);

		switch(upgrade)
		{
			case ENERGY:
				maxEnergy = MekanismUtils.getMaxEnergy(this, BASE_MAX_ENERGY);
				energyPerTick = MekanismUtils.getBaseEnergyPerTick(this, BASE_ENERGY_USAGE);
			default:
				break;
		}
	}

	@Override
	public List<String> getInfo(Upgrade upgrade) {
		return upgrade == Upgrade.SPEED ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
	}

	@Override
	public Object[] getTanks() {
		return new Object[] {gasTank, fluidTank};
	}
}
