package me.jezza.thaumicpipes.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.common.core.ArmState;
import me.jezza.thaumicpipes.common.core.AspectContainerList;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.AspectContainerList.AspectContainerState;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.lib.Reference;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileThaumicPipe extends TileEntityTP implements IThaumicPipe, IWandable, IDismantleable {

    private AspectList aspectList = new AspectList();
    private AspectContainerList stateList = new AspectContainerList();
    private ArmState[] armStateArray = new ArmState[6];
    private ForgeDirection priority = ForgeDirection.UNKNOWN;

    private int tickTiming = 0;
    private int timeTicked = 0;
    private int priorityPosition = 0;

    private float animationFrame = 0.0F;

    private NodeState nodeState;

    public TileThaumicPipe() {
        nodeState = null;
    }

    @Override
    public void updateEntity() {
        if (worldObj == null)
            return;

        updateArmStates();

        // Get rid of the client side.
        if (worldObj.isRemote) {
            updateNodeState();
            stepAnimation();
            return;
        }

        processNearbyConstructs();

        // Take aspects from all sources.
        processNearbySources();

        if (timeTicked > 10)
            timeTicked = 0;

        // Handle checking and sending to jars, if leftovers, returns true and passes to next pipe.
        if (processPossibleJars() && ++tickTiming > 10) {
            tickTiming = 0;
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

    private void updateNodeState() {
        boolean isNode = true;
        int count = 0;
        int side = 0;

        for (int i = 0; i <= 5; i += 2) {
            ArmState firstState = armStateArray[i];
            ArmState secondState = armStateArray[i + 1];

            boolean flag2 = firstState.isValid();
            boolean flag3 = secondState.isValid();

            if (flag2)
                count++;
            if (flag3)
                count++;

            if (flag2 && flag3)
                side = i;

            if (confirmArmState(firstState, secondState))
                isNode = false;
        }

        if (count > 2)
            isNode = true;

        boolean bigNode = false;
        for (ArmState armState : armStateArray) {
            if (armState == null)
                continue;

            if (armState.getConnectionState().getType().isBigNode()) {
                bigNode = true;
                break;
            }
        }

        nodeState = new NodeState(isNode, bigNode, side);
    }

    private void stepAnimation() {
        if (++priorityPosition > 24)
            priorityPosition = 0;

        animationFrame += 0.8F;
        if (animationFrame >= Reference.PIPE_ANIMATION_SIZE)
            animationFrame = 0.0F;
    }

    private void sendAnimationEffect() {
        TileEntity tileEntity = getCoordSet().addForgeDirection(priority).getTileEntity(worldObj);
        if (tileEntity instanceof TileThaumicPipe)
            ((TileThaumicPipe) tileEntity).receiveAnimationEffect(priority);
    }

    private void receiveAnimationEffect(ForgeDirection priority) {
        // animationStates.get(priority.ordinal()).receiveAnimationPosition();
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    private void updateArmStates() {
        int index = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            armStateArray[index++] = new ArmState(dir, getCoordSet().addForgeDirection(dir).getTileEntity(worldObj), canConnectTo(dir), dir.equals(priority), priorityPosition);
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    public int getAnimationFrame() {
        return (int) animationFrame;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    private boolean confirmArmState(ArmState firstState, ArmState secondState) {
        return firstState.isValid() && secondState.isValid() && firstState.getDirection().getOpposite().equals(secondState.getDirection());
    }

    public void drain() {
        aspectList = new AspectList();
    }

    private void processNearbyConstructs() {
        if (timeTicked++ > 0)
            return;

        ArrayList<ConstructState> constructStates = getNearbyConstructs();
        if (constructStates == null)
            return;

        if (!constructStates.isEmpty())
            for (ConstructState state : constructStates) {
                ForgeDirection direction = state.directionFound.getOpposite();
                IEssentiaTransport transport = state.transport;

                Aspect requiredAspect = transport.getSuctionType(direction);

                if (requiredAspect == null)
                    continue;

                AspectContainerList tempList = ping(requiredAspect, new HashSet<CoordSet>());

                if (tempList != null)
                    for (AspectContainerState container : tempList) {
                        int amountAdded = transport.addEssentia(requiredAspect, 1, direction);
                        if (amountAdded > 0) {
                            container.removeAmount(amountAdded);
                            break;
                        }
                    }
            }
    }

    private void processNearbySources() {
        ArrayList<IAspectContainer> containerList = getNearbyAspectContainers();

        for (IAspectContainer container : containerList) {
            if (container == null)
                continue;

            AspectList tempList = container.getAspects();

            if (tempList == null)
                continue;

            if (tempList.aspects.isEmpty())
                continue;

            for (Aspect aspect : tempList.getAspects()) {
                if (aspect == null)
                    continue;

                int amount = tempList.getAmount(aspect);
                if (container.takeFromContainer(aspect, amount))
                    aspectList.add(aspect, amount);
            }
        }
    }

    private boolean processPossibleJars() {
        ArrayList<IAspectContainer> jarList = getNearbyJars();
        HashMultimap<Aspect, IAspectContainer> placingMap = HashMultimap.create();

        if (jarList.isEmpty())
            return !aspectList.aspects.isEmpty();

        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == null)
                continue;

            for (IAspectContainer jar : jarList)
                if (jar.doesContainerContainAmount(aspect, 0))
                    placingMap.put(aspect, jar);

            int amountToAdd = aspectList.getAmount(aspect);
            for (IAspectContainer container : placingMap.get(aspect))
                reduceAndAddTo(aspect, container);

            for (IAspectContainer jar : jarList)
                if (jar.doesContainerAccept(aspect))
                    reduceAndAddTo(aspect, jar);
        }

        return !aspectList.aspects.isEmpty();
    }

    private void reduceAndAddTo(Aspect aspect, IAspectContainer container) {
        if (!container.doesContainerAccept(aspect))
            return;

        int totalToAdd = aspectList.getAmount(aspect);
        int leftOver = container.addToContainer(aspect, totalToAdd);
        aspectList.reduce(aspect, totalToAdd - leftOver);
    }

    private void processPossiblePipes() {
        ThaumicPipeState pipeState = getNearbyPipe();
        if (pipeState == null)
            return;

        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == null)
                continue;

            int totalToAdd = aspectList.getAmount(aspect);

            if (pipeState.pipe.receiveAspect(aspect, totalToAdd, pipeState.directionFound.getOpposite()))
                aspectList.reduce(aspect, totalToAdd);
        }
    }

    private ArrayList<IAspectContainer> getNearbyJars() {
        return getNearbyJars(new HashSet<CoordSet>());
    }

    private ArrayList<IAspectContainer> getNearbyJars(HashSet<CoordSet> coordSets) {
        ArrayList<IAspectContainer> containerList = new ArrayList<IAspectContainer>();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(direction))
                continue;
            CoordSet coordSet = getCoordSet().addForgeDirection(direction);
            TileEntity tileEntity = coordSet.getTileEntity(worldObj);
            if (ThaumicHelper.isJar(tileEntity)) {
                if (coordSets.contains(coordSet))
                    continue;

                coordSets.add(coordSet);
                containerList.add((IAspectContainer) tileEntity);
            }
        }

        return containerList;
    }

    private ArrayList<IAspectContainer> getNearbyAspectContainers() {
        return getNearbyAspectContainers(new HashSet<CoordSet>());
    }

    private ArrayList<IAspectContainer> getNearbyAspectContainers(HashSet<CoordSet> coordSets) {
        ArrayList<IAspectContainer> containerList = new ArrayList<IAspectContainer>();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(direction))
                continue;
            CoordSet coordSet = getCoordSet().addForgeDirection(direction);
            TileEntity tileEntity = coordSet.getTileEntity(worldObj);
            if (ThaumicHelper.isContainer(tileEntity)) {
                if (coordSets.contains(coordSet))
                    continue;

                coordSets.add(coordSet);
                containerList.add((IAspectContainer) tileEntity);
            }
        }

        return containerList;
    }

    private ArrayList<ThaumicPipeState> getNearbyPipes() {
        ArrayList<ThaumicPipeState> pipeList = new ArrayList<ThaumicPipeState>();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(direction))
                continue;
            TileEntity tileEntity = getCoordSet().addForgeDirection(direction).getTileEntity(worldObj);
            if (ThaumicHelper.isPipe(tileEntity)) {
                IThaumicPipe pipe = (IThaumicPipe) tileEntity;
                if (pipe.canReceiveFrom(direction))
                    pipeList.add(new ThaumicPipeState(pipe, direction));
            }
        }

        if (pipeList.isEmpty())
            return null;

        return pipeList;
    }

    private ThaumicPipeState getNearbyPipe() {
        if (priority != ForgeDirection.UNKNOWN && canConnectTo(priority)) {
            TileEntity tileEntity = getCoordSet().addForgeDirection(priority).getTileEntity(worldObj);
            if (ThaumicHelper.isPipe(tileEntity)) {
                IThaumicPipe pipe = (IThaumicPipe) tileEntity;
                if (pipe.canReceiveFrom(priority))
                    return new ThaumicPipeState(pipe, priority);
            }
            cyclePriorityState();
            return getNearbyPipe();
        }

        ArrayList<ThaumicPipeState> pipeList = getNearbyPipes();

        if (pipeList == null || pipeList.isEmpty())
            return null;

        return pipeList.get(new Random().nextInt(pipeList.size()));
    }

    private ArrayList<ConstructState> getNearbyConstructs() {
        ArrayList<ConstructState> constructList = new ArrayList<ConstructState>();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(direction))
                continue;
            TileEntity tileEntity = getCoordSet().addForgeDirection(direction).getTileEntity(worldObj);
            if (ThaumicHelper.isAlchemicalConstruct(tileEntity)) {
                constructList.add(new ConstructState((IEssentiaTransport) tileEntity, direction));
            }
        }

        if (constructList.isEmpty())
            return null;

        return constructList;
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
        return canConnectTo(direction.getOpposite()) && !direction.getOpposite().equals(priority);
    }

    @Override
    public boolean canConnectTo(ForgeDirection direction) {
        if (worldObj == null)
            return false;
        return ThaumicHelper.isValidConnection(getCoordSet().addForgeDirection(direction).getTileEntity(worldObj), direction);
    }

    @Override
    public AspectContainerList getContainerState() {
        return stateList;
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
    public AspectContainerList ping(Aspect pingedAspect, HashSet<CoordSet> pipeList) {
        pipeList.add(getCoordSet());
        stateList.clear(pingedAspect);

        int amount = aspectList.getAmount(pingedAspect);
        if (amount > 0)
            stateList.add(this);

        ArrayList<IAspectContainer> containerList = getNearbyJars(pipeList);
        containerList.addAll(getNearbyAspectContainers(pipeList));

        if (!containerList.isEmpty()) {
            for (IAspectContainer container : containerList) {
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

        ArrayList<ThaumicPipeState> tempPipeList = getNearbyPipes();

        if (tempPipeList != null)
            for (ThaumicPipeState state : tempPipeList) {
                CoordSet tempSet = getCoordSet().addForgeDirection(state.directionFound);

                if (pipeList.contains(tempSet))
                    continue;

                AspectContainerList pipe = state.pipe.ping(pingedAspect, pipeList);

                if (pipe != null)
                    stateList.addAll(pipe);
            }

        if (stateList.isEmpty())
            return null;

        return stateList;
    }

    private static class ConstructState {

        public IEssentiaTransport transport;
        public ForgeDirection directionFound;

        public ConstructState(IEssentiaTransport transport, ForgeDirection directionFound) {
            this.transport = transport;
            this.directionFound = directionFound;
        }

    }

    private static class ThaumicPipeState {

        public IThaumicPipe pipe;
        public ForgeDirection directionFound;

        public ThaumicPipeState(IThaumicPipe pipe, ForgeDirection directionFound) {
            this.pipe = pipe;
            this.directionFound = directionFound;
        }
    }

    @Override
    public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock) {
        ItemStack itemStack = new ItemStack(getBlockType());
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
}
