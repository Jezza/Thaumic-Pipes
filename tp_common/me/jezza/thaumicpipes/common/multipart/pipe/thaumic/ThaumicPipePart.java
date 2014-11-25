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
import me.jezza.thaumicpipes.common.lib.CoreProperties;
import me.jezza.thaumicpipes.common.multipart.MultiPartFactory;
import me.jezza.thaumicpipes.common.multipart.occlusion.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.connection.NodeState;
import me.jezza.thaumicpipes.common.transport.messages.NetworkMessageAspectLocator;
import me.jezza.thaumicpipes.common.transport.messages.NetworkMessageLabeledJar;
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
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class ThaumicPipePart extends PipePartAbstract implements IThaumicPipe, INetworkNode {

    private int prevSize = -1;

    protected Random random;

    protected AspectList albemicList, jarLabelList;

    protected ArmStateHandler armStateHandler;
    protected IMessageProcessor messageProcessor;
    // TODO Should I make a better method for this?
    protected TimeTicker albemicTicker, messageTicker, constructTicker;

    private int[] timeTickerValues, amounts;

    @SideOnly(Side.CLIENT)
    private ThaumicPipePartRenderer renderer;

    public ThaumicPipePart() {
        armStateHandler = new ArmStateHandler();

        albemicList = new AspectList();
        jarLabelList = new AspectList();

        random = new Random();
        timeTickerValues = getTimeTickerValues();
        amounts = getAmounts();
        albemicTicker = new TimeTicker(timeTickerValues[0]);
        messageTicker = new TimeTicker(timeTickerValues[1]);
        constructTicker = new TimeTicker(timeTickerValues[2]);
    }

    /**
     * [0] = albemicTicker;
     * [1] = messageTicker;
     * [2] = constructTicker;
     */
    public int[] getTimeTickerValues() {
        return new int[]{5, 10, 20};
    }

    /**
     * Drain speed.
     * [0] = withdraw speed.
     * [1] = dump speed.
     */
    public int[] getAmounts() {
        return new int[]{1, 10};
    }

    public int getWithdrawVariance() {
        return 0;
    }

    public int getDumpVariance() {
        return random.nextInt(5) - 2;
    }

    @Override
    public void update() {
        super.update();

        if (world().isRemote)
            return;

        if (albemicTicker.tick())
            processAlbemicConnections();

        if (constructTicker.tick())
            processConstructConnections();

        if (albemicList.size() > 0)
            if (messageTicker.tick())
                sendMessagesForAlembics();

        if (jarLabelList.size() > 0)
            if (messageTicker.tick())
                sendMessagesForLabelJars();
    }

    private void processAlbemicConnections() {
        Collection<TileEntity> albemicConnections = getAlbemicConnections();
        if (albemicConnections.isEmpty() || world().isRemote)
            return;

        for (TileEntity tileEntity : albemicConnections) {
            TileAlembic alembic = (TileAlembic) tileEntity;
            Aspect aspect = alembic.aspect;
            int withdrawAmount = amounts[0] + getWithdrawVariance();
            if (alembic.takeFromContainer(aspect, withdrawAmount))
                albemicList.add(aspect, withdrawAmount);
        }
    }

    private void processConstructConnections() {
        Collection<TileEntity> constructConnections = getConstructConnections();
        if (constructConnections.isEmpty() || world().isRemote)
            return;

        for (TileEntity tileEntity : constructConnections) {
            if (tileEntity instanceof TileThaumatorium) {
                TileThaumatorium construct = (TileThaumatorium) tileEntity;
                int currentCraft = construct.currentCraft;
                Aspect currentSuction = construct.currentSuction;
            } else if (tileEntity instanceof TileThaumatoriumTop) {
                TileThaumatoriumTop construct = (TileThaumatoriumTop) tileEntity;

            }

            // TODO SEND CONSTRUCT MESSAGES
        }
    }

    private void sendMessagesForAlembics() {
        Aspect[] aspects = albemicList.getAspects();
        for (int i = 0; i < aspects.length; i++) {
            Aspect aspect = aspects[i];
            if (aspect == null)
                continue;
            int amount = albemicList.getAmount(aspect);
            if (amount > 0) {
                int transportAmount = Math.min(amount, amounts[1] + getDumpVariance());
                messageProcessor.postMessage(new NetworkMessageAspectLocator(this, albemicList, aspect, transportAmount));
            }
        }
    }

    private void sendMessagesForLabelJars() {
        Aspect[] aspects = jarLabelList.getAspects();
        for (int i = 0; i < aspects.length; i++) {
            Aspect aspect = aspects[i];
            if (aspect == null)
                continue;
            int amount = albemicList.getAmount(aspect);
            if (amount > 0) {
                messageProcessor.postMessage(new NetworkMessageLabeledJar(this, aspect, ));
            }
        }
    }

    @Override
    public void save(NBTTagCompound tag) {
    }

    @Override
    public void load(NBTTagCompound tag) {
    }

    private AspectList saveAspectList(NBTTagCompound tag, AspectList list, String id) {

    }

    @Override
    public boolean activate(EntityPlayer player, MovingObjectPosition hit, ItemStack itemStack) {
        if (player.getCurrentEquippedItem() == null) {
            if (player.worldObj.isRemote)
                return true;

            if (player.isSneaking()) {
                drain();
                addChatMessage(player, EnumChatFormatting.DARK_AQUA + "Pipe was drained.");
            } else {
                boolean empty = true;
                for (Aspect aspect : pendingAspects.getAspects()) {
                    if (aspect == null)
                        continue;
                    empty = false;
                    addChatMessage(player, EnumChatFormatting.DARK_AQUA + "Contains " + pendingAspects.getAmount(aspect) + " " + aspect.getName());
                }
                if (empty)
                    addChatMessage(player, EnumChatFormatting.DARK_AQUA + "The pipe is empty.");
            }
            return true;
        }
        return false;
    }

    private void drain() {
//        pendingAspects = new AspectList();
        TileThaumatorium construct = null;
        Collection<TileEntity> constructs = getConstructConnections();
        if (!constructs.isEmpty())
            for (TileEntity entity : constructs) {
                IAspectContainer container = (IAspectContainer) entity;
                if (container instanceof TileThaumatorium)
                    construct = (TileThaumatorium) container;
                else if (container instanceof TileThaumatoriumTop)
                    construct = ((TileThaumatoriumTop) container).thaumatorium;

                if (construct == null)
                    continue;
                CoreProperties.logger.info(construct.currentCraft);
                if (construct.currentSuction != null)
                    CoreProperties.logger.info(construct.currentSuction.getName());
            }
    }

    private void addChatMessage(EntityPlayer player, String text) {
        player.addChatComponentMessage(new ChatComponentText(text));
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

    private static enum MessagePhase {
        ALEMBIC,
        LABELED_JAR,
    }

}
