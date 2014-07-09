package me.jezza.thaumicpipes.common.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.common.core.external.ThaumcraftHelper;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.utils.TimeTicker;
import me.jezza.thaumicpipes.common.core.utils.TimeTickerF;
import me.jezza.thaumicpipes.common.interfaces.IBlockInteract;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.lib.Reference;
import me.jezza.thaumicpipes.common.transport.ArmState;
import me.jezza.thaumicpipes.common.transport.AspectContainerList;
import me.jezza.thaumicpipes.common.transport.connection.TileEntityWrapper;
import me.jezza.thaumicpipes.common.transport.connection.TransportState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import cofh.api.block.IDismantleable;

import com.google.common.collect.HashMultimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileThaumicPipe extends TileTP implements IThaumicPipe, IWandable, IDismantleable, IBlockInteract {

    private AspectList aspectList = new AspectList();
    private AspectContainerList stateList = new AspectContainerList();

    // Client side for the render state
    private ArmState[] armStateArray = new ArmState[6];
    private ForgeDirection priority = ForgeDirection.UNKNOWN;

    private NodeState nodeState = null;

    private TimeTickerF priorityFrame;
    private TimeTicker constructs;
    private TimeTicker pipes;
    private TimeTicker priorityPosition;

    public TileThaumicPipe() {
        constructs = new TimeTicker(0, 10);
        pipes = new TimeTicker(0, 10);
        priorityPosition = new TimeTicker(0, 24);
        priorityFrame = new TimeTickerF(0.0F, Reference.PIPE_ANIMATION_SIZE).setStepAmount(0.8F);
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            updateArmStates();
            nodeState = NodeState.createNodeState(armStateArray);
            priorityPosition.tick();
            priorityFrame.tick();
            return;
        }

        // Process the travelling aspects

        /**
         * Should I put the TAs into a secondary loop or put it in the ticking loop.
         * 
         * I should add to the list, and THEN, parse it through the ticking loop.
         * 
         * Does it have any TAs to process?
         * 
         * If true Check nearby for sources that haven't been checked. Add to the pinged set.
         * 
         * 
         * If false
         */

        // Identify who sent the ping.

        // processNearbyConstructs();

        processNearbySources();

        // Handle checking and sending to jars, if leftovers, returns true and passes to next pipe.
        if (processPossibleJars() && pipes.tick()) {
            // Passes next pipes along the line.
            // Note: Will always try to send to priority, if can't manage, will not do anything.
            processPossiblePipes();
        }

        if (aspectList == null)
            aspectList = new AspectList();

        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == null)
                continue;

            int amount = aspectList.getAmount(aspect);
            if (amount <= 0)
                aspectList.remove(aspect);
        }
    }

    private void updateArmStates() {
        int index = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            armStateArray[index++] = new ArmState(dir, getCoordSet().addForgeDirection(dir).getTileEntity(worldObj), canConnectTo(dir), dir.equals(priority), priorityPosition.getAmount());
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    public int getAnimationFrame() {
        return (int) priorityFrame.getAmount();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    @Override
    public void drain() {
        aspectList = new AspectList();
    }

    private void reduceAndAddTo(Aspect aspect, TransportState state) {
        IAspectContainer container = state.getContainer();
        if (!container.doesContainerAccept(aspect))
            return;

        int totalToAdd = aspectList.getAmount(aspect);
        int leftOver = container.addToContainer(aspect, totalToAdd);
        aspectList.reduce(aspect, totalToAdd - leftOver);
    }

    private void addFromAndReduce(TransportState transportState) {
        AspectList tempList = transportState.getAspects();

        if (tempList == null || tempList.aspects.isEmpty())
            return;

        for (Aspect tempAspect : tempList.getAspects()) {
            if (tempAspect == null)
                continue;

            // int amount = tempList.getAmount(tempAspect);
            int amount = 1;
            if (transportState.getContainer().takeFromContainer(tempAspect, amount))
                aspectList.add(tempAspect, amount);
        }
    }

    private void processNearbyConstructs() {
        if (!constructs.tick())
            return;

        ArrayList<TransportState> constructStates = getNearbyConstructs();
        if (constructStates == null)
            return;

        if (!constructStates.isEmpty())
            for (TransportState state : constructStates) {
                if (!state.getType().isConstruct())
                    continue;
                ForgeDirection direction = state.getDirection().getOpposite();
                IEssentiaTransport transport = state.getTransport();

                Aspect requiredAspect = transport.getSuctionType(direction);

                if (requiredAspect == null)
                    continue;

                AspectContainerList tempList = ping(requiredAspect, new LinkedHashSet<CoordSet>());

                if (tempList != null)
                    for (TransportState container : tempList) {
                        int amountAdded = transport.addEssentia(requiredAspect, 1, direction);
                        if (amountAdded > 0) {
                            container.removeAmount(requiredAspect, amountAdded);
                            break;
                        }
                    }
            }
    }

    private void processNearbySources() {
        ArrayList<TransportState> containerList = getNearbySources();

        for (TransportState transportState : containerList) {
            if (!transportState.getType().isAlembic())
                continue;

            addFromAndReduce(transportState);
        }
    }

    private boolean processPossibleJars() {
        ArrayList<TransportState> jarList = getNearbyJars();
        HashMultimap<Aspect, TransportState> placingMap = HashMultimap.create();

        if (jarList.isEmpty())
            return !aspectList.aspects.isEmpty();

        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == null)
                continue;

            for (TransportState jar : jarList)
                if (jar.getContainer().doesContainerContainAmount(aspect, 0))
                    placingMap.put(aspect, jar);

            int amountToAdd = aspectList.getAmount(aspect);
            for (TransportState container : placingMap.get(aspect))
                reduceAndAddTo(aspect, container);

            for (TransportState jar : jarList)
                if (jar.getContainer().doesContainerAccept(aspect))
                    reduceAndAddTo(aspect, jar);
        }

        return !aspectList.aspects.isEmpty();
    }

    private void processPossiblePipes() {
        TransportState pipeState = getNearbyPipe();
        if (pipeState == null || !pipeState.getType().isPipe())
            return;

        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == null)
                continue;

            int totalToAdd = aspectList.getAmount(aspect);

            if (pipeState.getPipe().receiveAspect(aspect, totalToAdd, pipeState.getDirection().getOpposite()))
                aspectList.reduce(aspect, totalToAdd);
        }
    }

    public ArrayList<TileEntityWrapper> getConnectableTiles(HashSet<CoordSet> coordSets) {
        ArrayList<TileEntityWrapper> tileList = new ArrayList<TileEntityWrapper>();
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            if (canConnectTo(direction)) {
                CoordSet coordSet = getCoordSet().addForgeDirection(direction);
                if (coordSets.contains(coordSet))
                    continue;
                coordSets.add(coordSet);
                TileEntity tileEntity = coordSet.getTileEntity(worldObj);
                tileList.add(new TileEntityWrapper(tileEntity, direction));
            }
        return tileList;
    }

    private ArrayList<TransportState> getNearbyJars() {
        return getNearbyJars(new HashSet<CoordSet>());
    }

    private ArrayList<TransportState> getNearbySources() {
        return getNearbySources(new HashSet<CoordSet>());
    }

    private ArrayList<TransportState> getNearbyPipes() {
        return getNearbyPipes(new HashSet<CoordSet>());
    }

    private ArrayList<TransportState> getNearbyConstructs() {
        return getNearbyConstructs(new HashSet<CoordSet>());
    }

    private ArrayList<TransportState> getNearbyJars(HashSet<CoordSet> coordSets) {
        ArrayList<TransportState> containerList = new ArrayList<TransportState>();

        for (TileEntityWrapper wrapper : getConnectableTiles(coordSets))
            if (ThaumcraftHelper.isJarFillable(wrapper.tileEntity))
                containerList.add(new TransportState(wrapper.tileEntity).setDirection(wrapper.direction));

        return containerList;
    }

    private ArrayList<TransportState> getNearbySources(HashSet<CoordSet> coordSets) {
        ArrayList<TransportState> containerList = new ArrayList<TransportState>();

        for (TileEntityWrapper wrapper : getConnectableTiles(coordSets))
            if (ThaumcraftHelper.isSource(wrapper.tileEntity))
                containerList.add(new TransportState(wrapper.tileEntity).setDirection(wrapper.direction));

        return containerList;
    }

    private ArrayList<TransportState> getNearbyConstructs(HashSet<CoordSet> coordSets) {
        ArrayList<TransportState> constructList = new ArrayList<TransportState>();

        for (TileEntityWrapper wrapper : getConnectableTiles(coordSets))
            if (ThaumcraftHelper.isAlchemicalConstruct(wrapper.tileEntity))
                constructList.add(new TransportState(wrapper.tileEntity).setDirection(wrapper.direction));

        if (constructList.isEmpty())
            return null;

        return constructList;
    }

    private ArrayList<TransportState> getNearbyPipes(HashSet<CoordSet> coordSets) {
        ArrayList<TransportState> pipeList = new ArrayList<TransportState>();

        for (TileEntityWrapper wrapper : getConnectableTiles(coordSets))
            if (ThaumcraftHelper.isThaumicPipe(wrapper.tileEntity)) {
                IThaumicPipe pipe = (IThaumicPipe) wrapper.tileEntity;
                ForgeDirection direction = wrapper.direction;
                if (pipe.canReceiveFrom(direction))
                    pipeList.add(new TransportState(wrapper.tileEntity).setDirection(direction));
            }

        return pipeList;
    }

    private TransportState getNearbyPipe() {
        if (priority != ForgeDirection.UNKNOWN) {
            if (canConnectTo(priority)) {
                TileEntity tileEntity = getCoordSet().addForgeDirection(priority).getTileEntity(worldObj);
                if (ThaumcraftHelper.isThaumicPipe(tileEntity)) {
                    IThaumicPipe pipe = (IThaumicPipe) tileEntity;
                    if (pipe.canReceiveFrom(priority))
                        return new TransportState(tileEntity).setDirection(priority);
                }
            }
            cyclePriorityState();
            return getNearbyPipe();
        }

        ArrayList<TransportState> pipeList = getNearbyPipes();

        if (pipeList.isEmpty())
            return null;

        return pipeList.get(new Random().nextInt(pipeList.size()));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        aspectList.writeToNBT(tag);

        tag.setInteger("priority", priority.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        aspectList.readFromNBT(tag);

        priority = ForgeDirection.getOrientation(tag.getInteger("priority"));
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public boolean receiveAspect(Aspect aspect, int amount, ForgeDirection forgeDirection) {
        aspectList.add(aspect, amount);
        return aspectList.aspects.containsKey(aspect);
    }

    @Override
    public AspectList getAspectList() {
        return aspectList;
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        if (player.isSneaking()) {
            priority = ForgeDirection.UNKNOWN;
            return 0;
        }
        cyclePriorityState();
        return 0;
    }

    private void cyclePriorityState() {
        ArrayList<ForgeDirection> validDirections = new ArrayList<ForgeDirection>();
        validDirections.add(ForgeDirection.UNKNOWN);

        for (int i = 0; i < armStateArray.length; i++) {
            ArmState currentState = armStateArray[i];
            if (currentState != null && currentState.isValid())
                validDirections.add(currentState.getDirection());
        }

        int direction = priority.ordinal();
        do {
            if (++direction == 7)
                direction = 0;
        } while (!validDirections.contains(ForgeDirection.getOrientation(direction)));

        priority = ForgeDirection.getOrientation(direction);
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    @Override
    public boolean canReceiveFrom(ForgeDirection direction) {
        direction = direction.getOpposite();
        return canConnectTo(direction) && !direction.equals(priority);
    }

    @Override
    public boolean canConnectTo(ForgeDirection direction) {
        TileEntity tileEntity = getCoordSet().addForgeDirection(direction).getTileEntity(worldObj);
        return tileEntity != null && ThaumcraftHelper.isValidConnection(tileEntity, direction);
    }

    @Override
    public AspectList removeAspect(Aspect aspect, int amount) {
        return aspectList.remove(aspect, amount);
    }

    @Override
    public boolean reduceAspect(Aspect aspect, int amount) {
        return aspectList.reduce(aspect, amount);
    }

    @Override
    public AspectContainerList ping(Aspect pingedAspect, LinkedHashSet<CoordSet> pipeList) {
        pipeList.add(getCoordSet());
        stateList.clear(pingedAspect);

        int amount = aspectList.getAmount(pingedAspect);
        if (amount > 0)
            stateList.add(this);

        ArrayList<TransportState> containerList = getNearbyJars(pipeList);
        containerList.addAll(getNearbySources(pipeList));

        if (!containerList.isEmpty()) {
            for (TransportState container : containerList) {
                AspectList tempList = container.getAspects();

                if (tempList == null || tempList.aspects.isEmpty())
                    continue;

                for (Aspect aspect : tempList.getAspects()) {
                    if (aspect == null)
                        continue;

                    if (aspect.equals(pingedAspect)) {
                        stateList.add(container);
                        break;
                    }
                }
            }
        }

        ArrayList<TransportState> tempPipeList = getNearbyPipes();

        if (!tempPipeList.isEmpty())
            for (TransportState state : tempPipeList) {
                if (!state.getType().isPipe())
                    continue;

                CoordSet tempSet = getCoordSet().addForgeDirection(state.getDirection());

                if (pipeList.contains(tempSet))
                    continue;

                AspectContainerList pipe = state.getPipe().ping(pingedAspect, pipeList);

                if (pipe != null)
                    stateList.addAll(pipe);
            }

        if (stateList.isEmpty())
            return null;

        return stateList;
    }

    @Override
    public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock) {
        ItemStack itemStack = new ItemStack(getCoordSet().getBlock(world));
        boolean flag = true;

        world.func_147480_a(x, y, z, false);
        if (returnBlock && player.inventory.addItemStackToInventory(itemStack))
            flag = false;

        if (flag)
            world.spawnEntityInWorld(new EntityItem(world, x + 0.5F, y + 0.5F, z + 0.5F, itemStack));

        return itemStack;
    }

    @Override
    public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean onActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVecX, float hitVecY, float hitVecZ) {
        if (player.getCurrentEquippedItem() != null)
            return false;

        if (world.isRemote)
            return true;

        if (player.isSneaking()) {
            drain();
        } else {
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
}
