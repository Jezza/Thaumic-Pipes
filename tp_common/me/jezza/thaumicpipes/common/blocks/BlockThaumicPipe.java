package me.jezza.thaumicpipes.common.blocks;

import java.util.List;

import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.nodes.INode;

public class BlockThaumicPipe extends BlockTP {

    private static float minX, minY, minZ;
    private static float maxX, maxY, maxZ;

    private float MIN = 0.3125F;
    private float MAX = 0.6875F;

    public BlockThaumicPipe(Material material, String name) {
        super(material, name);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Pipe contains no Essentia."));
        }
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        CoordSet coordSet = new CoordSet(x, y, z);
        minX = minY = minZ = MIN;
        maxX = maxY = maxZ = MAX;

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = coordSet.copy().addForgeDirection(direction).getTileEntity(world);
            if (ThaumicHelper.isMatch(tileEntity) && canConnectTo(coordSet.getTileEntity(world), direction)) {
                switch (direction) {
                    case DOWN:
                        minY = 0.0F;
                        break;
                    case UP:
                        maxY = 1.0F;
                        break;
                    case NORTH:
                        minZ = 0.0F;
                        break;
                    case SOUTH:
                        maxZ = 1.0F;
                        break;
                    case WEST:
                        minX = 0.0F;
                        break;
                    case EAST:
                        maxX = 1.0F;
                        break;
                    default:
                        break;
                }
            }
        }

        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        super.setBlockBoundsBasedOnState(world, x, y, z);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB playerBoundingBox, List list, Entity entity) {
        CoordSet coordSet = new CoordSet(x, y, z);
        minX = minY = minZ = MIN;
        maxX = maxY = maxZ = MAX;

        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        super.addCollisionBoxesToList(world, x, y, z, playerBoundingBox, list, entity);

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = coordSet.copy().addForgeDirection(direction).getTileEntity(world);
            if (ThaumicHelper.isMatch(tileEntity) && canConnectTo(coordSet.getTileEntity(world), direction)) {
                switch (direction) {
                    case DOWN:
                        minY = 0.0F;
                        maxY = 0.5F;
                        break;
                    case UP:
                        minY = 0.5F;
                        maxY = 1.0F;
                        break;
                    case NORTH:
                        maxZ = 0.0F;
                        maxZ = -0.5F;
                        break;
                    case SOUTH:
                        maxZ = 0.5F;
                        maxZ = 1.0F;
                        break;
                    case WEST:
                        minX = 0.0F;
                        maxX = 0.5F;
                        break;
                    case EAST:
                        minX = 0.5F;
                        maxX = 1.0F;
                        break;
                    default:
                        break;
                }
                setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                minX = minY = minZ = MIN;
                maxX = maxY = maxZ = MAX;
                super.addCollisionBoxesToList(world, x, y, z, playerBoundingBox, list, entity);
            }
        }
    }

    private static boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction) {
        if (tileEntity instanceof IThaumicPipe)
            return ((IThaumicPipe) tileEntity).canConnectTo(direction);
        return false;
    }

    @Override
    public TileEntity getTileEntity() {
        return new TileThaumicPipe();
    }

    @Override
    public boolean renderWithModel() {
        return true;
    }
}
