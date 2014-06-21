package me.jezza.thaumicpipes.common.core.command;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class CommandAirBlock extends CommandAbstract {

    public CommandAirBlock(String commandName, String commandUsage) {
        super(commandName, commandUsage);
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args.length != 4) {
            sendCommandUsage(commandSender);
            return;
        }

        int dimID = Integer.parseInt(args[0]);
        CoordSet coordSet = new CoordSet(args[1], args[2], args[3]);

        WorldServer world = MinecraftServer.getServer().worldServers[dimID];

        coordSet.setBlockToAir(world);
        sendChatMessage(commandSender, "Command Executed Successfully");
    }
}
