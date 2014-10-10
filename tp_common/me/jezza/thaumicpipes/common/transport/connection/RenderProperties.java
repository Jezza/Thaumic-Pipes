package me.jezza.thaumicpipes.common.transport.connection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.thaumicpipes.api.interfaces.IThaumicPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;

@SideOnly(Side.CLIENT)
public enum RenderProperties {
    DEFAULT, ALBEMIC(0.5F), JAR, PIPE;

    private float extensionSize = 0.0F;

    RenderProperties() {
    }

    RenderProperties(float extensionSize) {
        this.extensionSize = MathHelper.clamp_float(extensionSize, 0.0F, 1.0F);
    }

    public boolean isPipe() {
        return this == PIPE;
    }

    public boolean isJar() {
        return this == JAR;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public float getExtensionSize() {
        return extensionSize;
    }

    public static RenderProperties getProperties(TileEntity tileEntity) {
        if (tileEntity instanceof IThaumicPipe)
            return PIPE;
        if (tileEntity instanceof TileJarFillable)
            return JAR;
        if (tileEntity instanceof TileAlembic)
            return ALBEMIC;
        return DEFAULT;
    }
}
