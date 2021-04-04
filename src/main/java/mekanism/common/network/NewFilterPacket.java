package mekanism.common.network;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.content.miner.MinerFilter;
import mekanism.common.content.transporter.TransporterFilter;
import mekanism.common.network.NewFilterPacket.NewFilterMessage;
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

public class NewFilterPacket implements IMessageHandler<NewFilterMessage, IMessage>
{
	@Override
	public IMessage onMessage(NewFilterMessage message, MessageContext context) {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.coord4D.dimensionId);
		if(world != null)
		{
			if(message.type == 0 && message.coord4D.getTileEntity(world) instanceof LogisticalSorterTileEntity)
			{
				LogisticalSorterTileEntity sorter = (LogisticalSorterTileEntity)message.coord4D.getTileEntity(world);

				sorter.filters.add(message.tFilter);

				for(EntityPlayer player : sorter.playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(sorter), sorter.getFilterPacket(new ArrayList())), (EntityPlayerMP)player);
				}
			}
			else if(message.type == 1 && message.coord4D.getTileEntity(world) instanceof DigitalMinerTileEntity)
			{
				DigitalMinerTileEntity miner = (DigitalMinerTileEntity)message.coord4D.getTileEntity(world);

				miner.filters.add(message.mFilter);

				for(EntityPlayer player : miner.playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(miner), miner.getFilterPacket(new ArrayList())), (EntityPlayerMP)player);
				}
			}
			else if(message.type == 2 && message.coord4D.getTileEntity(world) instanceof OredictionificatorTileEntity)
			{
				OredictionificatorTileEntity oredictionificator = (OredictionificatorTileEntity)message.coord4D.getTileEntity(world);
				oredictionificator.filters.add(message.oFilter);
				for(EntityPlayer player : oredictionificator.playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(oredictionificator), oredictionificator.getFilterPacket(new ArrayList())), (EntityPlayerMP)player);
				}
			}
		}
		return null;
	}

	public static class NewFilterMessage implements IMessage
	{
		public Coord4D coord4D;

		public TransporterFilter tFilter;

		public MinerFilter mFilter;

		public OredictionificatorFilter oFilter;

		public byte type = -1;

		public NewFilterMessage() {}

		public NewFilterMessage(Coord4D coord, Object filter)
		{
			coord4D = coord;

			if(filter instanceof TransporterFilter)
			{
				tFilter = (TransporterFilter)filter;
				type = 0;
			}
			else if(filter instanceof MinerFilter)
			{
				mFilter = (MinerFilter)filter;
				type = 1;
			}
			else if(filter instanceof OredictionificatorFilter)
			{
				oFilter = (OredictionificatorFilter)filter;
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
			ArrayList data = new ArrayList();
			if(type == 0)
			{
				tFilter.write(data);
			}
			else if(type == 1)
			{
				mFilter.write(data);
			}
			else if(type == 2)
			{
				oFilter.write(data);
			}
			PacketHandler.encode(data.toArray(), dataStream);
		}

		@Override
		public void fromBytes(ByteBuf dataStream)
		{
			coord4D = new Coord4D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
			type = dataStream.readByte();

			if(type == 0)
			{
				tFilter = TransporterFilter.readFromPacket(dataStream);
			}
			else if(type == 1)
			{
				mFilter = MinerFilter.readFromPacket(dataStream);
			}
			else if(type == 2)
			{
				oFilter = OredictionificatorFilter.readFromPacket(dataStream);
			}
		}
	}
}
