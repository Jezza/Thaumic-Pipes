package me.jezza.thaumicpipes.common.multipart.pipe.thaumic;

import codechicken.lib.vec.Cuboid6;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.client.IPartRenderer;
import me.jezza.thaumicpipes.client.renderer.multipart.ThaumicPipePartRenderer;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.multipart.MultiPartFactory;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.multipart.pipe.PipeProperties;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.connection.NodeState;
import me.jezza.thaumicpipes.common.transport.connection.TransportState;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileJarFillable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ThaumicPipePart extends PipePartAbstract implements IThaumicPipe {

    private ArmStateHandler armStateHandler;

    @SideOnly(Side.CLIENT)
    private ThaumicPipePartRenderer renderer;

    private AspectList aspectList;

    public ThaumicPipePart() {
        armStateHandler = new ArmStateHandler();
        aspectList = new AspectList();
    }

    @Override
    public void update() {
        super.update();
        /**
         * Start with sources?
         * Can you pull out of it?
         * add to ping list.
         *
         * Identify what aspects are currently waiting to be processed.
         *
         * Process necessary TAs
         * Eg, Ones waiting to be added.
         *
         * Don't pull out of LOWEST when LOWEST is the requester
         * Don't process LOWEST requests UNLESS there is a LOWER or higher source.
         */

        // Pull from sources greater than LOWEST
        // pullFromImportantSources();

        // TileAlembic
        //
    }

    private void process() {
        for (ArmState armState : armStateHandler.getOtherConnections()) {
            ForgeDirection direction = armState.getDirection().getOpposite();
            TileEntity tileEntity = armState.getTileEntity();
            if (tileEntity instanceof TileJarFillable) {
                TileJarFillable tileJarFillable = (TileJarFillable) tileEntity;
                TPLogger.info("-Begin DEBUG-");
                TPLogger.info(tileJarFillable.canInputFrom(direction));
                TPLogger.info(tileJarFillable.canOutputTo(direction));
                // canInputFrom should return true.
                // canOutputTo should also return true.
                // Shouldn't worry about these calls, as 5 / 6 times it will return false regardless.


                TPLogger.info(tileJarFillable.getSuctionType(direction));
                TPLogger.info(tileJarFillable.getEssentiaType(direction));
                // If it's null, means it's empty.


                TPLogger.info(tileJarFillable.getSuctionAmount(null));
                TPLogger.info(tileJarFillable.getEssentiaAmount(null));

                TPLogger.info("-Finish DEBUG-");
                continue;
            }

            if (tileEntity instanceof IEssentiaTransport) {
                IEssentiaTransport transport = (IEssentiaTransport) tileEntity;
                Aspect aspect = transport.getSuctionType(direction);
                Aspect aspect1 = transport.getEssentiaType(direction);
                TPLogger.info(transport.canInputFrom(direction));
                TPLogger.info(transport.canOutputTo(direction));
                TPLogger.info(aspect == null ? "No suction." : "Suction type: " + aspect.getName());
                TPLogger.info(aspect1 == null ? "No essentia." : "Essentia type: " + aspect1.getName());
            }
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

    @Override
    public void save(NBTTagCompound tag) {
        aspectList.writeToNBT(tag);
    }

    @Override
    public void load(NBTTagCompound tag) {
        aspectList.readFromNBT(tag);
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    public ArmState[] getArmStateArray() {
        return armStateHandler.getArmStateArray();
    }

    public ArmStateHandler getArmStateHandler() {
        return armStateHandler;
    }

    @Override
    public void updateStates() {
        super.updateStates();
        nodeState = armStateHandler.updateArmStates(this, getTileCache());
        occlusionTester.updateWithArmStates(armStateHandler.getArmStateArray(), nodeState);
    }

    public ArrayList<TileEntity> getTiles(HashSet<CoordSet> coordSets) {
        if (coordSets == null)
            coordSets = new HashSet<CoordSet>();
        ArrayList<TileEntity> tileList = new ArrayList<TileEntity>();

//        for (ArmState state : armStateHandler.) {
//            CoordSet tempSet = state.getCoordSet();
//            if (coordSets.contains(tempSet))
//                continue;
//            coordSets.add(tempSet);
//            tileList.add(state.getTileEntity());
//        }

        return tileList;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Arrays.asList(PipeProperties.getNode());
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
    public ItemStack getStack() {
        return new ItemStack(ModItems.thaumicPipe);
    }

    @Override
    public boolean canConnectTo(ForgeDirection direction) {
        if (direction == ForgeDirection.UNKNOWN)
            return false;
        TileEntity tileEntity = getTileCache()[direction.ordinal()];
        if (tileEntity == null)
            return false;

        OcclusionPart part = PipeProperties.getOcclusionPart(direction);
        if (tileEntity instanceof IThaumicPipe)
            return bothPassOcclusionTest(((IThaumicPipe) tileEntity).getPipe(), part, PipeProperties.getOcclusionPart(direction.getOpposite()));
        return passOcclusionTest(part) && isValidConnection(tileEntity, direction);
    }

    private boolean isValidConnection(TileEntity tileEntity, ForgeDirection direction) {
        if (tileEntity instanceof TileJarFillable)
            return true;
        if (tileEntity instanceof IEssentiaTransport)
            return ((IEssentiaTransport) tileEntity).isConnectable(direction.getOpposite());
        return false;
    }

    @Override
    public void drain() {
        aspectList = new AspectList();
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
    public boolean activate(EntityPlayer player, MovingObjectPosition hit, ItemStack itemStack) {
        if (!player.worldObj.isRemote)
            process();
        return true;
//        if (player.getCurrentEquippedItem() == null) {
//            if (player.worldObj.isRemote)
//                return true;
//
//            if (player.isSneaking()) {
//                drain();
//                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Pipe was drained."));
//            } else {
//                boolean empty = true;
//                for (Aspect aspect : aspectList.getAspects()) {
//                    if (aspect == null)
//                        continue;
//                    empty = false;
//                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Contains " + aspectList.getAmount(aspect) + " " + aspect.getName()));
//                }
//                if (empty)
//                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "The pipe is empty."));
//            }
//            return true;
//        }
//        return false;
    }
}
