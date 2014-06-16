package me.jezza.thaumicpipes.common.core.command;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class CommandAreaScan extends CommandBase {

    @Override
    public String getCommandName() {
        return "removeBlock";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "removeBlock <dimID> <x1> <y1> <z1> <x2> <y2> <z2> <id> <meta>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        if (args.length != 9) {
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

        int id = Integer.parseInt(args[7]);
        int meta = Integer.parseInt(args[8]);

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

        int index = 0;
        for (int i = x; i <= x2; i++)
            for (int j = y; j <= y2; j++)
                for (int k = z; k <= z2; k++)
                    if (world.getBlock(i, j, k).equals(Block.getBlockById(id)) && world.getBlockMetadata(i, j, k) == meta) {
                        index++;
                        world.setBlockToAir(i, j, k);
                    }

        iCommandSender.addChatMessage(new ChatComponentText("Command Executed Successfully"));
        iCommandSender.addChatMessage(new ChatComponentText("Removed: " + index));
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
