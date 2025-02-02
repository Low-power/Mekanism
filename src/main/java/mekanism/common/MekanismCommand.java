package mekanism.common;

import mekanism.api.EnumColor;
import mekanism.api.MekanismAPI;
import mekanism.api.MekanismConfig.general;
import mekanism.common.frequency.Frequency;
import mekanism.common.frequency.FrequencyManager;
import mekanism.common.tile.TeleporterTileEntity;
import mekanism.common.util.MekanismUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import java.util.Arrays;
import java.util.List;

public class MekanismCommand extends CommandBase
{
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(sender);
	}

	@Override
	public String getCommandName()
	{
		return "mekanism";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/mekanism [<subcommand>] [...]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		if(params.length < 1)
		{
			sender.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " -------------"));
			sender.addChatMessage(new ChatComponentText(EnumColor.GREY + " *Version: " + EnumColor.DARK_GREY + Mekanism.versionNumber));
			sender.addChatMessage(new ChatComponentText(EnumColor.GREY + " *Developed on Mac OS X 10.8 Mountain Lion"));
			sender.addChatMessage(new ChatComponentText(EnumColor.GREY + " *Code, textures, and ideas by aidancbrady"));
			sender.addChatMessage(new ChatComponentText(EnumColor.GREY + " *Try " + EnumColor.AQUA + "/mekanism help" + EnumColor.GREY + " for subcommands"));
			sender.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[========]" + EnumColor.GREY + " -------------"));
		}
		else if(params.length >= 1)
		{
			if(params[0].equalsIgnoreCase("help"))
			{
				if(params.length == 1)
				{
					sender.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " -------------"));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism help" + EnumColor.GREY + " -- displays this guide."));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism version" + EnumColor.GREY + " -- displays the version number."));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism debug" + EnumColor.GREY + " -- toggles Mekanism's debug mode."));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism teleporter" + EnumColor.GREY + " -- provides information on teleporters."));
					sender.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[========]" + EnumColor.GREY + " -------------"));
				}
				else if(params[1].equalsIgnoreCase("teleporter"))
				{
					sender.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " -------------"));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism teleporter freq list" + EnumColor.GREY + " -- displays a list of the public frequencies."));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism teleporter freq list <user>" + EnumColor.GREY + " -- displays a list of a certain user's private frequencies."));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism teleporter freq delete <freq>" + EnumColor.GREY + " -- removes a frequency from the public list."));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism teleporter freq delete <user> <freq>" + EnumColor.GREY + " -- removes a freqency from a certain user's private list."));
					sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " /mekanism teleporter freq deleteAll <user>" + EnumColor.GREY + " -- removes all frequencies owned by a certain user."));
					sender.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[========]" + EnumColor.GREY + " -------------"));
				}
			}
			else if(params[0].equalsIgnoreCase("version"))
			{
				sender.addChatMessage(new ChatComponentText("Version " + Mekanism.versionNumber));
			}
			else if(params[0].equalsIgnoreCase("teleporter"))
			{
				if(params.length < 2)
				{
					sender.addChatMessage(new ChatComponentText(String.format("%s[Mekanism]%s Missing subcommand for 'teleporter'. Type %s/mekanism help teleporter%s for help.", EnumColor.DARK_BLUE, EnumColor.GREY, EnumColor.INDIGO, EnumColor.GREY)));
				}
				else if(params[1].equalsIgnoreCase("freq") || params[1].equalsIgnoreCase("frequencies"))
				{
					if(params.length < 3) {
						sender.addChatMessage(new ChatComponentText(String.format("%s[Mekanism]%s Missing subcommand for 'teleporter %s'. Type %s/mekanism help teleporter%s for help.", EnumColor.DARK_BLUE, EnumColor.GREY, params[1], EnumColor.INDIGO, EnumColor.GREY)));
					} else if(params[2].equalsIgnoreCase("list"))
					{
						sender.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " -------------"));
						if(params.length == 3)
						{
							for(Frequency freq : Mekanism.publicTeleporters.getFrequencies())
							{
								sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " - " + freq.name + EnumColor.GREY + " (" + freq.owner + ")"));
							}
						}
						else {
							FrequencyManager manager = TeleporterTileEntity.loadManager(params[3].trim(), sender.getEntityWorld());
							if(manager != null)
							{
								for(Frequency freq : manager.getFrequencies())
								{
									sender.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " - " + freq.name + EnumColor.GREY + " (" + freq.owner + ")"));
								}
							}
							else {
								sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " User profile doesn't exist."));
							}
						}
						sender.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[========]" + EnumColor.GREY + " -------------"));
					}
					else if(params[2].equalsIgnoreCase("delete"))
					{
						if(params.length == 4)
						{
							if(Mekanism.publicTeleporters.containsFrequency(params[3].trim()))
							{
								Mekanism.publicTeleporters.remove(params[3].trim());
								sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " Successfully removed frequency."));
							}
							else {
								sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " No such frequency found."));
							}
						}
						else if(params.length == 5)
						{
							FrequencyManager manager = TeleporterTileEntity.loadManager(params[3].trim(), sender.getEntityWorld());
							if(manager != null)
							{
								if(manager.containsFrequency(params[4].trim()))
								{
									manager.remove(params[4].trim());
									sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " Successfully removed frequency."));
								}
								else {
									sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " No such frequency found."));
								}
							}
							else {
								sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " User profile doesn't exist."));
							}
						}
					}
					else if(params[2].equalsIgnoreCase("deleteAll"))
					{
						if(params.length == 4)
						{
							String owner = params[3].trim();
							FrequencyManager manager = TeleporterTileEntity.loadManager(owner, sender.getEntityWorld());
							if(manager != null)
							{
								int amount = Mekanism.publicTeleporters.removeAll(owner);
								amount += manager.removeAll(owner);
								sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " Successfully removed " + amount + " frequencies."));
							}
							else {
								sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " User profile doesn't exist."));
							}
						}
					} else {
						sender.addChatMessage(new ChatComponentText(String.format("%s[Mekanism]%s Invalid subcommand for 'teleporter %s'.", EnumColor.DARK_BLUE, EnumColor.GREY, params[1])));
					}
				} else {
					sender.addChatMessage(new ChatComponentText(String.format("%s[Mekanism]%s Invalid subcommand for 'teleporter'.", EnumColor.DARK_BLUE, EnumColor.GREY)));
				}
			}
			else if(params[0].equalsIgnoreCase("debug"))
			{
				MekanismAPI.debug = !MekanismAPI.debug;
				sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " Debug mode set to " + EnumColor.DARK_GREY + MekanismAPI.debug));
			}
			else if(params[0].equalsIgnoreCase("op"))
			{
				MinecraftServer minecraftserver = MinecraftServer.getServer();

				if (Mekanism.gameProfile != null)
				{
					minecraftserver.getConfigurationManager().func_152605_a(Mekanism.gameProfile);
					func_152373_a(sender, this, "commands.op.success", new Object[] {"[Mekanism]"});
				}
			}
			else if(params[0].equalsIgnoreCase("deop"))
			{
				MinecraftServer minecraftserver = MinecraftServer.getServer();

				if (Mekanism.gameProfile != null)
				{
					minecraftserver.getConfigurationManager().func_152610_b(Mekanism.gameProfile);
					func_152373_a(sender, this, "commands.deop.success", new Object[] {"[Mekanism]"});
				}
			}
			else {
				sender.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism]" + EnumColor.GREY + " Unknown command. Type '" + EnumColor.INDIGO + "/mekanism help" + EnumColor.GREY + "' for help."));
			}
		}
	}

	@Override
	public int compareTo(Object obj)
	{
		return 0;
	}
}
