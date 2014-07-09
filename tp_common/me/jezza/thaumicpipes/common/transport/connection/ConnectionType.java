package me.jezza.thaumicpipes.common.transport.connection;

import me.jezza.thaumicpipes.common.core.external.ModHelper;
import me.jezza.thaumicpipes.common.core.external.ThaumcraftHelper;
import me.jezza.thaumicpipes.common.core.external.ThaumicTinkerHelper;
import net.minecraft.tileentity.TileEntity;

public enum ConnectionType {
    ALEMBIC(0.5F), JAR(0.1F), PIPE(0.0F), CONSTRUCT(0.0F), REPAIRER(0.0F), UNKNOWN(-1.0F);

    private float size = 0.0F;

    private ConnectionType(float size) {
        this.size = size;
    }

    public float getSize() {
        return size;
    }

    public boolean isValid() {
        return this != UNKNOWN;
    }

    public boolean isConstruct() {
        return this == CONSTRUCT;
    }

    public boolean isAlembic() {
        return this == ALEMBIC;
    }

    public boolean isJar() {
        return this == JAR;
    }

    public boolean isPipe() {
        return this == PIPE;
    }

    public boolean isRepairer() {
        return this == REPAIRER;
    }

    public boolean isBigNode() {
        return isJar() || isAlembic() || isRepairer();
    }

    public static ConnectionType getConnectionType(TileEntity tileEntity, boolean canConnect) {
        if (!canConnect)
            return ConnectionType.UNKNOWN;

        if (ThaumcraftHelper.isThaumicPipe(tileEntity))
            return ConnectionType.PIPE;

        if (ThaumcraftHelper.isJarFillable(tileEntity))
            return ConnectionType.JAR;

        if (ThaumcraftHelper.isAlembic(tileEntity))
            return ConnectionType.ALEMBIC;

        if (ThaumcraftHelper.isAlchemicalConstruct(tileEntity))
            return ConnectionType.CONSTRUCT;

        if (ModHelper.isThaumicTinkererLoaded() && ThaumicTinkerHelper.isRepairer(tileEntity))
            return ConnectionType.REPAIRER;

        return ConnectionType.UNKNOWN;
    }
}