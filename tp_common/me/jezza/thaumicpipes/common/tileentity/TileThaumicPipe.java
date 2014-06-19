package me.jezza.thaumicpipes.common.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.google.common.collect.Lists;

import me.jezza.thaumicpipes.common.core.ArmState;
import me.jezza.thaumicpipes.common.core.AspectContainerList;
import me.jezza.thaumicpipes.common.core.AspectContainerList.AspectContainerState;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.utils.ThaumicHelper;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileThaumicPipe extends TileEntityTP implements IThaumicPipe, IWandable {

    private AspectList aspectList = new AspectList();
    private AspectContainerList stateList = new AspectContainerList();
    private ArmState[] armStateArray = new ArmState[6];
    private ForgeDirection priority = ForgeDirection.UNKNOWN;
    private int tickTiming = 0;
    private int timeTicked = 0;
    private boolean bigNode = false;

    @Override
    public void updateEntity() {
        if (worldObj == null)
            return;

        updateArmStates();

        // Get rid of the client side.
        if (worldObj.isRemote)
            return;

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

        // Get rid of all empty aspects in the list.
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
            armStateArray[index++] = new ArmState(dir, getCoordSet().addForgeDirection(dir).getTileEntity(worldObj), canConnectTo(dir), dir.equals(priority));
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
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

        for (IAspectContainer jar : jarList) {
            if (jar == null)
                continue;

            for (Aspect aspect : aspectList.getAspects()) {
                if (aspect == null)
                    continue;

                if (jar.doesContainerAccept(aspect)) {
                    int totalToAdd = aspectList.getAmount(aspect);
                    int leftOver = jar.addToContainer(aspect, totalToAdd);
                    aspectList.reduce(aspect, totalToAdd - leftOver);
                }
            }
        }

        return !aspectList.aspects.isEmpty();
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
        ArrayList<IAspectContainer> containerList = new ArrayList<IAspectContainer>();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(direction))
                continue;
            TileEntity tileEntity = getCoordSet().addForgeDirection(direction).getTileEntity(worldObj);
            if (ThaumicHelper.isJar(tileEntity))
                containerList.add((IAspectContainer) tileEntity);
        }

        return containerList;
    }

    private ArrayList<IAspectContainer> getNearbyAspectContainers() {
        ArrayList<IAspectContainer> containerList = new ArrayList<IAspectContainer>();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(direction))
                continue;
            TileEntity tileEntity = getCoordSet().addForgeDirection(direction).getTileEntity(worldObj);
            if (ThaumicHelper.isContainer(tileEntity))
                containerList.add((IAspectContainer) tileEntity);
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

        int direction = tag.getInteger("priority");
        priority = ForgeDirection.getOrientation(direction);
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
            if (currentState == null)
                continue;
            if (currentState.isValid())
                validDirections.add(currentState.getDirection());
        }

        int direction = priority.ordinal();

        do {
            direction++;
            if (direction == 7)
                direction = 0;
        } while (!validDirections.contains(ForgeDirection.getOrientation(direction)));

        if (direction == 6)
            priority = ForgeDirection.UNKNOWN;
        else
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
    public boolean canReceiveFrom(ForgeDirection forgeDirection) {
        return canConnectTo(forgeDirection.getOpposite()) && !forgeDirection.getOpposite().equals(priority);
    }

    @Override
    public boolean canConnectTo(ForgeDirection forgeDirection) {
        if (worldObj == null)
            return false;

        TileEntity tileEntity = getCoordSet().addForgeDirection(forgeDirection).getTileEntity(worldObj);

        if (tileEntity instanceof IFluidHandler)
            return false;

        if (ThaumicHelper.isJar(tileEntity) || ThaumicHelper.isPipe(tileEntity) || ThaumicHelper.isContainer(tileEntity))
            return true;

        if (tileEntity instanceof IEssentiaTransport)
            return ((IEssentiaTransport) tileEntity).isConnectable(forgeDirection.getOpposite());

        if (tileEntity instanceof IInventory)
            return false;

        return false;
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

        ArrayList<IAspectContainer> containerList = getNearbyJars();
        containerList.addAll(getNearbyAspectContainers());

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

        if (tempPipeList == null)
            return null;

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
}
