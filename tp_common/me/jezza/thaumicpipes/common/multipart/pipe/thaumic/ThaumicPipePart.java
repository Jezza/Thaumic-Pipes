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
import me.jezza.thaumicpipes.common.transport.messages.NetworkMessageJar;
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

    private int prevSize = -1;
    private int[] timeTickerValues, amounts;

    @SideOnly(Side.CLIENT)
    private ThaumicPipePartRenderer renderer;

    protected Random random;
    protected AspectList pendingAspects;

    protected ArmStateHandler armStateHandler;
    protected IMessageProcessor messageProcessor;

    // TODO Probably need to make a better method for this.
    protected TimeTicker albemicTicker, messageTicker, jarTicker;

    public ThaumicPipePart() {
        armStateHandler = new ArmStateHandler();

        pendingAspects = new AspectList();

        random = new Random();
        timeTickerValues = getTimeTickerValues();
        amounts = getAmounts();
        albemicTicker = new TimeTicker(timeTickerValues[0]);
        messageTicker = new TimeTicker(timeTickerValues[1]);
        jarTicker = new TimeTicker(timeTickerValues[2]);
    }

    /**
     * [0] = albemicTicker;
     * [1] = messageTicker;
     * [2] = jarTicker;
     */
    public int[] getTimeTickerValues() {
        return new int[]{5, 10, 10};
    }

    /**
     * [0] = withdrawSpeed.
     * [1] = depositSpeed.
     */
    public int[] getAmounts() {
        return new int[]{1, 10};
    }

    /**
     * Change in withdraw over time.
     */
    public int getWithdrawVariance() {
        return 0;
    }

    /**
     * Change in deposit over time.
     */
    public int getDepositVariance() {
        return random.nextInt(5) - 2;
    }

    private int getWithdrawSpeed() {
        return amounts[0] + getWithdrawVariance();
    }

    private int getDepositSpeed() {
        return amounts[1] + getDepositVariance();
    }

    @Override
    public void update() {
        super.update();
        if (world().isRemote)
            return;

        if (albemicTicker.tick())
            processAlbemicConnections();

        if (pendingAspects.size() > 0 && messageTicker.tick())
            sendAlembicMessages();

        if (jarTicker.tick())
            processJarConnections();
    }

    private void processAlbemicConnections() {
        Collection<TileEntity> albemicConnections = getAlbemicConnections();
        if (albemicConnections.isEmpty())
            return;

        for (TileEntity tileEntity : albemicConnections) {
            TileAlembic alembic = (TileAlembic) tileEntity;
            Aspect aspect = alembic.aspect;
            int withdrawAmount = getWithdrawSpeed();
            if (alembic.takeFromContainer(aspect, withdrawAmount))
                pendingAspects.add(aspect, withdrawAmount);
        }
    }

    private void sendAlembicMessages() {
        Aspect[] aspects = pendingAspects.getAspects();
        for (int i = 0; i < aspects.length; i++) {
            Aspect aspect = aspects[i];
            if (aspect == null)
                continue;
            int amount = pendingAspects.getAmount(aspect);
            if (amount > 0) {
                int transportAmount = Math.min(amount, getDepositSpeed());
                messageProcessor.postMessage(new NetworkMessageAspectLocator(this, pendingAspects, aspect, transportAmount));
            }
        }
    }

    private void processJarConnections() {
        Collection<TileEntity> jarConnections = getJarConnections();
        if (jarConnections.isEmpty())
            return;

        for (TileEntity tileEntity : jarConnections) {
            TileJarFillable jar = (TileJarFillable) tileEntity;
            Aspect aspect = jar.aspect;
            if (aspect == null)
                continue;
            int amount = jar.amount;
            if (amount > 0) {
                int withdrawAmount = Math.min(amount, getWithdrawSpeed());
                messageProcessor.postMessage(new NetworkMessageJar(this, jar, aspect, withdrawAmount, jar.aspectFilter != null));
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
        CoreProperties.logger.info(getJarConnections().size());
        for (TileEntity tileEntity : getJarConnections()) {
            TileJarFillable jar = (TileJarFillable) tileEntity;
            if (jar.aspectFilter != null)
                CoreProperties.logger.info(jar.aspectFilter.getName());
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
