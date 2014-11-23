package me.jezza.thaumicpipes.common.multipart.pipe.thaumic;

import codechicken.lib.vec.Cuboid6;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.oc.api.interfaces.IMessageProcessor;
import me.jezza.oc.api.interfaces.INetworkNode;
import me.jezza.oc.common.utils.TimeTicker;
import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.client.IPartRenderer;
import me.jezza.thaumicpipes.client.renderer.ThaumicPipePartRenderer;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.PipeProperties;
import me.jezza.thaumicpipes.common.core.interfaces.IOcclusionPart;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.multipart.MultiPartFactory;
import me.jezza.thaumicpipes.common.multipart.occlusion.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.connection.NodeState;
import me.jezza.thaumicpipes.common.transport.messages.NetworkMessageAspectLocator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileTube;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class ThaumicPipePart extends PipePartAbstract implements IThaumicPipe, INetworkNode {

    protected TimeTicker albemicTicker, messageTicker;
    private AspectList pendingAspects;

    private int prevSize = -1;
    private IMessageProcessor messageProcessor;
    private ArmStateHandler armStateHandler;

    @SideOnly(Side.CLIENT)
    private ThaumicPipePartRenderer renderer;

    public ThaumicPipePart() {
        armStateHandler = new ArmStateHandler();
        pendingAspects = new AspectList();
        createTickers();
    }

    public void createTickers() {
        albemicTicker = new TimeTicker(0, 5);
        messageTicker = new TimeTicker(0, 10);
    }

    @Override
    public void update() {
        super.update();

        if (albemicTicker.tick())
            processAlbemicConnections();

        if (pendingAspects.size() > 0)
            if (messageTicker.tick())
                sendPotentialMessage();
    }

    public int getWithdrawAmount() {
        return 1;
    }

    public int getTransportAmount() {
        return 10;
    }

    private void processAlbemicConnections() {
        Collection<TileEntity> albemicConnections = armStateHandler.getAlbemicConnections();
        if (albemicConnections.isEmpty())
            return;

        for (TileEntity tileEntity : albemicConnections) {
            TileAlembic alembic = (TileAlembic) tileEntity;
            Aspect aspect = alembic.aspect;
            int withdrawAmount = getWithdrawAmount();
            if (alembic.takeFromContainer(aspect, withdrawAmount))
                pendingAspects.add(aspect, withdrawAmount);
        }
    }

    private void sendPotentialMessage() {
        if (!world().isRemote) {
            for (Aspect aspect : pendingAspects.getAspects()) {
                if (aspect == null)
                    continue;
                int amount = pendingAspects.getAmount(aspect);
                if (amount > 0) {
                    int randomAlteration = new Random().nextInt(6) - 3;
                    int transportAmount = Math.min(amount, getTransportAmount() + randomAlteration);
                    messageProcessor.postMessage(new NetworkMessageAspectLocator(this, aspect, transportAmount));
                }
            }
        }
    }

    @Override
    public void save(NBTTagCompound tag) {
        pendingAspects.writeToNBT(tag);
    }

    @Override
    public void load(NBTTagCompound tag) {
        pendingAspects.readFromNBT(tag);
    }

    @Override
    public boolean activate(EntityPlayer player, MovingObjectPosition hit, ItemStack itemStack) {
        if (player.getCurrentEquippedItem() == null) {
            if (player.worldObj.isRemote)
                return true;

            if (player.isSneaking()) {
                drain();
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Pipe was drained."));
            } else {
                boolean empty = true;
                for (Aspect aspect : pendingAspects.getAspects()) {
                    if (aspect == null)
                        continue;
                    empty = false;
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Contains " + pendingAspects.getAmount(aspect) + " " + aspect.getName()));
                }
                if (empty)
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "The pipe is empty."));
            }
            return true;
        }
        return false;
    }

    private void drain() {
        pendingAspects = new AspectList();
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
    public Collection<TileEntity> getJarConnections() {
        return armStateHandler.getJarConnections();
    }

    @Override
    public Collection<TileEntity> getContainerConnections() {
        return armStateHandler.getContainerConnections();
    }

    @Override
    public Collection<TileEntity> getAlbemicConnections() {
        return armStateHandler.getAlbemicConnections();
    }

    @Override
    public Collection<TileEntity> getConstructConnections() {
        return armStateHandler.getConstructConnections();
    }

    @Override
    public AspectList getPendingAspects() {
        return pendingAspects;
    }

    @Override
    public boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction) {
        if (direction == ForgeDirection.UNKNOWN || tileEntity == null)
            return false;

        OcclusionPart part = PipeProperties.getOcclusionPart(direction);
        if (tileEntity instanceof IThaumicPipe)
            return bothPassOcclusionTest(((IThaumicPipe) tileEntity).getPipe(), part, PipeProperties.getOcclusionPart(direction.getOpposite()));
        return passOcclusionTest(part) && isValidConnection(tileEntity, direction);
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
}
