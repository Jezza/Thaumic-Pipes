package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.api.ThaumicRegistry;
import me.jezza.thaumicpipes.api.TileProperties;
import me.jezza.thaumicpipes.api.TileType;
import me.jezza.thaumicpipes.api.interfaces.IThaumicInput;
import me.jezza.thaumicpipes.api.interfaces.IThaumicOutput;
import me.jezza.thaumicpipes.api.interfaces.IThaumicStorage;
import me.jezza.thaumicpipes.common.core.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileTubeBuffer;

import java.util.Map;

public class ConnectionType {
    private static Map<Class<?>, TileProperties> classes;

    // @formatter:off
    public static final short DEFAULT                 = 0b1;
    public static final short PIPE                   = 0b10;
    public static final short JAR                   = 0b100;
    public static final short TRANSPORT            = 0b1000;
    public static final short TRANSPORT_EXTENDED  = 0b10000;

    public static final short INPUT             = 0b100000;
    public static final short STORAGE          = 0b1000000;
    public static final short OUTPUT          = 0b10000000;
    public static final short INVALID        = 0b100000000;

    public static final short DEFAULT_INVALID = DEFAULT | INVALID;

    // @formatter:on

    private ConnectionType() {
    }

    public static boolean isDefault(int num) {
        return (num & DEFAULT) != 0;
    }

    public static boolean isPipe(int num) {
        return (num & PIPE) != 0;
    }

    public static boolean isJar(int num) {
        return (num & JAR) != 0;
    }

    public static boolean isTransport(int num) {
        return (num & TRANSPORT) != 0;
    }

    public static boolean isTransportExtended(int num) {
        return (num & TRANSPORT_EXTENDED) != 0;
    }

    public static boolean isInput(int num) {
        return (num & INPUT) != 0;
    }

    public static boolean isStorage(int num) {
        return (num & STORAGE) != 0;
    }

    public static boolean isOutput(int num) {
        return (num & OUTPUT) != 0;
    }

    public static boolean isInvalid(int num) {
        return (num & INVALID) != 0;
    }

    public static void wasLocked() {
        if (classes != null)
            return;
        classes = ThaumicRegistry.getRegisteredClasses();
    }

    public static int getProperties(ForgeDirection direction, TileEntity tileEntity) {
        int result = 0;

        if (tileEntity == null)
            return DEFAULT_INVALID;

        if (tileEntity instanceof IThaumicPipe)
            return PIPE;
        if (tileEntity instanceof TileTubeBuffer)
            result = result | TRANSPORT_EXTENDED;
        else if (tileEntity instanceof TileJarFillable)
            result = result | JAR;
        else if (tileEntity instanceof IEssentiaTransport)
            result = result | (((IEssentiaTransport) tileEntity).renderExtendedTube() ? TRANSPORT_EXTENDED : TRANSPORT);
        else
            result = result | DEFAULT;

        TileType tileType = getTileType(direction.getOpposite(), tileEntity);

        if (tileType != null) {
            tileType = tileType.getOpposite();
            switch (tileType) {
                case INPUT:
                    result = result | INPUT;
                    break;
                case STORAGE:
                    result = result | STORAGE;
                    break;
                case OUTPUT:
                    result = result | OUTPUT;
                    break;
            }
        } else
            result = result | INVALID;
        return result;
    }

    private static TileType getTileType(ForgeDirection direction, TileEntity tileEntity) {
        Class<?> tileClazz = tileEntity.getClass();
        if (classes.containsKey(tileClazz))
            return classes.get(tileClazz).getTileType(direction);
        else {
            /**
             * The interfaces have a higher importance than the registered classes.
             * As you could wish to OUTPUT something but could extend TileJarFillable, which is registered as STORAGE.
             */
            if (IThaumicInput.class.isAssignableFrom(tileClazz))
                return TileType.INPUT;

            if (IThaumicStorage.class.isAssignableFrom(tileClazz))
                return TileType.STORAGE;

            if (IThaumicOutput.class.isAssignableFrom(tileClazz))
                return TileType.OUTPUT;

            for (Class<?> clazz : classes.keySet())
                if (clazz.isAssignableFrom(tileClazz))
                    return classes.get(clazz).getTileType(direction);
        }
        return null;
    }


}
