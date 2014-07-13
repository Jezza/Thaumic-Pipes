package me.jezza.thaumicpipes.common.multipart;

import java.util.Arrays;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.render.EntityDigIconFX;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class MultiPartAbstract extends TMultiPart implements JNormalOcclusion {

    public abstract ItemStack getStack();

    public abstract Block getBlock();

    @Override
    public void onWorldJoin() {
        super.onWorldJoin();
        onNeighborChanged();
    }

    @Override
    public ItemStack pickItem(MovingObjectPosition hit) {
        return getStack();
    }

    @Override
    public Iterable<ItemStack> getDrops() {
        return Arrays.asList(getStack());
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        Iterable<Cuboid6> occlusionBoxes = getAllOcclusionBoxes();
        LinkedList<IndexedCuboid6> partList = new LinkedList<IndexedCuboid6>();
        for (Cuboid6 c : occlusionBoxes)
            partList.add(new IndexedCuboid6(0, c));
        return partList;
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes() {
        return getAllOcclusionBoxes();
    }

    @Override
    public float getStrength(MovingObjectPosition hit, EntityPlayer player) {
        return getBlock().getPlayerRelativeBlockHardness(player, player.worldObj, hit.blockX, hit.blockY, hit.blockZ) * 30;
    }

    @Override
    public boolean occlusionTest(TMultiPart part) {
        return part instanceof OcclusionPart ? true : NormalOcclusionTest.apply(this, part);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDestroyEffects(MovingObjectPosition hit, EffectRenderer effectRenderer) {
        addDestroyEffects(effectRenderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDestroyEffects(EffectRenderer effectRenderer) {
        EntityDigIconFX.addBlockDestroyEffects(world(), Cuboid6.full.copy().add(Vector3.fromTileEntity(tile())), new IIcon[] { getIcon(), getIcon(), getIcon(), getIcon(), getIcon(), getIcon() }, effectRenderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addHitEffects(MovingObjectPosition hit, EffectRenderer effectRenderer) {
        EntityDigIconFX.addBlockHitEffects(world(), Cuboid6.full.copy().add(Vector3.fromTileEntity(tile())), hit.sideHit, getIcon(), effectRenderer);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return getBlock().getIcon(0, 0);
    }

    public abstract Iterable<Cuboid6> getAllOcclusionBoxes();
}
