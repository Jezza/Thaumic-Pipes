package me.jezza.thaumicpipes.common.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.common.core.ArmState;
import me.jezza.thaumicpipes.common.core.AspectContainerList;
import me.jezza.thaumicpipes.common.core.TransportState;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import me.jezza.thaumicpipes.common.interfaces.IBlockInteract;
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

public class TileThaumicPipe extends TileEntityTP implements IThaumicPipe, IWandable, IDismantleable, IBlockInteract {

    private AspectList aspectList = new AspectList();
    private AspectContainerList stateList = new AspectContainerList();

    // Client side for the render state
    private ArmState[] armStateArray = new ArmState[6];
    private ForgeDirection priority = ForgeDirection.UNKNOWN;

    private int tickTiming = 0;
    private int timeTicked = 0;
    private int priorityPosition = 0;

    private float animationFrame = 0.0F;

    private NodeState nodeState = null;

    @Override
    public void updateEntity() {
        if (worldObj == null)
            return;

        // Get rid of the client side.
        if (worldObj.isRemote) {
            updateArmStates();
            updateNodeState();
            stepAnimation();
            return;
        }

        processNearbyConstructs();

        // Take aspects from all sources.
        processNearbySources();

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

            boolean firstValid = firstState.isValid();
            boolean secondValid = secondState.isValid();

            if (firstValid)
                count++;
            if (secondValid)
                count++;

            if (firstValid && secondValid)
                side = i;

            if (confirmArmState(firstState, secondState))
                isNode = false;
        }

        if (count != 2)
            isNode = true;

        boolean bigNode = false;
        if (isNode)
            for (ArmState armState : armStateArray) {
                if (armState == null)
                    continue;

                if (armState.getType().isBigNode()) {
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

    @Override
    public void drain() {
        aspectList = new AspectList();
    }

    private void processNearbyConstructs() {
        if (timeTicked++ > 0) {
            if (timeTicked >= 10)
                timeTicked = 0;
            return;
        }

        ArrayList<TransportState> constructStates = getNearbyConstructs();
        if (constructStates == null)
            return;

        if (!constructStates.isEmpty())
            for (TransportState state : constructStates) {
                if (!state.isConstruct())
                    continue;
                ForgeDirection direction = state.getDirection().getOpposite();
                IEssentiaTransport transport = state.getTransport();

                Aspect requiredAspect = transport.getSuctionType(direction);

                if (requiredAspect == null)
                    continue;

                AspectContainerList tempList = ping(requiredAspect, new HashSet<CoordSet>());

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
        TransportState pipeState = getNearbyPipe();
        if (pipeState == null || !pipeState.isPipe())
            return;

        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == null)
                continue;

            int totalToAdd = aspectList.getAmount(aspect);

            if (pipeState.getPipe().receiveAspect(aspect, totalToAdd, pipeState.getDirection().getOpposite()))
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

    private ArrayList<TransportState> getNearbyPipes() {
        ArrayList<TransportState> pipeList = new ArrayList<TransportState>();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(direction))
                continue;
            TileEntity tileEntity = getCoordSet().addForgeDirection(direction).getTileEntity(worldObj);
            if (ThaumicHelper.isPipe(tileEntity)) {
                IThaumicPipe pipe = (IThaumicPipe) tileEntity;
                if (pipe.canReceiveFrom(direction))
                    pipeList.add(new TransportState(pipe).setDirection(direction));
            }
        }

        if (pipeList.isEmpty())
            return null;

        return pipeList;
    }

    private TransportState getNearbyPipe() {
        if (priority != ForgeDirection.UNKNOWN && canConnectTo(priority)) {
            TileEntity tileEntity = getCoordSet().addForgeDirection(priority).getTileEntity(worldObj);
            if (ThaumicHelper.isPipe(tileEntity)) {
                IThaumicPipe pipe = (IThaumicPipe) tileEntity;
                if (pipe.canReceiveFrom(priority))
                    return new TransportState(pipe).setDirection(priority);
            }
            cyclePriorityState();
            return getNearbyPipe();
        }

        ArrayList<TransportState> pipeList = getNearbyPipes();

        if (pipeList == null || pipeList.isEmpty())
            return null;

        return pipeList.get(new Random().nextInt(pipeList.size()));
    }

    private ArrayList<TransportState> getNearbyConstructs() {
        ArrayList<TransportState> constructList = new ArrayList<TransportState>();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(direction))
                continue;
            TileEntity tileEntity = getCoordSet().addForgeDirection(direction).getTileEntity(worldObj);
            if (ThaumicHelper.isAlchemicalConstruct(tileEntity))
                constructList.add(new TransportState((IEssentiaTransport) tileEntity).setDirection(direction));
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

        ArrayList<TransportState> tempPipeList = getNearbyPipes();

        if (tempPipeList != null)
            for (TransportState state : tempPipeList) {
                if (!state.isPipe())
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
