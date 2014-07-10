package me.jezza.thaumicpipes.client;

import me.jezza.thaumicpipes.CommonProxy;
import me.jezza.thaumicpipes.client.renderer.ItemThaumicPipeRenderer;
import me.jezza.thaumicpipes.client.renderer.TileThaumicPipeRenderer;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void initClientSide() {
        registerTileEntityRenderer(TileThaumicPipe.class, new TileThaumicPipeRenderer());

        registerItemRenderer(ModItems.thaumicPipe, new ItemThaumicPipeRenderer());
    }

    private void registerTileEntityRenderer(Class<? extends TileEntity> clazz, TileEntitySpecialRenderer renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(clazz, renderer);
    }

    private void registerItemRenderer(Item item, IItemRenderer renderer) {
        MinecraftForgeClient.registerItemRenderer(item, new ItemThaumicPipeRenderer());
    }

}
