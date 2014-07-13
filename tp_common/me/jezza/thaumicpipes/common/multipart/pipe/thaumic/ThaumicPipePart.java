package me.jezza.thaumicpipes.common.multipart.pipe.thaumic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.external.ConnectionRegistry;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.interfaces.IPartRenderer;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.multipart.MultiPartFactory;
import me.jezza.thaumicpipes.common.multipart.OcclusionPart;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.multipart.pipe.PipeProperties;
import me.jezza.thaumicpipes.common.transport.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.TransportState;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import codechicken.lib.vec.Cuboid6;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ThaumicPipePart extends PipePartAbstract implements IThaumicPipe {

    // private LinkedHashSet<TravellingAspect> travelSet;

    @SideOnly(Side.CLIENT)
    private ThaumicPipePartRenderer renderer;

    private AspectList aspectList;

    public ThaumicPipePart() {
        // travelSet = new LinkedHashSet<TravellingAspect>();
        aspectList = new AspectList();
    }

    @Override
    public void onWorldJoin() {
        super.onWorldJoin();

        TPLogger.info(getCoordSet());
    }

    @Override
    public void onWorldSeparate() {
        super.onWorldSeparate();

        TPLogger.info(getCoordSet());
    }

    @Override
    public void update() {
        super.update();

        //

    }

    public ArrayList<TransportState> getConnectableTiles(HashSet<CoordSet> coordSets) {
        ArrayList<TransportState> tileList = new ArrayList<TransportState>();

        for (ArmState state : armStateHandler.getArmStateArray()) {
            if (state == null || !state.isValid())
                continue;
            CoordSet tempSet = state.getCoordSet();
            if (coordSets.contains(tempSet))
                continue;
            coordSets.add(tempSet);
            tileList.add(state.getTransportState());
        }

        return tileList;
    }

    public void updateStates() {
        nodeState = armStateHandler.updateArmStates(this, world(), getCoordSet());
        occlusionTester.updateWithArmStates(armStateHandler.getArmStateArray(), nodeState);
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    public ArmState[] getArmStateArray() {
        return armStateHandler.getArmStateArray();
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
    public AspectList getAspectList() {
        return aspectList;
    }

    @Override
    public boolean addAspect(Aspect aspect, int amount, ForgeDirection forgeDirection) {
        aspectList.add(aspect, amount);
        return aspectList.aspects.containsKey(aspect);
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
    public boolean canReceiveFrom(ForgeDirection direction) {
        direction = direction.getOpposite();
        return !armStateHandler.isPriority(direction) && canConnectTo(direction);
    }

    @Override
    public boolean canConnectTo(ForgeDirection direction) {
        if (direction == ForgeDirection.UNKNOWN)
            return false;

        TileEntity tileEntity = getCoordSet().getTileFromDirection(world(), direction);
        OcclusionPart part = new OcclusionPart(PipeProperties.ARM_STATE_OCCLUSION_BOXES[direction.ordinal()]);
        OcclusionPart oppositePart = new OcclusionPart(PipeProperties.ARM_STATE_OCCLUSION_BOXES[direction.getOpposite().ordinal()]);

        boolean flag = tileEntity instanceof IThaumicPipe ? bothPassOcclusionTest(((IThaumicPipe) tileEntity).getPipe(), part, oppositePart) : passOcclusionTest(part);
        return tileEntity != null && flag && ConnectionRegistry.isValidConnection(tileEntity, direction);
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
        if (renderer == null)
            renderer = new ThaumicPipePartRenderer();
        return renderer;
    }

    @Override
    public Iterable<Cuboid6> getAllOcclusionBoxes() {
        return occlusionTester.getOcclusionBoxes();
    }

    @Override
    public boolean activate(EntityPlayer player, MovingObjectPosition hit, ItemStack itemStack) {
        if (player.getCurrentEquippedItem() == null) {
            if (player.worldObj.isRemote)
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
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "The pipe is empty."));
            }
            return true;
        }
        return false;
    }
}
