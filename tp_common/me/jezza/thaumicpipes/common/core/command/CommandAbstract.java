package me.jezza.thaumicpipes.common.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public abstract class CommandAbstract extends CommandBase {

    private String commandName = "";
    private String commandUsage = "";

    public CommandAbstract(String commandName, String commandUsage) {
        this.commandName = commandName;
        this.commandUsage = commandUsage;

        register();
    }

    private void register() {
        CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
        ch.registerCommand(this);
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + commandName + " " + commandUsage;
    }

    public void sendCommandUsage(ICommandSender commandSender) {
        sendChatMessage(commandSender, getCommandUsage(commandSender));
    }

    public void sendChatMessage(ICommandSender commandSender, String string) {
        commandSender.addChatMessage(new ChatComponentText(string));
    }

    @Override
    public abstract void processCommand(ICommandSender commandSender, String[] args);
}
