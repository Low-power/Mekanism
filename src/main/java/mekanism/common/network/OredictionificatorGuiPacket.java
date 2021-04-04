package mekanism.common.network;

import mekanism.api.Coord4D;
import mekanism.client.gui.OredictionificatorGui;
import mekanism.client.gui.OredictionificatorFilterGui;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.inventory.container.FilterContainer;
import mekanism.common.inventory.container.OredictionificatorContainer;
import mekanism.common.network.OredictionificatorGuiPacket.OredictionificatorGuiMessage;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.ContainerTileEntity;
import mekanism.common.tile.OredictionificatorTileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;

public class OredictionificatorGuiPacket implements IMessageHandler<OredictionificatorGuiMessage, IMessage>
{
	@Override
	public IMessage onMessage(OredictionificatorGuiMessage message, MessageContext context) {
		EntityPlayer player = PacketHandler.getPlayer(context);
		if(!player.worldObj.isRemote)
		{
			World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.coord4D.dimensionId);

			if(world != null && message.coord4D.getTileEntity(world) instanceof OredictionificatorTileEntity)
			{
				OredictionificatorGuiMessage.openServerGui(message.packetType, message.guiType, world, (EntityPlayerMP)player, message.coord4D, message.index);
			}
		}
		else {
			if(message.coord4D.getTileEntity(player.worldObj) instanceof OredictionificatorTileEntity)
			{
				try {
					if(message.packetType == OredictionificatorGuiPacketType.CLIENT)
					{
						FMLCommonHandler.instance().showGuiScreen(OredictionificatorGuiMessage.getGui(message.packetType, message.guiType, player, player.worldObj, message.coord4D.xCoord, message.coord4D.yCoord, message.coord4D.zCoord, -1));
					}
					else if(message.packetType == OredictionificatorGuiPacketType.CLIENT_INDEX)
					{
						FMLCommonHandler.instance().showGuiScreen(OredictionificatorGuiMessage.getGui(message.packetType, message.guiType, player, player.worldObj, message.coord4D.xCoord, message.coord4D.yCoord, message.coord4D.zCoord, message.index));
					}

					player.openContainer.windowId = message.windowId;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static class OredictionificatorGuiMessage implements IMessage
	{
		public Coord4D coord4D;

		public OredictionificatorGuiPacketType packetType;

		public int guiType;

		public int windowId = -1;

		public int index = -1;

		public OredictionificatorGuiMessage() {}

		public OredictionificatorGuiMessage(OredictionificatorGuiPacketType type, Coord4D coord, int guiID, int extra, int extra2)
		{
			packetType = type;
			coord4D = coord;
			guiType = guiID;
			if(packetType == OredictionificatorGuiPacketType.CLIENT)
			{
				windowId = extra;
			}
			else if(packetType == OredictionificatorGuiPacketType.SERVER_INDEX)
			{
				index = extra;
			}
			else if(packetType == OredictionificatorGuiPacketType.CLIENT_INDEX)
			{
				windowId = extra;
				index = extra2;
			}
		}

		public static void openServerGui(OredictionificatorGuiPacketType t, int guiType, World world, EntityPlayerMP player, Coord4D obj, int i)
		{
			Container container = null;
			player.closeContainer();
			if(guiType == 0)
			{
				container = new OredictionificatorContainer(player.inventory, (OredictionificatorTileEntity)obj.getTileEntity(world));
			}
			else if(guiType == 1)
			{
				container = new FilterContainer(player.inventory, (ContainerTileEntity)obj.getTileEntity(world));
			}
			player.getNextWindowId();
			int window = player.currentWindowId;
			if(t == OredictionificatorGuiPacketType.SERVER)
			{
				Mekanism.packetHandler.sendTo(new OredictionificatorGuiMessage(OredictionificatorGuiPacketType.CLIENT, obj, guiType, window, 0), player);
			}
			else if(t == OredictionificatorGuiPacketType.SERVER_INDEX)
			{
				Mekanism.packetHandler.sendTo(new OredictionificatorGuiMessage(OredictionificatorGuiPacketType.CLIENT_INDEX, obj, guiType, window, i), player);
			}
			player.openContainer = container;
			player.openContainer.windowId = window;
			player.openContainer.addCraftingToCrafters(player);
			if(guiType == 0)
			{
				OredictionificatorTileEntity tile = (OredictionificatorTileEntity)obj.getTileEntity(world);
				for(EntityPlayer p : tile.playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(obj, tile.getFilterPacket(new ArrayList())), (EntityPlayerMP)p);
				}
			}
		}

		@SideOnly(Side.CLIENT)
		public static GuiScreen getGui(OredictionificatorGuiPacketType packetType, int type, EntityPlayer player, World world, int x, int y, int z, int index)
		{
			if(type == 0)
			{
				return new OredictionificatorGui(player.inventory, (OredictionificatorTileEntity)world.getTileEntity(x, y, z));
			}
			else {
				if(packetType == OredictionificatorGuiPacketType.CLIENT)
				{
					if(type == 1)
					{
						return new OredictionificatorFilterGui(player, (OredictionificatorTileEntity)world.getTileEntity(x, y, z));
					}
				}
				else if(packetType == OredictionificatorGuiPacketType.CLIENT_INDEX)
				{
					if(type == 1)
					{
						return new OredictionificatorFilterGui(player, (OredictionificatorTileEntity)world.getTileEntity(x, y, z), index);
					}
				}
			}
			return null;
		}

		@Override
		public void toBytes(ByteBuf dataStream)
		{
			dataStream.writeInt(packetType.ordinal());
			dataStream.writeInt(coord4D.xCoord);
			dataStream.writeInt(coord4D.yCoord);
			dataStream.writeInt(coord4D.zCoord);
			dataStream.writeInt(coord4D.dimensionId);
			dataStream.writeInt(guiType);
			if(packetType == OredictionificatorGuiPacketType.CLIENT || packetType == OredictionificatorGuiPacketType.CLIENT_INDEX)
			{
				dataStream.writeInt(windowId);
			}
			if(packetType == OredictionificatorGuiPacketType.SERVER_INDEX || packetType == OredictionificatorGuiPacketType.CLIENT_INDEX)
			{
				dataStream.writeInt(index);
			}
		}

		@Override
		public void fromBytes(ByteBuf dataStream)
		{
			packetType = OredictionificatorGuiPacketType.values()[dataStream.readInt()];
			coord4D = new Coord4D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
			guiType = dataStream.readInt();
			if(packetType == OredictionificatorGuiPacketType.CLIENT || packetType == OredictionificatorGuiPacketType.CLIENT_INDEX)
			{
				windowId = dataStream.readInt();
			}
			if(packetType == OredictionificatorGuiPacketType.SERVER_INDEX || packetType == OredictionificatorGuiPacketType.CLIENT_INDEX)
			{
				index = dataStream.readInt();
			}
		}
	}

	public static enum OredictionificatorGuiPacketType
	{
		SERVER, CLIENT, SERVER_INDEX, CLIENT_INDEX
	}
}
