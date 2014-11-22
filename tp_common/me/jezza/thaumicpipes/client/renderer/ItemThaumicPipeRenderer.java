package me.jezza.thaumicpipes.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.thaumicpipes.client.RenderUtils;
import me.jezza.thaumicpipes.client.model.ModelThaumicPipe;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class ItemThaumicPipeRenderer implements IItemRenderer {

    ModelThaumicPipe modelPipe;

    public ItemThaumicPipeRenderer() {
        modelPipe = new ModelThaumicPipe();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case ENTITY:
                renderThaumicPipe(0.0F, 0.0F, 0.0F, 0.3850F);
                break;
            case EQUIPPED:
                renderThaumicPipe(0.5F, 0.5F, 0.5F, 0.3850F);
                break;
            case EQUIPPED_FIRST_PERSON:
                renderThaumicPipe(0.5F, 0.5F, 0.5F, 0.3850F);
                break;
            case INVENTORY:
                renderThaumicPipe(0.0F, 0.0F, 0.0F, 0.3850F);
                break;
            default:
                break;
        }
    }

    private void renderThaumicPipe(float x, float y, float z, float scale) {
        glPushMatrix();

        glTranslatef(x, y, z);

        glScalef(scale, scale, scale);

        RenderUtils.bindTexture(TextureMaps.THAUMIC_PIPE_CENTRE[TextureMaps.THAUMIC_TEXTURE_INDEX]);

        glPushMatrix();
        scale = 1.2F;
        glScalef(scale, scale, scale);

        modelPipe.renderPart("Centre");
        glPopMatrix();

        RenderUtils.bindTexture(TextureMaps.THAUMIC_PIPE_ARM[TextureMaps.THAUMIC_TEXTURE_INDEX]);

        modelPipe.renderVerticalParts();

        glPopMatrix();
    }

}
