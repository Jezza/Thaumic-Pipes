package me.jezza.thaumicpipes.common.core.config;

import me.jezza.thaumicpipes.common.lib.Strings;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

import static me.jezza.thaumicpipes.common.lib.CoreProperties.*;

public class ConfigHandler {

    private static Configuration config;

    private static final String MISC = "Gameplay";

    public static void init(File file) {
        config = new Configuration(file);

        try {
            config.load();

            logger.info("Configs started to load");

            getConstants();

            logger.info("Configs loaded successfully");

        } catch (Exception e) {
        } finally {
            config.save();
        }
    }

    public static void getConstants() {
        COMMANDS = config.get(MISC, "commands", COMMANDS, "They aren't thaumcraft related, just some useful commands.").getBoolean(COMMANDS);

        DIFFICULTY_LEVEL = config.getInt(MISC, "difficultyLevel", DIFFICULTY_LEVEL, 0, 2, "0 - Simple crafting recipe inside the arcane crafting table.\n1 - Infusion recipe.\n2 - Really hard infusion recipe, as in really hard.\nIn fact, it's probably easier to write your own mod when it's on this.\nYou WILL not succeed.\n");


        TextureMaps.THAUMIC_TEXTURE_INDEX = config.getInt(MISC, Strings.THAUMIC_PIPE_TEXTURE, TextureMaps.THAUMIC_TEXTURE_INDEX, 0, 2, "0 - Steam punk style texture.\n1 - Old thaumcraft style texture.\n2 - The default texture.");
    }

    public static int getID(String path, int defaultID) {
        return config.get("IDs", path, defaultID).getInt();
    }

    public static int getID(String path, int defaultID, String comment) {
        return config.get("IDs", path, defaultID, comment).getInt();
    }
}
