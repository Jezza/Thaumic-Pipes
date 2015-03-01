package me.jezza.thaumicpipes.common.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import me.jezza.oc.common.core.network.NetworkDispatcher;
import me.jezza.oc.common.utils.CoordSet;
import me.jezza.thaumicpipes.ThaumicPipes;
import net.minecraft.client.Minecraft;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;

public class AspectPacket extends NetworkDispatcher.MessageAbstract<AspectPacket, IMessage> {

    private List<CoordSet> path;
    private String aspect;

    public AspectPacket() {
    }

    public AspectPacket(List<CoordSet> path, Aspect aspect) {
        this.path = path;
        this.aspect = aspect.getTag();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, aspect);

        buf.writeInt(path.size());
        for (CoordSet set : path)
            set.writeBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        aspect = ByteBufUtils.readUTF8String(buf);

        int size = buf.readInt();
        path = new ArrayList<>();
        for (int i = 0; i < size; i++)
            path.add(CoordSet.readBytes(buf));
    }

    @Override
    public IMessage onMessage(AspectPacket message, MessageContext ctx) {
        int dimensionId = Minecraft.getMinecraft().theWorld.provider.dimensionId;
        ThaumicPipes.proxy.spawnAspectTrail(dimensionId, message.path, Aspect.getAspect(message.aspect));
        return null;
    }
}
