package me.jezza.thaumicpipes.common.core.command;

import me.jezza.oc.common.core.command.CommandAbstractDefinedLength;
import me.jezza.oc.common.utils.CoordSet;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandAreaRemove extends CommandAbstractDefinedLength {

    public CommandAreaRemove(String commandName, String commandUsage) {
        super(commandName, commandUsage, 7, 7);
    }

    @Override
    public void processValidCommand(ICommandSender commandSender, String[] args) {
        int dimID = Integer.parseInt(args[0]);
        CoordSet firstSet = new CoordSet(args[1], args[2], args[3]);
        CoordSet secondSet = new CoordSet(args[4], args[5], args[6]);

        WorldServer world = MinecraftServer.getServer().worldServers[dimID];

        sortCoordSets(firstSet, secondSet);

        for (int i = firstSet.getX(); i <= secondSet.getX(); i++)
            for (int j = firstSet.getY(); j <= secondSet.getY(); j++)
                for (int k = firstSet.getZ(); k <= secondSet.getZ(); k++)
                    world.setBlockToAir(i, j, k);

        sendChatMessage(commandSender, "Command Executed Successfully");
    }
}
