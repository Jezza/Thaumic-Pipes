package me.jezza.thaumicpipes.common.core.command;

import me.jezza.oc.common.core.command.CommandAbstractArea;
import me.jezza.oc.common.utils.CoordSet;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandAreaScan extends CommandAbstractArea {

    public CommandAreaScan(String commandName, String commandUsage) {
        super(commandName, commandUsage, 9, 9);
    }

    @Override
    public void processAreaCommand(ICommandSender commandSender, String[] args) {
        int dimID = Integer.parseInt(args[0]);
        CoordSet firstSet = new CoordSet(args[1], args[2], args[3]);
        CoordSet secondSet = new CoordSet(args[4], args[5], args[6]);

        int id = Integer.parseInt(args[7]);
        int meta = Integer.parseInt(args[8]);

        WorldServer world = MinecraftServer.getServer().worldServers[dimID];

        sortCoordSets(firstSet, secondSet);

        int index = 0, found = 0;
        for (int i = firstSet.getX(); i <= secondSet.getX(); i++)
            for (int j = firstSet.getY(); j <= secondSet.getY(); j++)
                for (int k = firstSet.getZ(); k <= secondSet.getZ(); k++)
                    if (world.getBlock(i, j, k).equals(Block.getBlockById(id)) && world.getBlockMetadata(i, j, k) == meta) {
                        found++;
                        if (world.setBlockToAir(i, j, k))
                            index++;
                    }

        sendChatMessage(commandSender, "Command Executed Successfully");
        if (found != index)
            sendChatMessage(commandSender, "Found: " + found);
        sendChatMessage(commandSender, "Successfully Removed: " + index);
    }

}
