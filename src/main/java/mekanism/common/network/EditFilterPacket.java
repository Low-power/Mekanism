package mekanism.common.network;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.content.miner.MinerFilter;
import mekanism.common.content.transporter.TransporterFilter;
import mekanism.common.network.EditFilterPacket.EditFilterMessage;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.DigitalMinerTileEntity;
import mekanism.common.tile.LogisticalSorterTileEntity;
import mekanism.common.tile.OredictionificatorTileEntity;
import mekanism.common.tile.OredictionificatorTileEntity.OredictionificatorFilter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;

public class EditFilterPacket implements IMessageHandler<EditFilterMessage, IMessage>
{
	@Override
	public IMessage onMessage(EditFilterMessage message, MessageContext context) {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.coord4D.dimensionId);
		if(world != null)
		{
			if(message.type == 0 && message.coord4D.getTileEntity(world) instanceof LogisticalSorterTileEntity)
			{
				LogisticalSorterTileEntity sorter = (LogisticalSorterTileEntity) message.coord4D.getTileEntity(world);

				if(!sorter.filters.contains(message.tFilter))
				{
					return null;
				}

				int index = sorter.filters.indexOf(message.tFilter);

				sorter.filters.remove(index);

				if(!message.delete)
				{
					sorter.filters.add(index, message.tEdited);
				}

				for(EntityPlayer player : sorter.playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(sorter), sorter.getFilterPacket(new ArrayList())), (EntityPlayerMP)player);
				}
			}
			else if(message.type == 1 && message.coord4D.getTileEntity(world) instanceof DigitalMinerTileEntity)
			{
				DigitalMinerTileEntity miner = (DigitalMinerTileEntity)message.coord4D.getTileEntity(world);

				if(!miner.filters.contains(message.mFilter))
				{
					return null;
				}

				int index = miner.filters.indexOf(message.mFilter);

				miner.filters.remove(index);

				if(!message.delete)
				{
					miner.filters.add(index, message.mEdited);
				}

				for(EntityPlayer player : miner.playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(miner), miner.getFilterPacket(new ArrayList())), (EntityPlayerMP)player);
				}
			}
			else if(message.type == 2 && message.coord4D.getTileEntity(world) instanceof OredictionificatorTileEntity)
			{
				OredictionificatorTileEntity oredictionificator = (OredictionificatorTileEntity)message.coord4D.getTileEntity(world);

				if(!oredictionificator.filters.contains(message.oFilter))
				{
					return null;
				}

				int index = oredictionificator.filters.indexOf(message.oFilter);

				oredictionificator.filters.remove(index);

				if(!message.delete)
				{
					oredictionificator.filters.add(index, message.oEdited);
				}

				for(EntityPlayer player : oredictionificator.playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(oredictionificator), oredictionificator.getFilterPacket(new ArrayList())), (EntityPlayerMP)player);
				}
			}
		}
		return null;
	}

	public static class EditFilterMessage implements IMessage
	{
		public Coord4D coord4D;

		public TransporterFilter tFilter;
		public TransporterFilter tEdited;

		public MinerFilter mFilter;
		public MinerFilter mEdited;

		public OredictionificatorFilter oFilter;
		public OredictionificatorFilter oEdited;

		public byte type = -1;

		public boolean delete;

		public EditFilterMessage() {}

		public EditFilterMessage(Coord4D coord, boolean deletion, Object filter, Object edited)
		{
			coord4D = coord;
			delete = deletion;

			if(filter instanceof TransporterFilter)
			{
				tFilter = (TransporterFilter)filter;

				if(!delete)
				{
					tEdited = (TransporterFilter)edited;
				}

				type = 0;
			}
			else if(filter instanceof MinerFilter)
			{
				mFilter = (MinerFilter)filter;

				if(!delete)
				{
					mEdited = (MinerFilter)edited;
				}

				type = 1;
			}
			else if(filter instanceof OredictionificatorFilter)
			{
				oFilter = (OredictionificatorFilter)filter;

				if(!delete)
				{
					oEdited = (OredictionificatorFilter)edited;
				}

				type = 2;
			}
		}

		@Override
		public void toBytes(ByteBuf dataStream)
		{
			dataStream.writeInt(coord4D.xCoord);
			dataStream.writeInt(coord4D.yCoord);
			dataStream.writeInt(coord4D.zCoord);
			dataStream.writeInt(coord4D.dimensionId);
			dataStream.writeByte(type);
			dataStream.writeBoolean(delete);
			ArrayList data = new ArrayList();
			if(type == 0)
			{
				tFilter.write(data);
				if(!delete)
				{
					tEdited.write(data);
				}
			}
			else if(type == 1)
			{
				mFilter.write(data);
				if(!delete)
				{
					mEdited.write(data);
				}
			}
			else if(type == 2)
			{
				oFilter.write(data);
				if(!delete)
				{
					oEdited.write(data);
				}
			}
			PacketHandler.encode(data.toArray(), dataStream);
		}

		@Override
		public void fromBytes(ByteBuf dataStream)
		{
			coord4D = new Coord4D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());

			type = dataStream.readByte();
			delete = dataStream.readBoolean();

			if(type == 0)
			{
				tFilter = TransporterFilter.readFromPacket(dataStream);

				if(!delete)
				{
					tEdited = TransporterFilter.readFromPacket(dataStream);
				}
			}
			else if(type == 1)
			{
				mFilter = MinerFilter.readFromPacket(dataStream);

				if(!delete)
				{
					mEdited = MinerFilter.readFromPacket(dataStream);
				}
			}
			else if(type == 2)
			{
				oFilter = OredictionificatorFilter.readFromPacket(dataStream);

				if(!delete)
				{
					oEdited = OredictionificatorFilter.readFromPacket(dataStream);
				}
			}
		}
	}
}
