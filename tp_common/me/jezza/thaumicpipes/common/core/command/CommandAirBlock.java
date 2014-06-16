package me.jezza.thaumicpipes.common.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class CommandAirBlock extends CommandBase {

    @Override
    public String getCommandName() {
        return "delete";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/delete <dimID> <x> <y> <z>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        if (args.length != 4) {
            iCommandSender.addChatMessage(new ChatComponentText(getCommandUsage(iCommandSender)));
            return;
        }

        int dimID = Integer.parseInt(args[0]);
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);

        WorldServer world = MinecraftServer.getServer().worldServers[dimID];

        world.setBlockToAir(x, y, z);

        iCommandSender.addChatMessage(new ChatComponentText("Command Executed Successfully"));
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

}
