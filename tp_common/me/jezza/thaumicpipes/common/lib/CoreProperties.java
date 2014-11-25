package me.jezza.thaumicpipes.common.lib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CoreProperties {

    public static final String MOD_ID = "ThaumicPipes";
    public static final String MOD_NAME = "Thaumic Pipes";

    public static final String MOD_IDENTIFIER = MOD_ID + ":";

    public static final String CLIENT_PROXY_CLASS = "me.jezza.thaumicpipes.client.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "me.jezza.thaumicpipes.CommonProxy";

    public static Logger logger = LogManager.getLogger("ThaumicPipes");

    public static final String DEPENDENCIES = "required-after:OmnisCore;required-after:Thaumcraft;after:ThaumicTinkerer";

    public static final int PIPE_ANIMATION_SIZE = 18;

    public static boolean COMMANDS = false;

    public static int DIFFICULTY_LEVEL = 0;

}
