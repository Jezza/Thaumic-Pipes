package me.jezza.thaumicpipes.client.renderer;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;
import me.jezza.thaumicpipes.client.RenderUtils;
import me.jezza.thaumicpipes.client.model.ModelThaumicPipe;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileThaumicPipeRenderer extends TileEntitySpecialRenderer {

    ModelThaumicPipe modelThaumicPipe;

    public TileThaumicPipeRenderer() {
        modelThaumicPipe = new ModelThaumicPipe();
    }

    public void renderThaumicPipeAt(TileThaumicPipe thaumicPipe, double x, double y, double z, float tick) {
        glPushMatrix();

        glTranslated(x, y, z);

        float scale = 0.3850F;

        glScalef(scale, scale, scale);

        RenderUtils.bindTexture(TextureMaps.THAUMIC_PIPE_CENTRE[TextureMaps.THAUMIC_TEXTURE_INDEX]);

        scale = 1.33F;
        glScalef(scale, scale, scale);

        modelThaumicPipe.renderPart("Centre");
        glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
        if (tileEntity instanceof TileThaumicPipe)
            renderThaumicPipeAt((TileThaumicPipe) tileEntity, x + 0.5F, y + 0.5F, z + 0.5F, tick);
    }
}
