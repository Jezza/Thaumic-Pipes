package me.jezza.thaumicpipes.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.thaumicpipes.client.lib.TextureMaps;
import me.jezza.thaumicpipes.client.model.ModelThaumicPipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class ItemThaumicPipeRenderer implements IItemRenderer {

    protected ModelThaumicPipe modelPipe;

    public ItemThaumicPipeRenderer() {
        modelPipe = new ModelThaumicPipe();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return ItemRendererHelper.BLOCK_3D != helper;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case ENTITY:
                renderThaumicPipe(0.0F, 0.25F, 0.0F, 0.3850F, type);
                break;
            case EQUIPPED:
                renderThaumicPipe(-0.1F, 1.25F, 0.95F, 0.7F, type);
                break;
            case EQUIPPED_FIRST_PERSON:
                renderThaumicPipe(0.4F, 0.5F, 0.4F, 0.5F, type);
                break;
            case INVENTORY:
                renderThaumicPipe(0.0F, -0.05F, 0.0F, 0.55F, type);
                break;
            default:
                break;
        }
    }

    private void renderThaumicPipe(float x, float y, float z, float scale, ItemRenderType type) {
        glPushMatrix();

        glTranslatef(x, y, z);

        glScalef(scale, scale, scale);

        glPushMatrix();
        // Just to resize the centre up to normal size.
        scale = 1.2F;
        glScalef(scale, scale, scale);

        TextureMaps.bindPipeTexture();
        modelPipe.renderPart("Centre");
        glPopMatrix();

        modelPipe.renderVerticalParts();

        glPopMatrix();
    }

}
