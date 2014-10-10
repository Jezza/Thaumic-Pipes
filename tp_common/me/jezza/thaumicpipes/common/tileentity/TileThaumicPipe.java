package me.jezza.thaumicpipes.common.tileentity;

import me.jezza.thaumicpipes.common.ModItems;
import me.jezza.thaumicpipes.common.tileentity.interfaces.IBlockInteract;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;
import cofh.api.block.IDismantleable;

public class TileThaumicPipe extends TileTP implements IDismantleable, IBlockInteract {
    private AspectList aspectList = new AspectList();

    public TileThaumicPipe() {
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

//    private boolean processPossibleJars() {
//        ArrayList<TransportState> jarList = Lists.newArrayList();
//        HashMultimap<Aspect, TransportState> placingMap = HashMultimap.create();
//
//        if (jarList.isEmpty())
//            return !aspectList.aspects.isEmpty();
//
//        for (Aspect aspect : aspectList.getAspects()) {
//            if (aspect == null)
//                continue;
//
//            for (TransportState jar : jarList)
//                if (jar.getContainer().doesContainerContainAmount(aspect, 0))
//                    placingMap.put(aspect, jar);
//
//            // int amountToAdd = aspectList.getAmount(aspect);
//            for (TransportState container : placingMap.get(aspect))
//                reduceAndAddTo(aspect, container);
//
//            for (TransportState jar : jarList)
//                if (jar.getContainer().doesContainerAccept(aspect))
//                    reduceAndAddTo(aspect, jar);
//        }
//
//        return !aspectList.aspects.isEmpty();
//    }

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
        player.addChatMessage(new ChatComponentText("This block needs to be converted."));
        player.addChatMessage(new ChatComponentText("Break and replace this block."));
        return true;
    }
}
