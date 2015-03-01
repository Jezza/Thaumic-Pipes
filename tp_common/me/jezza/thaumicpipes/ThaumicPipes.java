package me.jezza.thaumicpipes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import me.jezza.oc.api.configuration.Config;
import me.jezza.oc.client.CreativeTabSimple;
import me.jezza.oc.common.core.network.NetworkDispatcher;
import me.jezza.thaumicpipes.api.ThaumicRegistry;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.RegistryHelper;
import me.jezza.thaumicpipes.common.multipart.MultiPartFactory;
import me.jezza.thaumicpipes.common.packet.AspectPacket;
import me.jezza.thaumicpipes.common.research.ModRecipes;
import me.jezza.thaumicpipes.common.research.ModResearch;
import me.jezza.thaumicpipes.common.transport.connection.ConnectionType;

import static me.jezza.thaumicpipes.common.lib.CoreProperties.*;

@Config.Controller
@Mod(modid = MOD_ID, name = MOD_NAME, version = VERSION, dependencies = DEPENDENCIES)
public class ThaumicPipes {

    @Instance(MOD_ID)
    public static ThaumicPipes instance;

    public static CreativeTabSimple creativeTab = new CreativeTabSimple(MOD_ID);

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    public static NetworkDispatcher network;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        ModBlocks.init();
        ModItems.init();

        creativeTab.setIcon(ModItems.thaumicPipe);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        new MultiPartFactory().init();
        CommonProxy.createNetworkInstance();
        proxy.initServerSide();
        proxy.initClientSide();

        RegistryHelper.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModRecipes.init();
        ModResearch.init();

        network = new NetworkDispatcher(MOD_ID);
        network.registerMessage(AspectPacket.class, Side.CLIENT);

        ThaumicRegistry.lock();
        ConnectionType.wasLocked();
    }
}
