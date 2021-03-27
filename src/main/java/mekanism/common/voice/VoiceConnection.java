package mekanism.common.voice;

import mekanism.common.Mekanism;
import mekanism.common.item.ItemWalkieTalkie;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoiceConnection extends Thread
{
	public Socket socket;

	public String username;

	public boolean open = true;

	public DataInputStream input;
	public DataOutputStream output;

	public MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

	public VoiceConnection(Socket s)
	{
		socket = s;
	}

	private static String get_remote_address(EntityPlayerMP player) {
		SocketAddress sockaddr = player.playerNetServerHandler.netManager.getSocketAddress();
		if(sockaddr instanceof InetSocketAddress) {
			InetAddress address = ((InetSocketAddress)sockaddr).getAddress();
			return address.getHostAddress();
		}
		String address = sockaddr.toString();
		int begin_i = address.indexOf('/') + 1;
		int end_i = address.lastIndexOf(':');
		if(end_i < begin_i) end_i = address.length();
		return address.substring(begin_i, end_i);
	}

	@Override
	public void run()
	{
		try {
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			synchronized(Mekanism.voiceManager)
			{
				int retryCount = 0;

				while(username == null && retryCount <= 100)
				{
					try {
						List l = Collections.synchronizedList((List)((ArrayList)server.getConfigurationManager().playerEntityList).clone());

						for(Object obj : l)
						{
							if(obj instanceof EntityPlayerMP)
							{
								EntityPlayerMP player = (EntityPlayerMP)obj;
								String player_address = get_remote_address(player);
								if(!server.isDedicatedServer() && player_address.equals("local") && !Mekanism.voiceManager.foundLocal)
								{
									Mekanism.voiceManager.foundLocal = true;
									username = player.getCommandSenderName();
									break;
								}
								else if(player_address.equals(socket.getInetAddress().getHostAddress()))
								{
									username = player.getCommandSenderName();
									break;
								}
							}
						}

						retryCount++;
						Thread.sleep(50);
					} catch(Exception e) {}
				}

				if(username == null)
				{
					Mekanism.logger.error("VoiceServer: Unable to trace connection's remote address.");
					kill();
					return;
				}
				Mekanism.logger.info(String.format("VoiceServer: Traced remote address in %d attempt%s.", retryCount, retryCount == 1 ? "" : "s"));
			}
		} catch(Exception e) {
			Mekanism.logger.error("VoiceServer: Error while starting server-based connection.");
			e.printStackTrace();
			open = false;
		}

		// Main client listen thread
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while(open)
				{
					try {
						short byteCount = VoiceConnection.this.input.readShort();
						byte[] audioData = new byte[byteCount];
						VoiceConnection.this.input.readFully(audioData);

						if(byteCount > 0)
						{
							Mekanism.voiceManager.sendToPlayers(byteCount, audioData, VoiceConnection.this);
						}
					} catch(Exception e) {
						open = false;
					}
				}

				kill();
			}
		}).start();
	}

	public void kill()
	{
		try {
			input.close();
			output.close();
			socket.close();

			Mekanism.voiceManager.connections.remove(this);
		} catch(Exception e) {
			Mekanism.logger.error("VoiceServer: Error while stopping server-based connection.");
			e.printStackTrace();
		}
	}

	public void sendToPlayer(short byteCount, byte[] audioData, VoiceConnection connection)
	{
		if(!open)
		{
			kill();
		}

		try {
			output.writeShort(byteCount);
			output.write(audioData);

			output.flush();
		} catch(Exception e) {
			Mekanism.logger.error("VoiceServer: Error while sending data to player.");
			e.printStackTrace();
		}
	}

	public boolean canListen(int channel)
	{
		for(ItemStack itemStack : getPlayer().inventory.mainInventory)
		{
			if(itemStack != null)
			{
				if(itemStack.getItem() instanceof ItemWalkieTalkie)
				{
					if(((ItemWalkieTalkie)itemStack.getItem()).getOn(itemStack))
					{
						if(((ItemWalkieTalkie)itemStack.getItem()).getChannel(itemStack) == channel)
						{
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public int getCurrentChannel()
	{
		ItemStack itemStack = getPlayer().getCurrentEquippedItem();

		if(itemStack != null)
		{
			ItemWalkieTalkie walkieTalkie = (ItemWalkieTalkie)itemStack.getItem();

			if(walkieTalkie != null)
			{
				if(walkieTalkie.getOn(itemStack))
				{
					return walkieTalkie.getChannel(itemStack);
				}
			}
		}

		return 0;
	}

	public EntityPlayerMP getPlayer()
	{
		return server.getConfigurationManager().func_152612_a(username); //TODO getPlayerForUsername
	}
}
