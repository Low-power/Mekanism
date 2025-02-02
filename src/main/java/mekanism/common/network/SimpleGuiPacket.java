package mekanism.common.network;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.base.IGuiProvider;
import mekanism.common.network.SimpleGuiPacket.SimpleGuiMessage;
import mekanism.common.tile.BasicBlockTileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;

public class SimpleGuiPacket implements IMessageHandler<SimpleGuiMessage, IMessage>
{
	public static List<IGuiProvider> handlers = new ArrayList<IGuiProvider>();

	@Override
	public IMessage onMessage(SimpleGuiMessage message, MessageContext context) {
		EntityPlayer player = PacketHandler.getPlayer(context);
		if(!player.worldObj.isRemote)
		{
			World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.coord4D.dimensionId);

			if(world != null && message.coord4D.getTileEntity(world) instanceof BasicBlockTileEntity)
			{
				if(message.guiId == -1)
				{
					return null;
				}

				SimpleGuiMessage.openServerGui(message.guiHandler, message.guiId, (EntityPlayerMP)player, player.worldObj, message.coord4D);
			}
		}
		else {
			FMLCommonHandler.instance().showGuiScreen(SimpleGuiMessage.getGui(message.guiHandler, message.guiId, player, player.worldObj, message.coord4D));
			player.openContainer.windowId = message.windowId;
		}
		return null;
	}

	public static class SimpleGuiMessage implements IMessage
	{
		public Coord4D coord4D;

		public int guiHandler;

		public int guiId;

		public int windowId;

		public SimpleGuiMessage() {}

		public SimpleGuiMessage(Coord4D coord, int handler, int gui)
		{
			coord4D = coord;
			guiHandler = handler;
			guiId = gui;
		}

		public SimpleGuiMessage(Coord4D coord, int handler, int gui, int id)
		{
			this(coord, handler, gui);
			windowId = id;
		}

		public static void openServerGui(int handler, int id, EntityPlayerMP player, World world, Coord4D obj)
		{
			player.closeContainer();
			player.getNextWindowId();
			int window = player.currentWindowId;
			Mekanism.packetHandler.sendTo(new SimpleGuiMessage(obj, handler, id, window), player);
			player.openContainer = handlers.get(handler).getServerGui(id, player, world, obj.xCoord, obj.yCoord, obj.zCoord);
			player.openContainer.windowId = window;
			player.openContainer.addCraftingToCrafters(player);
		}

		@SideOnly(Side.CLIENT)
		public static GuiScreen getGui(int handler, int id, EntityPlayer player, World world, Coord4D obj)
		{
			return (GuiScreen)handlers.get(handler).getClientGui(id, player, world, obj.xCoord, obj.yCoord, obj.zCoord);
		}

		@Override
		public void toBytes(ByteBuf dataStream)
		{
			dataStream.writeInt(coord4D.xCoord);
			dataStream.writeInt(coord4D.yCoord);
			dataStream.writeInt(coord4D.zCoord);
			dataStream.writeInt(coord4D.dimensionId);
			dataStream.writeInt(guiHandler);
			dataStream.writeInt(guiId);
			dataStream.writeInt(windowId);
		}

		@Override
		public void fromBytes(ByteBuf dataStream)
		{
			coord4D = new Coord4D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
			guiHandler = dataStream.readInt();
			guiId = dataStream.readInt();
			windowId = dataStream.readInt();
		}
	}
}
