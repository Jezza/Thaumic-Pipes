package me.jezza.thaumicpipes;

import java.lang.reflect.Field;

import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.command.CommandAirBlock;
import me.jezza.thaumicpipes.common.core.command.CommandAreaRemove;
import me.jezza.thaumicpipes.common.core.command.CommandAreaScan;
import me.jezza.thaumicpipes.common.core.command.CommandUnlockAspect;
import me.jezza.thaumicpipes.common.core.config.ConfigHandler;
import me.jezza.thaumicpipes.common.lib.Reference;
import me.jezza.thaumicpipes.common.research.ModRecipes;
import me.jezza.thaumicpipes.common.research.ModResearch;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, dependencies = "required-after:Thaumcraft@[4.1.0g,);")
public class ThaumicPipes {

    @Instance(Reference.MOD_ID)
    public static ThaumicPipes instance;

    public static CreativeTabs thaumcraftCreativeTab = null;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TPLogger.init();
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        // No way am I making a creative tab for two things... :P
        thaumcraftCreativeTab = getThaumcraftCreativeTab();
        ModBlocks.init();
        ModItems.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerTileEntities();
        proxy.initServerSide();
        proxy.initClientSide();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModRecipes.init();
        ModResearch.init();
    }

    @EventHandler
    public void initServer(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAirBlock());
        event.registerServerCommand(new CommandAreaRemove());
        event.registerServerCommand(new CommandAreaScan());
        event.registerServerCommand(new CommandUnlockAspect());
    }

    private CreativeTabs getThaumcraftCreativeTab() {
        String creativeTabClass = "thaumcraft.common.Thaumcraft";
        CreativeTabs tab = null;
        Class clazz = CreativeTabs.class;

        try {
            Field field = Class.forName(creativeTabClass).getField("tabTC");
            tab = (CreativeTabs) field.get(clazz);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
