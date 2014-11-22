package me.jezza.thaumicpipes.common.core.config;

import me.jezza.thaumicpipes.common.lib.Strings;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import net.minecraft.util.MathHelper;
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

        int textureIndex = config.get(MISC, Strings.THAUMIC_PIPE_TEXTURE, TextureMaps.THAUMIC_TEXTURE_INDEX, "0 - Steam punk style texture.\n1 - Old thaumcraft style texture.\n2 - The default texture.").getInt();
        TextureMaps.THAUMIC_TEXTURE_INDEX = MathHelper.clamp_int(textureIndex, 0, 2);
    }

    public static int getID(String path, int defaultID) {
        return config.get("IDs", path, defaultID).getInt();
    }

    public static int getID(String path, int defaultID, String comment) {
        return config.get("IDs", path, defaultID, comment).getInt();
    }
}
