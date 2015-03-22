package me.jezza.thaumicpipes.client.particles;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.oc.common.utils.CoordSet;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.awt.Color;
import java.util.List;

@SideOnly(Side.CLIENT)
public class AspectTrailFX extends EntityFX {

    public static int TICKS_PER_NODE = 5;

    private int currentTick = 0;
    private int index = 1;
    private CoordSet current, next;

    public final Aspect aspect;
    public final List<CoordSet> path;

    public AspectTrailFX(World world, double x, double y, double z, List<CoordSet> path, Aspect aspect) {
        super(world, x, y, z);
        this.aspect = aspect;
        this.path = path;
        particleMaxAge = path.size() * TICKS_PER_NODE;
        noClip = true;
        setParticleTextureIndex(36);
        particleScale = 1.0F;

        Color c = new Color(aspect.getColor());
        float mr = (float) c.getRed() / 255.0F * 0.2F;
        float mg = (float) c.getGreen() / 255.0F * 0.2F;
        float mb = (float) c.getBlue() / 255.0F * 0.2F;
        this.particleRed = (float) c.getRed() / 255.0F - mr + this.rand.nextFloat() * mr;
        this.particleGreen = (float) c.getGreen() / 255.0F - mg + this.rand.nextFloat() * mg;
        this.particleBlue = (float) c.getBlue() / 255.0F - mb + this.rand.nextFloat() * mb;

        EntityLivingBase e = FMLClientHandler.instance().getClient().renderViewEntity;
        if (e == null)
            return;

        byte visibleDistance = 64;
        if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics)
            visibleDistance = 32;

        if (e.getDistance(this.posX, this.posY, this.posZ) > (double) visibleDistance)
            this.particleMaxAge = 0;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (currentTick++ % TICKS_PER_NODE == 0) {
            if (index >= path.size()) {
                setDead();
                return;
            }

            current = next != null ? next : path.get(0);
            next = path.get(index);

            index++;
        }

        if (next == null || current == null) {
            setDead();
            return;
        }

        motionX = ((next.x + ((rand.nextFloat() - 0.5F))) - current.x) / (float) TICKS_PER_NODE;
        motionY = ((next.y + ((rand.nextFloat() - 0.5F))) - current.y) / (float) TICKS_PER_NODE;
        motionZ = ((next.z + ((rand.nextFloat() - 0.5F))) - current.z) / (float) TICKS_PER_NODE;

        moveEntity(this.motionX, this.motionY, this.motionZ);

        if (particleAge++ >= particleMaxAge)
            setDead();
    }

    @Override
    public void renderParticle(Tessellator tessellator, float tick, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY) {
        float f6 = (float) this.particleTextureIndexX / 16.0F;
        float f7 = f6 + (0.0624375F * 4);
        float f8 = (float) this.particleTextureIndexY / 16.0F;
        float f9 = f8 + (0.0624375F * 4);
        float scale = 0.1F * this.particleScale;

        if (this.particleIcon != null) {
            f6 = this.particleIcon.getMinU();
            f7 = this.particleIcon.getMaxU();
            f8 = this.particleIcon.getMinV();
            f9 = this.particleIcon.getMaxV();
        }

        float dX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) tick - interpPosX);
        float dY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) tick - interpPosY);
        float dZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) tick - interpPosZ);
        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tessellator.addVertexWithUV((double) (dX - rotationX * scale - rotationYZ * scale), (double) (dY - rotationXZ * scale), (double) (dZ - rotationZ * scale - rotationXY * scale), (double) f7, (double) f9);
        tessellator.addVertexWithUV((double) (dX - rotationX * scale + rotationYZ * scale), (double) (dY + rotationXZ * scale), (double) (dZ - rotationZ * scale + rotationXY * scale), (double) f7, (double) f8);
        tessellator.addVertexWithUV((double) (dX + rotationX * scale + rotationYZ * scale), (double) (dY + rotationXZ * scale), (double) (dZ + rotationZ * scale + rotationXY * scale), (double) f6, (double) f8);
        tessellator.addVertexWithUV((double) (dX + rotationX * scale - rotationYZ * scale), (double) (dY - rotationXZ * scale), (double) (dZ + rotationZ * scale - rotationXY * scale), (double) f6, (double) f9);
    }

    @Override
    public int getFXLayer() {
        return 0;
    }
}
