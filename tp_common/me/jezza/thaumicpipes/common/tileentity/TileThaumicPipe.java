package me.jezza.thaumicpipes.common.tileentity;

import java.util.ArrayList;
import java.util.Random;

import me.jezza.thaumicpipes.common.core.ArmState;
import me.jezza.thaumicpipes.common.core.TPLogger;
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

public class TileThaumicPipe extends TileEntity implements IThaumicPipe, IWandable {

    AspectList aspectList = new AspectList();
    ArmState[] armStateArray = new ArmState[6];
    ForgeDirection priority = ForgeDirection.UNKNOWN;
    int tickTiming = 0;
    boolean bigNode = false;

    @Override
    public void updateEntity() {
        if (worldObj == null)
            return;

        updateArmStates();

        // Get rid of the client side, because fuck them.
        if (worldObj.isRemote)
            return;

        // Take aspects from all sources.
        processNearbySources();

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
            armStateArray[index++] = new ArmState(dir, worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ), canConnectTo(dir), dir.equals(priority));
    }

    public ArmState[] getArmStateArray() {
        return armStateArray;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    public void drain() {
        aspectList = new AspectList();
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

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(dir))
                continue;
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
            if (ThaumicHelper.isJar(tileEntity))
                containerList.add((IAspectContainer) tileEntity);
        }

        return containerList;
    }

    private ArrayList<IAspectContainer> getNearbyAspectContainers() {
        ArrayList<IAspectContainer> containerList = new ArrayList<IAspectContainer>();

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(dir))
                continue;
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
            if (ThaumicHelper.isContainer(tileEntity))
                containerList.add((IAspectContainer) tileEntity);
        }

        return containerList;
    }

    private ThaumicPipeState getNearbyPipe() {
        if (priority != ForgeDirection.UNKNOWN && canConnectTo(priority)) {
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + priority.offsetX, yCoord + priority.offsetY, zCoord + priority.offsetZ);
            if (ThaumicHelper.isPipe(tileEntity))
                return new ThaumicPipeState((IThaumicPipe) tileEntity, priority);
            return null;
        }

        ArrayList<ThaumicPipeState> pipeList = new ArrayList<ThaumicPipeState>();

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (!canConnectTo(dir))
                continue;
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
            if (ThaumicHelper.isPipe(tileEntity)) {
                IThaumicPipe pipe = (IThaumicPipe) tileEntity;
                if (pipe.canReceiveFrom(dir))
                    pipeList.add(new ThaumicPipeState(pipe, dir));
            }
        }

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
        if (forgeDirection.equals(priority))
            return false;

        aspectList.add(aspect, amount);
        return aspectList.aspects.containsKey(aspect);
    }

    public AspectList getAspects() {
        return aspectList;
    }

    public static class ThaumicPipeState {

        public IThaumicPipe pipe;;
        public ForgeDirection directionFound;

        public ThaumicPipeState(IThaumicPipe pipe, ForgeDirection directionFound) {
            this.pipe = pipe;
            this.directionFound = directionFound;
        }

    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        if (player.isSneaking()) {
            priority = ForgeDirection.UNKNOWN;
            return 0;
        }

        ArrayList<ForgeDirection> validDirections = new ArrayList<ForgeDirection>();
        validDirections.add(ForgeDirection.UNKNOWN);

        for (int i = 0; i < armStateArray.length; i++) {
            ArmState currentState = armStateArray[i];
            if (currentState == null)
                continue;
            if (currentState.isValid())
                validDirections.add(currentState.dir);
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

        return 0;
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
        return !forgeDirection.getOpposite().equals(priority) && canConnectTo(forgeDirection);
    }

    @Override
    public boolean canConnectTo(ForgeDirection forgeDirection) {
        if (worldObj == null)
            return false;

        TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);

        if (tileEntity instanceof IFluidHandler)
            return false;

        if (ThaumicHelper.isJar(tileEntity) || ThaumicHelper.isPipe(tileEntity))
            return true;

        if (tileEntity instanceof IEssentiaTransport)
            return ((IEssentiaTransport) tileEntity).isConnectable(forgeDirection.getOpposite());

        if (tileEntity instanceof IInventory)
            return false;

        if (ThaumicHelper.isContainer(tileEntity))
            return true;

        return false;
    }
}
