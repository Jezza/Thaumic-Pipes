package me.jezza.thaumicpipes.common.blocks;

import java.util.List;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.nodes.INode;

public class BlockThaumicPipe extends BlockTP {

    public static float minX, minY, minZ;
    public static float maxX, maxY, maxZ;

    float min = 0.3125F;
    float max = 0.6875F;

    public BlockThaumicPipe(Material material, String name) {
        super(material, name);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

        minX = minY = minZ = min;
        maxX = maxY = maxZ = max;

        for (int side = 0; side < 6; side++) {
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            if (isMatch(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) && canConnectTo(world, x, y, z, dir)) {
                switch (side) {
                    case 0:
                        minY = 0.0F;
                        break;
                    case 1:
                        maxY = 1.0F;
                        break;
                    case 2:
                        minZ = 0.0F;
                        break;
                    case 3:
                        maxZ = 1.0F;
                        break;
                    case 4:
                        minX = 0.0F;
                        break;
                    case 5:
                        maxX = 1.0F;
                        break;
                }
            }
        }

        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        super.setBlockBoundsBasedOnState(world, x, y, z);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {

        minX = minY = minZ = min;
        maxX = maxY = maxZ = max;

        for (int side = 0; side < 6; side++) {
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            if (isMatch(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) && canConnectTo(world, x, y, z, dir)) {
                switch (side) {
                    case 0:
                        minY = 0.0F;
                        break;
                    case 1:
                        maxY = 1.0F;
                        break;
                    case 2:
                        minZ = 0.0F;
                        break;
                    case 3:
                        maxZ = 1.0F;
                        break;
                    case 4:
                        minX = 0.0F;
                        break;
                    case 5:
                        maxX = 1.0F;
                        break;
                }
            }
        }

        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        super.addCollisionBoxesToList(world, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
    }

    private static boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection forgeDirection) {
        IThaumicPipe pipe = (IThaumicPipe) world.getTileEntity(x, y, z);
        return pipe.canConnectTo(forgeDirection);
    }

    private static boolean isMatch(IBlockAccess world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return isPipe(tileEntity) || isJar(tileEntity) || isContainer(tileEntity);
    }

    private static boolean isPipe(TileEntity tileEntity) {
        return tileEntity instanceof IThaumicPipe;
    }

    private static boolean isJar(TileEntity tileEntity) {
        return tileEntity instanceof IAspectSource;
    }

    private static boolean isContainer(TileEntity tileEntity) {
        return tileEntity instanceof IAspectContainer && !(tileEntity instanceof IAspectSource || tileEntity instanceof INode);
    }

    @Override
    public TileEntity getTileEntity() {
        return new TileThaumicPipe();
    }

    @Override
    public boolean renderWithModel() {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVectorX, float hitVectorY, float hitVectorZ) {
        if (player.getCurrentEquippedItem() != null)
            return false;

        if (world.isRemote)
            return true;

        TileThaumicPipe pipe = (TileThaumicPipe) world.getTileEntity(x, y, z);
        if (player.isSneaking()) {
            pipe.drain();
        } else {
            AspectList aspectList = pipe.getAspectList();
            boolean empty = true;
            for (Aspect aspect : aspectList.getAspects()) {
                if (aspect == null)
                    continue;
                empty = false;
                player.addChatMessage(new ChatComponentText("Contains " + aspectList.getAmount(aspect) + " " + aspect.getName()));
            }
            if (empty)
                player.addChatMessage(new ChatComponentText("Pipe contains no Essentia."));
        }
        return true;
    }
}
