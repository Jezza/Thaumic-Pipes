package me.jezza.thaumicpipes;

import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.command.CommandAirBlock;
import me.jezza.thaumicpipes.common.core.command.CommandAreaRemove;
import me.jezza.thaumicpipes.common.core.command.CommandAreaScan;
import me.jezza.thaumicpipes.common.core.config.ConfigHandler;
import me.jezza.thaumicpipes.common.core.multipart.MultiPartFactory;
import me.jezza.thaumicpipes.common.lib.Reference;
import me.jezza.thaumicpipes.common.research.ModRecipes;
import me.jezza.thaumicpipes.common.research.ModResearch;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import thaumcraft.common.Thaumcraft;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, dependencies = "required-after:Thaumcraft@[4.1.0g,);")
public class ThaumicPipes {

    @Instance(Reference.MOD_ID)
    public static ThaumicPipes instance;

    public static CreativeTabs creativeTab = new CreativeTabs(Reference.MOD_ID) {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.thaumicPipe;
        }
    };

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    public static thaumcraft.common.CommonProxy tcProxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        tcProxy = Thaumcraft.proxy;
        TPLogger.init();
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        ModBlocks.init();
        ModItems.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        new MultiPartFactory().init();
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
        if (Reference.COMMANDS) {
            new CommandAirBlock("delete", "<dimID> <x> <y> <z>");
            new CommandAreaRemove("removearea", "<dimID> <x1> <y1> <z1> <x2> <y2> <z2>");
            new CommandAreaScan("removeBlock", "<dimID> <x1> <y1> <z1> <x2> <y2> <z2> <id> <meta>");
        }
    }
}
