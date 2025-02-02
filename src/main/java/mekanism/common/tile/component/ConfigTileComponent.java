package mekanism.common.tile.component;

import mekanism.api.EnumColor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
import mekanism.common.SideData.IOState;
import mekanism.common.base.ITileComponent;
import mekanism.common.tile.ContainerTileEntity;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigTileComponent implements ITileComponent
{
	public static SideData EMPTY = new SideData("Empty", EnumColor.BLACK, InventoryUtils.EMPTY);

	public ContainerTileEntity tileEntity;

	public Map<Integer, byte[]> sideConfigs = new HashMap<Integer, byte[]>();
	public Map<Integer, ArrayList<SideData>> sideOutputs = new HashMap<Integer, ArrayList<SideData>>();
	public Map<Integer, Boolean> ejecting = new HashMap<Integer, Boolean>();
	public Map<Integer, Boolean> canEject = new HashMap<Integer, Boolean>();

	public List<TransmissionType> transmissions = new ArrayList<TransmissionType>();

	public ConfigTileComponent(ContainerTileEntity tile, TransmissionType... types)
	{
		tileEntity = tile;
		for(TransmissionType type : types)
		{
			addSupported(type);
		}
		tile.components.add(this);
	}

	public void readFrom(ConfigTileComponent config)
	{
		sideConfigs = config.sideConfigs;
		ejecting = config.ejecting;
		canEject = config.canEject;
		transmissions = config.transmissions;
	}

	public void addSupported(TransmissionType type)
	{
		if(!transmissions.contains(type))
		{
			transmissions.add(type);
		}

		sideOutputs.put(type.ordinal(), new ArrayList<SideData>());
		ejecting.put(type.ordinal(), false);
		canEject.put(type.ordinal(), true);
	}

	public EnumSet<ForgeDirection> getSidesForData(TransmissionType type, int facing, int dataIndex)
	{
		EnumSet<ForgeDirection> ret = EnumSet.noneOf(ForgeDirection.class);
		for(byte b = 0; b < 6; b++)
		{
			int side = MekanismUtils.getBaseOrientation(b, facing);
			if(getConfig(type)[side] == dataIndex)
			{
				ret.add(ForgeDirection.getOrientation(b));
			}
		}
		return ret;
	}

	public void setCanEject(TransmissionType type, boolean eject)
	{
		canEject.put(type.ordinal(), eject);
	}

	public boolean canEject(TransmissionType type)
	{
		return canEject.get(type.ordinal());
	}

	public void fillConfig(TransmissionType type, int data)
	{
		byte sideData = (byte)data;
		setConfig(type, new byte[] {sideData, sideData, sideData, sideData, sideData, sideData});
	}

	public void setIOConfig(TransmissionType type)
	{
		addOutput(type, new SideData("None", EnumColor.GREY, IOState.OFF));
		addOutput(type, new SideData("Input", EnumColor.DARK_GREEN, IOState.INPUT));
		addOutput(type, new SideData("Output", EnumColor.DARK_RED, IOState.OUTPUT));
		setConfig(type, new byte[] {1, 1, 2, 1, 1, 1});
	}

	public void setInputConfig(TransmissionType type)
	{
		addOutput(type, new SideData("None", EnumColor.GREY, IOState.OFF));
		addOutput(type, new SideData("Input", EnumColor.DARK_GREEN, IOState.INPUT));
		fillConfig(type, 1);
		setCanEject(type, false);
	}

	public void setConfig(TransmissionType type, byte[] config)
	{
		sideConfigs.put(type.ordinal(), config);
	}

	public void addOutput(TransmissionType type, SideData data)
	{
		sideOutputs.get(type.ordinal()).add(data);
	}

	public ArrayList<SideData> getOutputs(TransmissionType type)
	{
		return sideOutputs.get(type.ordinal());
	}

	public byte[] getConfig(TransmissionType type)
	{
		return sideConfigs.get(type.ordinal());
	}

	public SideData getOutput(TransmissionType type, int side, int facing)
	{
		return getOutput(type, MekanismUtils.getBaseOrientation(side, facing));
	}

	public SideData getOutput(TransmissionType type, int side)
	{
		int index = getConfig(type)[side];
		if(index == -1)
		{
			return EMPTY;
		}
		else if(index > getOutputs(type).size()-1)
		{
			index = getConfig(type)[side] = 0;
		}
		return getOutputs(type).get(index);
	}

	public boolean supports(TransmissionType type)
	{
		return transmissions.contains(type);
	}

	@Override
	public void tick() {}

	@Override
	public void read(NBTTagCompound nbtTags) {
		if(nbtTags.getBoolean("sideDataStored"))
		{
			for(TransmissionType type : transmissions)
			{
				if(nbtTags.getByteArray("config" + type.ordinal()).length > 0)
				{
					sideConfigs.put(type.ordinal(), nbtTags.getByteArray("config" + type.ordinal()));
					ejecting.put(type.ordinal(), nbtTags.getBoolean("ejecting" + type.ordinal()));
				}
			}
		}
	}

	@Override
	public void read(ByteBuf dataStream) {
		transmissions.clear();
		int amount = dataStream.readInt();
		for(int i = 0; i < amount; i++)
		{
			transmissions.add(TransmissionType.values()[dataStream.readInt()]);
		}
		for(TransmissionType type : transmissions)
		{
			byte[] array = new byte[6];
			dataStream.readBytes(array);
			sideConfigs.put(type.ordinal(), array);
			ejecting.put(type.ordinal(), dataStream.readBoolean());
		}
	}

	@Override
	public void write(NBTTagCompound nbtTags) {
		for(TransmissionType type : transmissions)
		{
			nbtTags.setByteArray("config" + type.ordinal(), sideConfigs.get(type.ordinal()));
			nbtTags.setBoolean("ejecting" + type.ordinal(), ejecting.get(type.ordinal()));
		}
		nbtTags.setBoolean("sideDataStored", true);
	}

	@Override
	public void write(ArrayList data) {
		data.add(transmissions.size());
		for(TransmissionType type : transmissions)
		{
			data.add(type.ordinal());
		}
		for(TransmissionType type : transmissions)
		{
			data.add(sideConfigs.get(type.ordinal()));
			data.add(ejecting.get(type.ordinal()));
		}
	}

	@Override
	public void invalidate() {}

	public boolean isEjecting(TransmissionType type)
	{
		return ejecting.get(type.ordinal());
	}

	public void setEjecting(TransmissionType type, boolean eject)
	{
		ejecting.put(type.ordinal(), eject);
		MekanismUtils.saveChunk(tileEntity);
	}
}
