package me.jezza.thaumicpipes.common.multipart.pipe.thaumic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.api.registry.ConnectionRegistry;
import me.jezza.thaumicpipes.client.IPartRenderer;
import me.jezza.thaumicpipes.client.renderer.multipart.ThaumicPipePartRenderer;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.grid.interfaces.INetworkHandler;
import me.jezza.thaumicpipes.common.multipart.MultiPartFactory;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.multipart.pipe.PipeProperties;
import me.jezza.thaumicpipes.common.transport.MessageHandler;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.connection.NodeState;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileJarFillable;
import codechicken.lib.vec.Cuboid6;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ThaumicPipePart extends PipePartAbstract implements IThaumicPipe {

    private ArmStateHandler armStateHandler;
    private MessageHandler messageHandler;

    @SideOnly(Side.CLIENT)
    private ThaumicPipePartRenderer renderer;

    private AspectList aspectList;

    public ThaumicPipePart() {
        armStateHandler = new ArmStateHandler();
        messageHandler = new MessageHandler();
        aspectList = new AspectList();
    }

    @Override
    public void update() {
        super.update();
        // @formatter:off
        /**
         * Start with sources?
         * Can you pull out of it?
         * add to ping list.
         * 
         * Identify what aspects are currently waiting to be processed.
         * 
         * Process necessary TAs
         * Eg, Ones waiting to be added.
         * 
         * Don't pull out of LOWEST when LOWEST is the requester
         * Don't process LOWEST requests UNLESS there is a LOWER or higher source.
         */
        // @formatter:on

        messageHandler.processMessages(this);
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    public ArmState[] getArmStateArray() {
        return armStateHandler.getArmStateArray();
    }

    public ArmStateHandler getArmStateHandler() {
        return armStateHandler;
    }

    @Override
    public INetworkHandler getNetworkHandler() {
        return messageHandler;
    }

    @Override
    public void updateStates() {
        super.updateStates();
        nodeState = armStateHandler.updateArmStates(this, getTileCache());
        occlusionTester.updateWithArmStates(armStateHandler.getArmStateArray(), nodeState);
    }

    public ArrayList<TileEntity> getSourceTiles(HashSet<CoordSet> coordSets) {
        if (coordSets == null)
            coordSets = new HashSet<CoordSet>();
        ArrayList<TileEntity> tileList = new ArrayList<TileEntity>();

        for (ArmState state : armStateHandler.getSourceConnections()) {
            CoordSet tempSet = state.getCoordSet();
            if (coordSets.contains(tempSet))
                continue;
            coordSets.add(tempSet);
            tileList.add(state.getTileEntity());
        }

        return tileList;
    }

    public ArrayList<TileEntity> getRequesterTiles(HashSet<CoordSet> coordSets) {
        if (coordSets == null)
            coordSets = new HashSet<CoordSet>();
        ArrayList<TileEntity> tileList = new ArrayList<TileEntity>();

        for (ArmState state : armStateHandler.getRequesterConnections()) {
            CoordSet tempSet = state.getCoordSet();
            if (coordSets.contains(tempSet))
                continue;
            coordSets.add(tempSet);
            tileList.add(state.getTileEntity());
        }

        return tileList;
    }

    public ArrayList<TileEntity> getPipeTiles(HashSet<CoordSet> coordSets) {
        if (coordSets == null)
            coordSets = new HashSet<CoordSet>();
        ArrayList<TileEntity> tileList = new ArrayList<TileEntity>();

        for (ArmState state : armStateHandler.getPipeConnections()) {
            CoordSet tempSet = state.getCoordSet();
            if (coordSets.contains(tempSet))
                continue;
            coordSets.add(tempSet);
            tileList.add(state.getTileEntity());
        }

        return tileList;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Arrays.asList(PipeProperties.getNode());
    }

    @Override
    public String getType() {
        return MultiPartFactory.thaumicPipe;
    }

    @Override
    public Block getBlock() {
        return ModBlocks.thaumicPipe;
    }

    @Override
    public ItemStack getStack() {
        return new ItemStack(ModItems.thaumicPipe);
    }

    @Override
    public boolean canConnectTo(ForgeDirection direction) {
        if (direction == ForgeDirection.UNKNOWN)
            return false;

        TileEntity tileEntity = getTileCache()[direction.ordinal()];
        if (tileEntity == null)
            return false;
        OcclusionPart part = new OcclusionPart(PipeProperties.ARM_STATE_OCCLUSION_BOXES[direction.ordinal()]);
        OcclusionPart oppositePart = new OcclusionPart(PipeProperties.ARM_STATE_OCCLUSION_BOXES[direction.getOpposite().ordinal()]);

        boolean flag = tileEntity instanceof IThaumicPipe ? bothPassOcclusionTest(((IThaumicPipe) tileEntity).getPipe(), part, oppositePart) : passOcclusionTest(part);
        return tileEntity != null && flag && isConnectable(tileEntity, direction);
    }

    private boolean isConnectable(TileEntity tileEntity, ForgeDirection direction) {
        if (tileEntity instanceof IThaumicPipe)
            return true;

        boolean flag = ConnectionRegistry.isValidConnection(tileEntity);

        if (flag) {
            if (tileEntity instanceof TileJarFillable)
                return true;
            if (tileEntity instanceof IEssentiaTransport)
                return ((IEssentiaTransport) tileEntity).isConnectable(direction.getOpposite());
        }

        return flag;
    }

    @Override
    public void drain() {
        aspectList = new AspectList();
    }

    @Override
    public PipePartAbstract getPipe() {
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IPartRenderer getRenderer() {
        return renderer == null ? renderer = new ThaumicPipePartRenderer() : renderer;
    }

    @Override
    public Iterable<Cuboid6> getAllOcclusionBoxes() {
        return occlusionTester.getOcclusionBoxes();
    }

    @Override
    public boolean activate(EntityPlayer player, MovingObjectPosition hit, ItemStack itemStack) {
        if (!player.worldObj.isRemote) {
            messageHandler.sendPing(getCoordSet());
            TPLogger.info(getCoordSet());
        }
        if (player.getCurrentEquippedItem() == null) {
            if (player.worldObj.isRemote)
                return true;

            if (player.isSneaking()) {
                drain();
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Pipe was drained."));
            } else {
                boolean empty = true;
                for (Aspect aspect : aspectList.getAspects()) {
                    if (aspect == null)
                        continue;
                    empty = false;
                    player.addChatMessage(new ChatComponentText("Contains " + aspectList.getAmount(aspect) + " " + aspect.getName()));
                }
                if (empty)
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "The pipe is empty."));
            }
            return true;
        }
        return false;
    }
}
