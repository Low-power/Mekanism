package mekanism.common.network;

import mekanism.api.Coord4D;
import mekanism.client.gui.DigitalMinerGui;
import mekanism.client.gui.DigitalMinerConfigGui;
import mekanism.client.gui.MFilterSelectGui;
import mekanism.client.gui.MItemStackFilterGui;
import mekanism.client.gui.MMaterialFilterGui;
import mekanism.client.gui.MModIDFilterGui;
import mekanism.client.gui.MOreDictFilterGui;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.inventory.container.DigitalMinerContainer;
import mekanism.common.inventory.container.FilterContainer;
import mekanism.common.inventory.container.NullContainer;
import mekanism.common.network.DigitalMinerGuiPacket.DigitalMinerGuiMessage;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.ContainerTileEntity;
import mekanism.common.tile.DigitalMinerTileEntity;
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

public class DigitalMinerGuiPacket implements IMessageHandler<DigitalMinerGuiMessage, IMessage>
{
	@Override
	public IMessage onMessage(DigitalMinerGuiMessage message, MessageContext context) {
		EntityPlayer player = PacketHandler.getPlayer(context);
		if(!player.worldObj.isRemote)
		{
			World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.coord4D.dimensionId);

			if(world != null && message.coord4D.getTileEntity(world) instanceof DigitalMinerTileEntity)
			{
				DigitalMinerGuiMessage.openServerGui(message.packetType, message.guiType, world, (EntityPlayerMP)player, message.coord4D, message.index);
			}
		}
		else {
			if(message.coord4D.getTileEntity(player.worldObj) instanceof DigitalMinerTileEntity)
			{
				try {
					if(message.packetType == MinerGuiPacket.CLIENT)
					{
						FMLCommonHandler.instance().showGuiScreen(DigitalMinerGuiMessage.getGui(message.packetType, message.guiType, player, player.worldObj, message.coord4D.xCoord, message.coord4D.yCoord, message.coord4D.zCoord, -1));
					}
					else if(message.packetType == MinerGuiPacket.CLIENT_INDEX)
					{
						FMLCommonHandler.instance().showGuiScreen(DigitalMinerGuiMessage.getGui(message.packetType, message.guiType, player, player.worldObj, message.coord4D.xCoord, message.coord4D.yCoord, message.coord4D.zCoord, message.index));
					}

					player.openContainer.windowId = message.windowId;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static class DigitalMinerGuiMessage implements IMessage
	{
		public Coord4D coord4D;
		public MinerGuiPacket packetType;
		public int guiType;
		public int windowId = -1;
		public int index = -1;
		public DigitalMinerGuiMessage() {}
		public DigitalMinerGuiMessage(MinerGuiPacket type, Coord4D coord, int guiID, int extra, int extra2)
		{
			packetType = type;
			coord4D = coord;
			guiType = guiID;
			if(packetType == MinerGuiPacket.CLIENT)
			{
				windowId = extra;
			}
			else if(packetType == MinerGuiPacket.SERVER_INDEX)
			{
				index = extra;
			}
			else if(packetType == MinerGuiPacket.CLIENT_INDEX)
			{
				windowId = extra;
				index = extra2;
			}
		}
		public static void openServerGui(MinerGuiPacket t, int guiType, World world, EntityPlayerMP player, Coord4D obj, int i)
		{
			Container container = null;
			player.closeContainer();
			if(guiType == 0)
			{
				container = new NullContainer(player, (ContainerTileEntity)obj.getTileEntity(world));
			}
			else if(guiType == 4)
			{
				container = new DigitalMinerContainer(player.inventory, (DigitalMinerTileEntity)obj.getTileEntity(world));
			}
			else if(guiType == 5)
			{
				container = new NullContainer(player, (ContainerTileEntity)obj.getTileEntity(world));
			}
			else if(guiType == 1 || guiType == 2 || guiType == 3 || guiType == 6)
			{
				container = new FilterContainer(player.inventory, (ContainerTileEntity)obj.getTileEntity(world));
			}
			player.getNextWindowId();
			int window = player.currentWindowId;
			if(t == MinerGuiPacket.SERVER)
			{
				Mekanism.packetHandler.sendTo(new DigitalMinerGuiMessage(MinerGuiPacket.CLIENT, obj, guiType, window, 0), player);
			}
			else if(t == MinerGuiPacket.SERVER_INDEX)
			{
				Mekanism.packetHandler.sendTo(new DigitalMinerGuiMessage(MinerGuiPacket.CLIENT_INDEX, obj, guiType, window, i), player);
			}
			player.openContainer = container;
			player.openContainer.windowId = window;
			player.openContainer.addCraftingToCrafters(player);
			if(guiType == 0)
			{
				DigitalMinerTileEntity tile = (DigitalMinerTileEntity)obj.getTileEntity(world);
				for(EntityPlayer p : tile.playersUsing)
				{
					Mekanism.packetHandler.sendTo(new TileEntityMessage(obj, tile.getFilterPacket(new ArrayList())), (EntityPlayerMP)p);
				}
			}
		}
		@SideOnly(Side.CLIENT)
		public static GuiScreen getGui(MinerGuiPacket packetType, int type, EntityPlayer player, World world, int x, int y, int z, int index)
		{
			if(type == 0)
			{
				return new DigitalMinerConfigGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z));
			}
			else if(type == 4)
			{
				return new DigitalMinerGui(player.inventory, (DigitalMinerTileEntity)world.getTileEntity(x, y, z));
			}
			else if(type == 5)
			{
				return new MFilterSelectGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z));
			}
			else {
				if(packetType == MinerGuiPacket.CLIENT)
				{
					if(type == 1)
					{
						return new MItemStackFilterGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z));
					}
					else if(type == 2)
					{
						return new MOreDictFilterGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z));
					}
					else if(type == 3)
					{
						return new MMaterialFilterGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z));
					}
					else if(type == 6)
					{
						return new MModIDFilterGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z));
					}
				}
				else if(packetType == MinerGuiPacket.CLIENT_INDEX)
				{
					if(type == 1)
					{
						return new MItemStackFilterGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z), index);
					}
					else if(type == 2)
					{
						return new MOreDictFilterGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z), index);
					}
					else if(type == 3)
					{
						return new MMaterialFilterGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z), index);
					}
					else if(type == 6)
					{
						return new MModIDFilterGui(player, (DigitalMinerTileEntity)world.getTileEntity(x, y, z), index);
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
			if(packetType == MinerGuiPacket.CLIENT || packetType == MinerGuiPacket.CLIENT_INDEX)
			{
				dataStream.writeInt(windowId);
			}
			if(packetType == MinerGuiPacket.SERVER_INDEX || packetType == MinerGuiPacket.CLIENT_INDEX)
			{
				dataStream.writeInt(index);
			}
		}
		@Override
		public void fromBytes(ByteBuf dataStream)
		{
			packetType = MinerGuiPacket.values()[dataStream.readInt()];
			coord4D = new Coord4D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
			guiType = dataStream.readInt();
			if(packetType == MinerGuiPacket.CLIENT || packetType == MinerGuiPacket.CLIENT_INDEX)
			{
				windowId = dataStream.readInt();
			}
			if(packetType == MinerGuiPacket.SERVER_INDEX || packetType == MinerGuiPacket.CLIENT_INDEX)
			{
				index = dataStream.readInt();
			}
		}
	}

	public static enum MinerGuiPacket
	{
		SERVER, CLIENT, SERVER_INDEX, CLIENT_INDEX
	}
}
