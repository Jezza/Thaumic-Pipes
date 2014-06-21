package me.jezza.thaumicpipes.common.core.command;

import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.utils.CoordSet.Axis;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandAreaRemove extends CommandAbstract {

    public CommandAreaRemove(String commandName, String commandUsage) {
        super(commandName, commandUsage);
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args.length != 7) {
            sendCommandUsage(commandSender);
            return;
        }

        int dimID = Integer.parseInt(args[0]);
        CoordSet firstSet = new CoordSet(args[1], args[2], args[3]);
        CoordSet secondSet = new CoordSet(args[4], args[5], args[6]);

        WorldServer world = MinecraftServer.getServer().worldServers[dimID];

        if (secondSet.getX() < firstSet.getX())
            firstSet.swap(secondSet, Axis.X);

        if (secondSet.getY() < firstSet.getY())
            firstSet.swap(secondSet, Axis.Y);

        if (secondSet.getZ() < firstSet.getZ())
            firstSet.swap(secondSet, Axis.Z);

        for (int i = firstSet.getX(); i <= secondSet.getX(); i++)
            for (int j = firstSet.getY(); j <= secondSet.getY(); j++)
                for (int k = firstSet.getZ(); k <= secondSet.getZ(); k++)
                    world.setBlockToAir(i, j, k);

        sendChatMessage(commandSender, "Command Executed Successfully");
    }
}
