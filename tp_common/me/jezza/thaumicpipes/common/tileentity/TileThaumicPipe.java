package me.jezza.thaumicpipes.common.tileentity;

import java.util.ArrayList;

import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.utils.TimeTicker;
import me.jezza.thaumicpipes.common.core.utils.TimeTickerF;
import me.jezza.thaumicpipes.common.interfaces.IBlockInteract;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.lib.Reference;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.transport.connection.TransportState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import cofh.api.block.IDismantleable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

public class TileThaumicPipe extends TileTP implements IThaumicPipe, IDismantleable, IBlockInteract {

    private AspectList aspectList = new AspectList();

    // Client side for the render state
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

        processNearbyConstructs();

        processNearbySources();

        // Handle checking and sending to jars, if leftovers, returns true and passes to next pipe.
        if (processPossibleJars() && pipes.tick()) {
            // Passes next pipes along the line.
            // Note: Will always try to send to priority, if can't manage, will not do anything.
            processPossiblePipes();
        }
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

        ArrayList<TransportState> constructStates = Lists.newArrayList();
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

                // AspectContainerList tempList = ping(requiredAspect, new LinkedHashSet<CoordSet>());

                // if (tempList != null)
                // for (TransportState container : tempList) {
                // int amountAdded = transport.addEssentia(requiredAspect, 1, direction);
                // if (amountAdded > 0) {
                // container.removeAmount(requiredAspect, amountAdded);
                // break;
                // }
                // }
            }
    }

    private void processNearbySources() {
        ArrayList<TransportState> containerList = Lists.newArrayList();

        for (TransportState transportState : containerList) {
            if (!transportState.getType().isAlembic())
                continue;

            addFromAndReduce(transportState);
        }
    }

    private boolean processPossibleJars() {
        ArrayList<TransportState> jarList = Lists.newArrayList();
        HashMultimap<Aspect, TransportState> placingMap = HashMultimap.create();

        if (jarList.isEmpty())
            return !aspectList.aspects.isEmpty();

        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == null)
                continue;

            for (TransportState jar : jarList)
                if (jar.getContainer().doesContainerContainAmount(aspect, 0))
                    placingMap.put(aspect, jar);

            // int amountToAdd = aspectList.getAmount(aspect);
            for (TransportState container : placingMap.get(aspect))
                reduceAndAddTo(aspect, container);

            for (TransportState jar : jarList)
                if (jar.getContainer().doesContainerAccept(aspect))
                    reduceAndAddTo(aspect, jar);
        }

        return !aspectList.aspects.isEmpty();
    }

    private void processPossiblePipes() {
        // TransportState pipeState = getNearbyPipe();
        // if (pipeState == null || !pipeState.getType().isPipe())
        // return;
        //
        // for (Aspect aspect : aspectList.getAspects()) {
        // if (aspect == null)
        // continue;
        //
        // int totalToAdd = aspectList.getAmount(aspect);
        //
        // if (pipeState.getPipe().addAspect(aspect, totalToAdd, pipeState.getDirection().getOpposite()))
        // aspectList.reduce(aspect, totalToAdd);
        // }
    }

    @Override
    public void drain() {
        aspectList = new AspectList();
    }

    @Override
    public boolean addAspect(Aspect aspect, int amount, ForgeDirection forgeDirection) {
        return false;
    }

    @Override
    public AspectList getAspectList() {
        return aspectList;
    }

    @Override
    public boolean canReceiveFrom(ForgeDirection direction) {
        return false;
    }

    @Override
    public boolean canConnectTo(ForgeDirection direction) {
        return false;
    }

    @Override
    public AspectList removeAspect(Aspect aspect, int amount) {
        return aspectList.copy();
    }

    @Override
    public boolean reduceAspect(Aspect aspect, int amount) {
        return false;
    }

    // @Override
    // public AspectContainerList ping(Aspect pingedAspect, LinkedHashSet<CoordSet> pipeList) {
    // pipeList.add(getCoordSet());
    // stateList.clear(pingedAspect);
    //
    // int amount = aspectList.getAmount(pingedAspect);
    // if (amount > 0)
    // stateList.add(this);
    //
    // ArrayList<TransportState> containerList = getNearbyJars(pipeList);
    // containerList.addAll(getNearbySources(pipeList));
    //
    // if (!containerList.isEmpty()) {
    // for (TransportState container : containerList) {
    // AspectList tempList = container.getAspects();
    //
    // if (tempList == null || tempList.aspects.isEmpty())
    // continue;
    //
    // for (Aspect aspect : tempList.getAspects()) {
    // if (aspect == null)
    // continue;
    //
    // if (aspect.equals(pingedAspect)) {
    // stateList.add(container);
    // break;
    // }
    // }
    // }
    // }
    //
    // ArrayList<TransportState> tempPipeList = getNearbyPipes();
    //
    // if (!tempPipeList.isEmpty())
    // for (TransportState state : tempPipeList) {
    // if (!state.getType().isPipe())
    // continue;
    //
    // CoordSet tempSet = getCoordSet().addForgeDirection(state.getDirection());
    //
    // if (pipeList.contains(tempSet))
    // continue;
    //
    // AspectContainerList pipe = state.getPipe().ping(pingedAspect, pipeList);
    //
    // if (pipe != null)
    // stateList.addAll(pipe);
    // }
    //
    // if (stateList.isEmpty())
    // return null;
    //
    // return stateList;
    // }

    @Override
    public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock) {
        ItemStack itemStack = new ItemStack(ModItems.thaumicPipe);
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

        player.addChatMessage(new ChatComponentText("Break and replace this block."));
        player.addChatMessage(new ChatComponentText("This block needs to be converted."));
        return true;
    }

    @Override
    public PipePartAbstract getPipe() {
        return null;
    }
}
