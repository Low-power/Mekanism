package mekanism.common.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import mekanism.common.PacketHandler;
import mekanism.common.tile.BasicBlockTileEntity;

public class TileEntityInternalMultiblock extends BasicBlockTileEntity
{
	public String multiblockUUID;

	@Override
	public void onUpdate() {}
	
	@Override
	public boolean canUpdate()
	{
		return false;
	}
	
	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);
		
		if(worldObj.isRemote)
		{
			if(dataStream.readBoolean())
			{
				multiblockUUID = PacketHandler.readString(dataStream);
			}
			else {
				multiblockUUID = null;
			}
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		if(multiblockUUID != null)
		{
			data.add(true);
			data.add(multiblockUUID);
		}
		else {
			data.add(false);
		}
		
		return data;
	}
	
	public void setMultiblock(String id)
	{
		multiblockUUID = id;
	}
}
