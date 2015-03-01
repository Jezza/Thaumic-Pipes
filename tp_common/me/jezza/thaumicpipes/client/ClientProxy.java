package me.jezza.thaumicpipes.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.CommonProxy;
import me.jezza.thaumicpipes.client.particles.AspectTrailFX;
import me.jezza.thaumicpipes.client.renderer.ItemThaumicPipeRenderer;
import me.jezza.thaumicpipes.common.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import thaumcraft.api.aspects.Aspect;

import java.util.List;

import static net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void initServerSide() {
    }

    @Override
    public void initClientSide() {
        registerItemRenderer(ModItems.thaumicPipe, new ItemThaumicPipeRenderer());
    }

    @Override
    public void spawnAspectTrail(int dimID, List<CoordSet> path, Aspect aspect) {
        if (path.isEmpty() || aspect == null)
            return;

        WorldClient world = FMLClientHandler.instance().getClient().theWorld;
        CoordSet coordSet = path.get(0);
        Minecraft.getMinecraft().effectRenderer.addEffect(new AspectTrailFX(world, coordSet.x + 0.5D, coordSet.y + 0.5D, coordSet.z + 0.5D, path, aspect));
    }
}
