package me.jezza.thaumicpipes.common.core.utils;

import codechicken.lib.vec.BlockCoord;
import io.netty.buffer.ByteBuf;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CoordSet {

    private int x, y, z;

    public CoordSet(int[] array) {
        x = array[0];
        y = array[1];
        z = array[2];
    }

    public CoordSet(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CoordSet(String x, String y, String z) {
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
        this.z = Integer.parseInt(z);
    }

    public CoordSet(TileEntity tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
    }

    public CoordSet(BlockCoord coord) {
        x = coord.x;
        y = coord.y;
        z = coord.z;
    }

    public CoordSet() {
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public CoordSet addX(int x) {
        this.x += x;
        return this;
    }

    public CoordSet addY(int y) {
        this.y += y;
        return this;
    }

    public CoordSet addZ(int z) {
        this.z += z;
        return this;
    }

    public CoordSet addForgeDirection(ForgeDirection direction) {
        x += direction.offsetX;
        y += direction.offsetY;
        z += direction.offsetZ;
        return this;
    }

    public boolean withinRange(CoordSet tempSet, int range) {
        return getDistanceSq(tempSet) <= (range * range);
    }

    public double getDistanceSq(CoordSet tempSet) {
        double x2 = x - tempSet.x;
        double y2 = y - tempSet.y;
        double z2 = z - tempSet.z;
        return x2 * x2 + y2 * y2 + z2 * z2;
    }

    public float getDistance(CoordSet tempSet) {
        return net.minecraft.util.MathHelper.sqrt_double(getDistanceSq(tempSet));
    }

    public TileEntity getTileFromDirection(IBlockAccess world, ForgeDirection direction) {
        return copy().addForgeDirection(direction).getTileEntity(world);
    }

    public Block getBlock(IBlockAccess world) {
        return world.getBlock(x, y, z);
    }

    public TileEntity getTileEntity(IBlockAccess world) {
        return world.getTileEntity(x, y, z);
    }

    public boolean hasTileEntity(IBlockAccess world) {
        return getTileEntity(world) != null;
    }

    public boolean setBlockToAir(World world) {
        return world.setBlockToAir(x, y, z);
    }

    public boolean isAirBlock(World world) {
        return world.isAirBlock(x, y, z);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof CoordSet))
            return false;
        CoordSet coordSet = (CoordSet) other;
        return (coordSet.x == x && coordSet.y == y && coordSet.z == z);
    }

    @Override
    public int hashCode() {
        int hash = this.x;
        hash *= 31 + this.y;
        hash *= 31 + this.z;
        return hash;
    }

    public String toPacketString() {
        return x + ":" + y + ":" + z;
    }

    @Override
    public String toString() {
        return " @ " + toPacketString();
    }

    public CoordSet copy() {
        return new CoordSet(x, y, z);
    }

    public boolean isThaumicPipe(IBlockAccess world) {
        return getTileEntity(world) instanceof TileThaumicPipe;
    }

    public void swap(CoordSet coordSet, Axis axis) {
        int temp = 0;
        switch (axis) {
            case X:
                temp = x;
                x = coordSet.x;
                coordSet.x = temp;
                break;
            case Y:
                temp = y;
                y = coordSet.y;
                coordSet.y = temp;
                break;
            case Z:
                temp = z;
                z = coordSet.z;
                coordSet.z = temp;
                break;
            default:
                break;
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setIntArray("coordSet", new int[] { x, y, z });
    }

    public static CoordSet readFromNBT(NBTTagCompound tag) {
        return new CoordSet(tag.getIntArray("coordSet"));
    }

    public void writeBytes(ByteBuf bytes) {
        bytes.writeInt(x);
        bytes.writeInt(y);
        bytes.writeInt(z);
    }

    public static CoordSet readBytes(ByteBuf bytes) {
        int x = bytes.readInt();
        int y = bytes.readInt();
        int z = bytes.readInt();

        return new CoordSet(x, y, z);
    }

    public static CoordSet createFromMinecraftTag(NBTTagCompound tag) {
        int x = tag.getInteger("x");
        int y = tag.getInteger("y");
        int z = tag.getInteger("z");
        return new CoordSet(x, y, z);
    }

    public static enum Axis {
        X, Y, Z;
    }
}
