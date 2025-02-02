package mekanism.common.tile.component;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTransmission;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITankManager;
import mekanism.common.base.ITileComponent;
import mekanism.common.base.ITransporterTile;
import mekanism.common.content.transporter.TransporterManager;
import mekanism.common.tile.ContainerTileEntity;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.PipeUtils;
import mekanism.common.util.TransporterUtils;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EjectorTileComponent implements ITileComponent
{
	public ContainerTileEntity tileEntity;

	public boolean strictInput;

	public EnumColor outputColor;

	public EnumColor[] inputColors = new EnumColor[] {null, null, null, null, null, null};

	public int tickDelay = 0;

	public Map<TransmissionType, SideData> sideData = new HashMap<TransmissionType, SideData>();

	public Map<TransmissionType, int[]> trackers = new HashMap<TransmissionType, int[]>();

	public static final int GAS_OUTPUT = 256;
	public static final int FLUID_OUTPUT = 256;

	public EjectorTileComponent(ContainerTileEntity tile)
	{
		tileEntity = tile;

		tile.components.add(this);
	}

	public EjectorTileComponent setOutputData(TransmissionType type, SideData data)
	{
		sideData.put(type, data);
		trackers.put(type, new int[data.availableSlots.length]);
		return this;
	}

	public void readFrom(EjectorTileComponent ejector)
	{
		strictInput = ejector.strictInput;
		outputColor = ejector.outputColor;
		inputColors = ejector.inputColors;
		tickDelay = ejector.tickDelay;
		sideData = ejector.sideData;
	}

	private List<ForgeDirection> getTrackedOutputs(TransmissionType type, int index, List<ForgeDirection> dirs)
	{
		List<ForgeDirection> sides = new ArrayList<ForgeDirection>();

		for(int i = trackers.get(type)[index]+1; i <= trackers.get(type)[index]+6; i++)
		{
			for(ForgeDirection side : dirs)
			{
				if(ForgeDirection.getOrientation(i%6) == side)
				{
					sides.add(side);
				}
			}
		}

		return sides;
	}

	@Override
	public void tick()
	{
		if(tickDelay == 0)
		{
			if(sideData.get(TransmissionType.ITEM) != null)
			{
				outputItems();
			}
		}
		else {
			tickDelay--;
		}
		if(!tileEntity.getWorldObj().isRemote)
		{
			if(sideData.get(TransmissionType.GAS) != null && getEjecting(TransmissionType.GAS))
			{
				SideData data = sideData.get(TransmissionType.GAS);
				List<ForgeDirection> outputSides = getOutputSides(TransmissionType.GAS, data);
				if(((ITankManager)tileEntity).getTanks() != null)
				{
					GasTank tank = (GasTank)((ITankManager)tileEntity).getTanks()[data.availableSlots[0]];
					if(tank.getStored() > 0)
					{
						GasStack toEmit = tank.getGas().copy().withAmount(Math.min(GAS_OUTPUT, tank.getStored()));
						int emit = GasTransmission.emit(toEmit, tileEntity, outputSides);
						tank.draw(emit, true);
					}
				}
			}
			if(sideData.get(TransmissionType.FLUID) != null && getEjecting(TransmissionType.FLUID))
			{
				SideData data = sideData.get(TransmissionType.FLUID);
				List<ForgeDirection> outputSides = getOutputSides(TransmissionType.FLUID, data);
				if(((ITankManager)tileEntity).getTanks() != null)
				{
					FluidTank tank = (FluidTank)((ITankManager)tileEntity).getTanks()[data.availableSlots[0]];
					if(tank.getFluidAmount() > 0)
					{
						FluidStack toEmit = PipeUtils.copy(tank.getFluid(), Math.min(FLUID_OUTPUT, tank.getFluidAmount()));
						int emit = PipeUtils.emit(outputSides, toEmit, tileEntity);
						tank.drain(emit, true);
					}
				}
			}
		}
	}

	public List<ForgeDirection> getOutputSides(TransmissionType type, SideData data)
	{
		List<ForgeDirection> outputSides = new ArrayList<ForgeDirection>();
		ISideConfiguration configurable = (ISideConfiguration)tileEntity;

		for(int i = 0; i < configurable.getConfig().getConfig(type).length; i++)
		{
			int side = MekanismUtils.getBaseOrientation(i, tileEntity.facing);
			if(configurable.getConfig().getConfig(type)[side] == configurable.getConfig().getOutputs(type).indexOf(data))
			{
				outputSides.add(ForgeDirection.getOrientation(i));
			}
		}
		return outputSides;
	}

	public void outputItems()
	{
		if(!getEjecting(TransmissionType.ITEM) || tileEntity.getWorldObj().isRemote)
		{
			return;
		}

		SideData data = sideData.get(TransmissionType.ITEM);
		List<ForgeDirection> outputSides = getOutputSides(TransmissionType.ITEM, data);

		for(int index = 0; index < sideData.get(TransmissionType.ITEM).availableSlots.length; index++)
		{
			int slotID = sideData.get(TransmissionType.ITEM).availableSlots[index];

			if(tileEntity.getStackInSlot(slotID) == null)
			{
				continue;
			}

			ItemStack stack = tileEntity.getStackInSlot(slotID);
			List<ForgeDirection> outputs = getTrackedOutputs(TransmissionType.ITEM, index, outputSides);

			for(ForgeDirection side : outputs)
			{
				TileEntity tile = Coord4D.get(tileEntity).getFromSide(side).getTileEntity(tileEntity.getWorldObj());
				ItemStack prev = stack.copy();

				if(tile instanceof IInventory && !(tile instanceof ITransporterTile))
				{
					stack = InventoryUtils.putStackInInventory((IInventory)tile, stack, side.ordinal(), false);
				}
				else if(tile instanceof ITransporterTile)
				{
					ItemStack rejects = TransporterUtils.insert(tileEntity, ((ITransporterTile)tile).getTransmitter(), stack, outputColor, true, 0);

					if(TransporterManager.didEmit(stack, rejects))
					{
						stack = rejects;
					}
				}

				if(stack == null || prev.stackSize != stack.stackSize)
				{
					trackers.get(TransmissionType.ITEM)[index] = side.ordinal();
				}

				if(stack == null)
				{
					break;
				}
			}

			tileEntity.setInventorySlotContents(slotID, stack);
			tileEntity.markDirty();
		}

		tickDelay = 20;
	}

	public boolean hasStrictInput()
	{
		return strictInput;
	}

	public void setStrictInput(boolean strict)
	{
		strictInput = strict;
		MekanismUtils.saveChunk(tileEntity);
	}

	public void setOutputColor(EnumColor color)
	{
		outputColor = color;
		MekanismUtils.saveChunk(tileEntity);
	}

	public EnumColor getOutputColor()
	{
		return outputColor;
	}

	public void setInputColor(ForgeDirection side, EnumColor color)
	{
		inputColors[side.ordinal()] = color;
		MekanismUtils.saveChunk(tileEntity);
	}

	public EnumColor getInputColor(ForgeDirection side)
	{
		return inputColors[side.ordinal()];
	}

	@Override
	public void read(NBTTagCompound nbtTags)
	{
		strictInput = nbtTags.getBoolean("strictInput");

		if(nbtTags.hasKey("ejectColor"))
		{
			outputColor = TransporterUtils.colors.get(nbtTags.getInteger("ejectColor"));
		}

		for(TransmissionType type : sideData.keySet())
		{
			for(int i = 0; i < sideData.get(type).availableSlots.length; i++)
			{
				trackers.get(type)[i] = nbtTags.getInteger("tracker" + type.getTransmission() + i);
			}
		}

		for(int i = 0; i < 6; i++)
		{
			if(nbtTags.hasKey("inputColors" + i))
			{
				int inC = nbtTags.getInteger("inputColors" + i);

				if(inC != -1)
				{
					inputColors[i] = TransporterUtils.colors.get(inC);
				}
				else {
					inputColors[i] = null;
				}
			}
		}
	}

	@Override
	public void read(ByteBuf dataStream)
	{
		strictInput = dataStream.readBoolean();

		int c = dataStream.readInt();

		if(c != -1)
		{
			outputColor = TransporterUtils.colors.get(c);
		}
		else {
			outputColor = null;
		}

		for(int i = 0; i < 6; i++)
		{
			int inC = dataStream.readInt();

			if(inC != -1)
			{
				inputColors[i] = TransporterUtils.colors.get(inC);
			}
			else {
				inputColors[i] = null;
			}
		}
	}

	@Override
	public void write(NBTTagCompound nbtTags)
	{
		nbtTags.setBoolean("strictInput", strictInput);

		if(outputColor != null)
		{
			nbtTags.setInteger("ejectColor", TransporterUtils.colors.indexOf(outputColor));
		}

		for(TransmissionType type : sideData.keySet())
		{
			for(int i = 0; i < sideData.get(type).availableSlots.length; i++)
			{
				nbtTags.setInteger("tracker" + type.getTransmission() + i, trackers.get(type)[i]);
			}
		}

		for(int i = 0; i < 6; i++)
		{
			if(inputColors[i] == null)
			{
				nbtTags.setInteger("inputColors" + i, -1);
			}
			else {
				nbtTags.setInteger("inputColors" + i, TransporterUtils.colors.indexOf(inputColors[i]));
			}
		}
	}

	@Override
	public void write(ArrayList data)
	{
		data.add(strictInput);

		if(outputColor != null)
		{
			data.add(TransporterUtils.colors.indexOf(outputColor));
		}
		else {
			data.add(-1);
		}

		for(int i = 0; i < 6; i++)
		{
			if(inputColors[i] == null)
			{
				data.add(-1);
			}
			else {
				data.add(TransporterUtils.colors.indexOf(inputColors[i]));
			}
		}
	}

	@Override
	public void invalidate() {}

	private boolean getEjecting(TransmissionType type)
	{
		return ((ISideConfiguration)tileEntity).getConfig().isEjecting(type);
	}
}
