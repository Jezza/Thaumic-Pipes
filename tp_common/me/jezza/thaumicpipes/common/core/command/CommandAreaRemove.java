package me.jezza.thaumicpipes.common.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class CommandAreaRemove extends CommandBase {

    @Override
    public String getCommandName() {
        return "removearea";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "removearea <dimID> <x1> <y1> <z1> <x2> <y2> <z2>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        if (args.length != 7) {
            iCommandSender.addChatMessage(new ChatComponentText(getCommandUsage(iCommandSender)));
            return;
        }

        int dimID = Integer.parseInt(args[0]);
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);

        int x2 = Integer.parseInt(args[4]);
        int y2 = Integer.parseInt(args[5]);
        int z2 = Integer.parseInt(args[6]);

        WorldServer world = MinecraftServer.getServer().worldServers[dimID];

        if (x2 < x) {
            int temp = x2;
            x2 = x;
            x = temp;
        }

        if (y2 < y) {
            int temp = y2;
            y2 = y;
            y = temp;
        }

        if (z2 < z) {
            int temp = z2;
            z2 = z;
            z = temp;
        }

        for (int i = x; i <= x2; i++)
            for (int j = y; j <= y2; j++)
                for (int k = z; k <= z2; k++)
                    world.setBlockToAir(i, j, k);

        iCommandSender.addChatMessage(new ChatComponentText("Command Executed Successfully"));
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

}
