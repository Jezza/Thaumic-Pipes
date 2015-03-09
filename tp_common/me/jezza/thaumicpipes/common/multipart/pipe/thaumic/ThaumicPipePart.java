package me.jezza.thaumicpipes.common.multipart.pipe.thaumic;

import codechicken.lib.vec.Cuboid6;
import me.jezza.oc.api.network.interfaces.IMessageProcessor;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.oc.common.core.DebugHelper;
import me.jezza.thaumicpipes.ThaumicPipes;
import me.jezza.thaumicpipes.client.interfaces.IPartRenderer;
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
import me.jezza.thaumicpipes.common.transport.messages.InformationMessage;
import me.jezza.thaumicpipes.common.transport.messages.StorageMessage;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileTube;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class ThaumicPipePart extends PipePartAbstract implements IThaumicPipe, INetworkNode {

    private int prevSize = -1;
    private int[] counts, amounts, timeTickerValues;

    protected Random random;
    protected AspectList aspects;

    protected ArmStateHandler armStateHandler;
    protected IMessageProcessor messageProcessor;

    public ThaumicPipePart() {
        armStateHandler = new ArmStateHandler();
        aspects = new AspectList();

        random = new Random();
        timeTickerValues = getTimeTickerValues();
        counts = new int[timeTickerValues.length];
        for (int i = 0; i < counts.length; i++)
            counts[i] = random.nextInt(timeTickerValues[i]);
        amounts = getAmounts();
    }

    /**
     * [0] = Input ticker; Default: 5
     * [1] = Aspect ticker; Default: 10
     * [2] = Storage ticker; Default: 10
     */
    public int[] getTimeTickerValues() {
        return new int[]{5, 10, 20};
    }

    /**
     * [0] = withdrawSpeed.
     * [1] = depositSpeed.
     */
    public int[] getAmounts() {
        return new int[]{2, 5};
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
        if (world() == null)
            return;

        super.update();
        if (world().isRemote)
            return;

        if (tick(0))
            processInputs();

        if (tick(1))
            processAspects();

        if (tick(2))
            processStorage();
    }

    private boolean tick(int index) {
        boolean flag = ++counts[index] % timeTickerValues[index] == 0;
        if (flag)
            counts[index] = 0;
        return flag;
    }

    private void processInputs() {
        Map<ForgeDirection, IEssentiaTransport> inputs = armStateHandler.getInputs();
        if (inputs.isEmpty())
            return;

        for (Map.Entry<ForgeDirection, IEssentiaTransport> entry : inputs.entrySet()) {
            IEssentiaTransport transport = entry.getValue();
            ForgeDirection direction = entry.getKey();

            int essentiaAmount = transport.getEssentiaAmount(direction);
            if (essentiaAmount <= 0)
                continue;

            Aspect essentiaType = transport.getEssentiaType(direction);
            essentiaAmount = Math.min(essentiaAmount, getWithdrawSpeed());
            int resultPulled = transport.takeEssentia(essentiaType, essentiaAmount, direction);
            if (resultPulled == 0) {
                IAspectContainer container = (IAspectContainer) transport;
                boolean flag = container.takeFromContainer(essentiaType, essentiaAmount);
                if (!flag)
                    continue;
            }
            aspects.add(essentiaType, essentiaAmount);
        }
    }

    private void processAspects() {
        if (aspects.size() > 0)
            messageProcessor.postMessage(new InformationMessage(this, aspects, getDepositSpeed()));
    }

    private void processStorage() {
        for (Map.Entry<ForgeDirection, IEssentiaTransport> entry : armStateHandler.getStorage().entrySet()) {
            IEssentiaTransport transport = entry.getValue();
            ForgeDirection direction = entry.getKey();

            Aspect type = transport.getSuctionType(null);
            if (type != null)
                messageProcessor.postMessage(new StorageMessage(this, transport, direction, type));

        }
    }

    @Override
    public void save(NBTTagCompound tag) {
        aspects.writeToNBT(tag);
    }

    @Override
    public void load(NBTTagCompound tag) {
        aspects.readFromNBT(tag);
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
                boolean debugFlag = DebugHelper.isDebug_enableChat();
                if (debugFlag) {
                    addChatMessage(player, getDescriptionString(armStateHandler.getValidConnections().values(), "Pipes", false));
                    addChatMessage(player, getDescriptionString(armStateHandler.getInputs().values(), "Inputs", false));
                    addChatMessage(player, getDescriptionString(armStateHandler.getStorage().values(), "Storages", false));
                    addChatMessage(player, getDescriptionString(armStateHandler.getOutputs().values(), "Outputs", false));
                    addChatMessage(player, EnumChatFormatting.DARK_PURPLE + "Pending Aspects:");
                }

                AspectList total = new AspectList();
                int count = 0;
                for (Aspect aspect : aspects.getAspects()) {
                    if (aspect == null)
                        continue;
                    empty = false;
                    if (ThaumcraftApiHelper.hasDiscoveredAspect(player.getCommandSenderName(), aspect))
                        total.add(aspect, aspects.getAmount(aspect));
                    else
                        count += aspects.getAmount(aspect);
                    if (debugFlag)
                        addChatMessage(player, EnumChatFormatting.DARK_AQUA + "Contains " + aspects.getAmount(aspect) + " " + aspect.getName());
                }

                if (debugFlag) {
                    addChatMessage(player, "");
                    addChatMessage(player, EnumChatFormatting.DARK_PURPLE + "Default message:");
                }

                for (Map.Entry<Aspect, Integer> entry : total.aspects.entrySet())
                    addChatMessage(player, EnumChatFormatting.DARK_AQUA + "Contains " + entry.getValue() + " " + entry.getKey().getName());

                if (count > 0) {
                    StringBuilder sb = new StringBuilder(EnumChatFormatting.DARK_AQUA.toString());
                    sb.append("Contains possibly ");
                    sb.append(count);
                    sb.append(" ");
                    if (!total.aspects.isEmpty())
                        sb.append("more ");
                    if (count == 1)
                        sb.append("of an Unknown Aspect...");
                    else
                        sb.append("Unknown Aspects...");
                    sb.append(" Probably...");
                    addChatMessage(player, sb.toString());
                }
                if (empty)
                    addChatMessage(player, EnumChatFormatting.DARK_AQUA + "The pipe is empty.");
            }
            return true;
        }
        return false;
    }

    private <T> String getDescriptionString(Collection<T> transports, String description, boolean suction) {
        StringBuilder result = new StringBuilder(EnumChatFormatting.DARK_RED.toString());
        result.append(description);
        result.append(": ");
        result.append(EnumChatFormatting.RESET);

        // TODO Suction.
        int size = transports.size();

        result.append(size);
        return result.toString();
    }

    private void drain() {
        aspects.aspects.clear();
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
            else if (size != prevSize)
                ThaumicPipes.proxy.updateNetworkNode(this);
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
        if (tileEntity == null || direction == ForgeDirection.UNKNOWN)
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
        return tileEntity instanceof IEssentiaTransport && ((IEssentiaTransport) tileEntity).isConnectable(direction.getOpposite());
    }

    @Override
    public PipePartAbstract getPipe() {
        return this;
    }

    @Override
    public ArmStateHandler getArmStateHandler() {
        return armStateHandler;
    }

    @Override
    public IPartRenderer getRenderer() {
        return new ThaumicPipePartRenderer();
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
    public Object notifyNode(int id, int process, Object... data) {
        if (id == 0)
            return getCoordSet();
        if (id == 1)
            return world();
        return null;
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
        return armStateHandler.getValidConnections().values();
    }

    @Override
    public void setIMessageProcessor(IMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }
}
