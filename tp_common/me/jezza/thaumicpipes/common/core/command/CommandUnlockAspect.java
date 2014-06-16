package me.jezza.thaumicpipes.common.core.command;

import java.lang.reflect.Field;
import java.util.Map;

import me.jezza.thaumicpipes.common.core.TPLogger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;

public class CommandUnlockAspect extends CommandBase {

    @Override
    public String getCommandName() {
        return "uaa";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/uaa <Username>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        if (args.length != 1) {
            iCommandSender.addChatMessage(new ChatComponentText(getCommandUsage(iCommandSender)));
            return;
        }

        Map<String, AspectList> aspects = null;

        try {
            String className = "thaumcraft.common.lib.research.PlayerKnowledge";
            Field field = Class.forName(className).getField("aspectsDiscovered");
            aspects = (Map) field.get(Map.class);
        } catch (Exception ex) {
            TPLogger.severe("Thaumcraft communication error! Report to mod author at once!");
        }

        if (aspects != null) {
            AspectList aspectList = ThaumcraftApiHelper.getAllAspects(50);
            aspects.put(args[0], aspectList);
            iCommandSender.addChatMessage(new ChatComponentText("Added all aspects to " + args[0]));
        }

    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
