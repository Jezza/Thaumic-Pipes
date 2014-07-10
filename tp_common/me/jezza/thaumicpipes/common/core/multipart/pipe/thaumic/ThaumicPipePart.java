package me.jezza.thaumicpipes.common.core.multipart.pipe.thaumic;

import java.util.LinkedHashSet;

import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.common.ModBlocks;
import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.core.TPLogger;
import me.jezza.thaumicpipes.common.core.external.ThaumcraftHelper;
import me.jezza.thaumicpipes.common.core.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.core.utils.CoordSet;
import me.jezza.thaumicpipes.common.core.utils.TimeTicker;
import me.jezza.thaumicpipes.common.core.utils.TimeTickerF;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import me.jezza.thaumicpipes.common.lib.Reference;
import me.jezza.thaumicpipes.common.transport.ArmState;
import me.jezza.thaumicpipes.common.transport.ArmStateHandler;
import me.jezza.thaumicpipes.common.transport.AspectContainerList;
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
import thaumcraft.common.items.wands.ItemWandCasting;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ThaumicPipePart extends PipePartAbstract implements IThaumicPipe {

    private boolean shouldUpdate = false;

    private AspectList aspectList = new AspectList();

    private ThaumicPipePartRenderer renderer;
    private ArmStateHandler armStateHandler;
    private NodeState nodeState;

    private TimeTickerF priorityFrame;
    private TimeTicker priorityPosition;

    public ThaumicPipePart() {
        renderer = new ThaumicPipePartRenderer();
        armStateHandler = new ArmStateHandler();

        priorityPosition = new TimeTicker(0, 24);
        priorityFrame = new TimeTickerF(0.0F, Reference.PIPE_ANIMATION_SIZE).setStepAmount(0.8F);
    }

    @Override
    public void onNeighborChanged() {
        updateStates();
        if (world().isRemote)
            return;

        sendDescUpdate();
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        super.writeDesc(packet);
        packet.writeBoolean(true);
    }

    @Override
    public void readDesc(MCDataInput packet) {
        super.readDesc(packet);
        shouldUpdate = packet.readBoolean();
    }

    @Override
    public void onPartChanged(TMultiPart part) {
        updateStates();
    }

    @Override
    public void update() {
        priorityPosition.tick();
        priorityFrame.tick();

        if (shouldUpdate) {
            shouldUpdate = false;
            updateStates();
        }

        // TODO DAMN RIGHT LOGIC.
    }

    private void updateStates() {
        armStateHandler.updateArmStates(this, world(), getCoordSet(), priorityPosition.getAmount());
        nodeState = armStateHandler.createNode();
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
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Pipe contains no Essentia."));
            }
            return true;
        }

        if (itemStack != null)
            if (itemStack.getItem() instanceof ItemWandCasting) {
                if (player.isSneaking()) {
                    armStateHandler.resetPriority();
                    return true;
                }
                armStateHandler.cyclePriorityState();
                return true;
            }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vector3 pos, float frame, int pass) {
        renderer.render(this, pos.x, pos.y, pos.z, frame);
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    public ArmState[] getArmStateArray() {
        return armStateHandler.getArmStateArray();
    }

    public int getAnimationFrame() {
        return (int) priorityFrame.getAmount();
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return occlusionTester.fetchFromArmState(getArmStateArray());
    }

    @Override
    public String getType() {
        return "tp_thaumicPipe";
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
    public boolean receiveAspect(Aspect aspect, int amount, ForgeDirection forgeDirection) {
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
        return canConnectTo(direction) && !armStateHandler.isPriority(direction);
    }

    @Override
    public boolean canConnectTo(ForgeDirection direction) {
        ArmState armState = armStateHandler.getArmState(direction);
        TileEntity tileEntity = getCoordSet().addForgeDirection(direction).getTileEntity(world());

        boolean flag = tileEntity instanceof IThaumicPipe ? bothPassOcclusionTest(((IThaumicPipe) tileEntity).getPipe(), armState) : passOcclusionTest(armState);
        return tileEntity != null && ThaumcraftHelper.isValidConnection(tileEntity, direction) && flag;
    }

    @Override
    public AspectContainerList ping(Aspect pingedAspect, LinkedHashSet<CoordSet> pipeList) {
        return null;
    }

    @Override
    public void drain() {
        aspectList = new AspectList();
    }

    @Override
    public void onNeighborTileChanged(int arg0, boolean arg1) {
        updateStates();
    }

    @Override
    public boolean weakTileChanges() {
        return false;
    }

    @Override
    public PipePartAbstract getPipe() {
        return this;
    }
}
