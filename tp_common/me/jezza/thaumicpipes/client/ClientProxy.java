package me.jezza.thaumicpipes.client;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import me.jezza.thaumicpipes.CommonProxy;
import me.jezza.thaumicpipes.client.renderer.ItemThaumicPipeRenderer;
import me.jezza.thaumicpipes.client.renderer.TileThaumicPipeRenderer;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void initClientSide() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileThaumicPipe.class, new TileThaumicPipeRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.thaumicPipe), new ItemThaumicPipeRenderer());
    }

}
