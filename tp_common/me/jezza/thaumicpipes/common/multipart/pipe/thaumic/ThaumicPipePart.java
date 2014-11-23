package me.jezza.thaumicpipes.common.multipart.pipe.thaumic;

import codechicken.lib.vec.Cuboid6;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.oc.api.NetworkResponse.MessageResponse;
import me.jezza.oc.api.NetworkResponse.NetworkOverride;
import me.jezza.oc.api.interfaces.IMessageProcessor;
import me.jezza.oc.api.interfaces.INetworkMessage;
import me.jezza.oc.api.interfaces.INetworkNode;
import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.client.IPartRenderer;
import me.jezza.thaumicpipes.client.renderer.ThaumicPipePartRenderer;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.PipeProperties;
import me.jezza.thaumicpipes.common.core.interfaces.IOcclusionPart;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.lib.CoreProperties;
import me.jezza.thaumicpipes.common.multipart.MultiPartFactory;
import me.jezza.thaumicpipes.common.multipart.occlusion.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.connection.NodeState;
import me.jezza.thaumicpipes.common.transport.messages.NetworkMessageTest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileTube;

import java.util.Arrays;
import java.util.Collection;

public class ThaumicPipePart extends PipePartAbstract implements IThaumicPipe, INetworkNode {

    private int prevSize = -1;
    private IMessageProcessor messageProcessor;
    private ArmStateHandler armStateHandler;

    @SideOnly(Side.CLIENT)
    private ThaumicPipePartRenderer renderer;

    public ThaumicPipePart() {
        armStateHandler = new ArmStateHandler();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void save(NBTTagCompound tag) {
        // TODO SAVE SHIT
    }

    @Override
    public void load(NBTTagCompound tag) {
        // TODO LOAD SHIT
    }

    @Override
    public boolean activate(EntityPlayer player, MovingObjectPosition hit, ItemStack item) {
        if (!world().isRemote) {

            if (messageProcessor != null) {
                messageProcessor.postMessage(new NetworkMessageTest(this));
                CoreProperties.logger.info("Message posted.");
            } else {
                CoreProperties.logger.info("messageProcessor is null");
            }

        }
        return false;
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    public ArmState[] getArmStateArray() {
        return armStateHandler.getArmStateArray();
    }

    @Override
    public void updateConnections() {
        nodeState = armStateHandler.updateArmStates(this, getTileCache());
    }

    @Override
    public void updateNetwork() {
        if (!world().isRemote) {
            int size = tile().jPartList().size();
            if (prevSize == -1)
                ThaumicPipes.proxy.addNetworkNode(this);
            else if (size != prevSize) {
                ThaumicPipes.proxy.updateNetworkNode(this);
            }
            prevSize = size;
        }
    }

    @Override
    public void onWorldSeparate() {
        if (!world().isRemote)
            ThaumicPipes.proxy.removeNetworkNode(this);
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Arrays.asList(PipeProperties.getNode());
    }

    @Override
    public boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction) {
        if (direction == ForgeDirection.UNKNOWN || tileEntity == null)
            return false;

        OcclusionPart part = PipeProperties.getOcclusionPart(direction);
        if (tileEntity instanceof IThaumicPipe)
            return bothPassOcclusionTest(((IThaumicPipe) tileEntity).getPipe(), part, PipeProperties.getOcclusionPart(direction.getOpposite()));
        return isValidConnection(tileEntity, direction) && passOcclusionTest(part);
    }

    private boolean isValidConnection(TileEntity tileEntity, ForgeDirection direction) {
        if (tileEntity instanceof TileJarFillable)
            return true;
        if (tileEntity instanceof TileTube)
            return false;
        if (tileEntity instanceof IEssentiaTransport)
            return ((IEssentiaTransport) tileEntity).isConnectable(direction.getOpposite());
        return false;
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
    public String getType() {
        return MultiPartFactory.thaumicPipe;
    }

    @Override
    public Block getBlock() {
        return ModBlocks.thaumicPipe;
    }

    @Override
    public ItemStack getDropStack() {
        return new ItemStack(ModItems.thaumicPipe);
    }

    @Override
    public IOcclusionPart[] getOcclusionParts() {
        IOcclusionPart[] occlusionParts = new IOcclusionPart[7];
        System.arraycopy(armStateHandler.getArmStateArray(), 0, occlusionParts, 0, 6);
        occlusionParts[6] = nodeState;
        return occlusionParts;
    }

    @Override
    public NetworkOverride onMessagePosted(INetworkMessage message) {
        return NetworkOverride.IGNORE;
    }

    @Override
    public NetworkOverride onMessageReceived(INetworkMessage message) {
        return NetworkOverride.IGNORE;
    }

    @Override
    public MessageResponse onMessageComplete(INetworkMessage message) {
        CoreProperties.logger.info("Fired");
        return MessageResponse.VALID;
    }

    @Override
    public Collection<INetworkNode> getNearbyNodes() {
        return armStateHandler.getValidConnections();
    }

    @Override
    public void setIMessageProcessor(IMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public IMessageProcessor getIMessageProcessor() {
        return messageProcessor;
    }

    @Override
    public boolean registerMessagePostedOverride() {
        return false;
    }
}
