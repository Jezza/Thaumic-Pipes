package me.jezza.thaumicpipes.client.renderer;

import static org.lwjgl.opengl.GL11.*;

import me.jezza.thaumicpipes.client.RenderUtils;
import me.jezza.thaumicpipes.client.model.ModelJarConnection;
import me.jezza.thaumicpipes.client.model.ModelPipeExtension;
import me.jezza.thaumicpipes.client.model.ModelThaumicPipe;
import me.jezza.thaumicpipes.common.core.ArmState;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileThaumicPipeRenderer extends TileEntitySpecialRenderer {

    ModelThaumicPipe modelThaumicPipe;
    ModelJarConnection modelJarConnection;
    ModelPipeExtension modelPipeExtension;

    public TileThaumicPipeRenderer() {
        modelThaumicPipe = new ModelThaumicPipe();
        modelJarConnection = new ModelJarConnection();
        modelPipeExtension = new ModelPipeExtension();
    }

    public void renderThaumicPipeAt(TileThaumicPipe thaumicPipe, double x, double y, double z, float tick) {
        glPushMatrix();

        glTranslated(x, y, z);

        float scale = 0.3850F;

        glScalef(scale, scale, scale);

        RenderUtils.bindTexture(TextureMaps.THAUMIC_PIPE_CENTRE[TextureMaps.THAUMIC_TEXTURE_INDEX]);

        glPushMatrix();

        ArmState[] armSet = thaumicPipe.getArmStateArray();

        scale = 1.01F;
        if (isBigNode(armSet))
            scale += 0.32F;
        // scale -= 0.32F;

        glScalef(scale, scale, scale);

        modelThaumicPipe.renderPart("Centre");
        glPopMatrix();

        for (int i = 0; i < armSet.length; i++) {
            ArmState currentState = armSet[i];
            if (currentState == null || !currentState.isValid())
                continue;

            TextureMaps.bindPipeTexture(currentState);

            modelThaumicPipe.renderArm(i + 1);

            processPostRender(currentState, i);
        }

        glPopMatrix();
    }

    private void processPostRender(ArmState currentState, int i) {
        ForgeDirection currentDir = currentState.dir;

        float xDisplace = currentDir.offsetX;
        float yDisplace = currentDir.offsetY;
        float zDisplace = currentDir.offsetZ;

        if (currentState.isJarType()) {
            glPushMatrix();
            glTranslatef(xDisplace, yDisplace, zDisplace);

            if (currentDir != ForgeDirection.UP) {
                glPushMatrix();

                float distance = 0.4F;
                glTranslatef(currentDir.offsetX * distance, currentDir.offsetY * distance, currentDir.offsetZ * distance);

                glRotatef(90F, xDisplace, zDisplace, yDisplace);

                if (currentDir == ForgeDirection.DOWN) {
                    TextureMaps.bindBorderlessTexture(currentState);
                    modelPipeExtension.render();

                    distance = -0.20F;
                    glTranslatef(currentDir.offsetY * distance, currentDir.offsetZ * distance, currentDir.offsetX * distance);

                }
                TextureMaps.bindBorderedTexture(currentState);
                modelPipeExtension.render();

                glPopMatrix();
            }

            if (currentDir != ForgeDirection.DOWN) {
                glPushMatrix();
                RenderUtils.bindTexture(TextureMaps.JAR_CONNECTION);

                float distance = 0.173F;
                glTranslatef(currentDir.offsetX * distance, currentDir.offsetY * distance, currentDir.offsetZ * distance);

                if (currentDir != ForgeDirection.UP) {

                    float secondaryTranslate = 0.455F;

                    glTranslatef(currentDir.offsetX * secondaryTranslate, 0.0F, currentDir.offsetZ * secondaryTranslate);
                    glRotatef(90F, currentDir.offsetZ * 1.0F, currentDir.offsetY * 1.0F, currentDir.offsetX * 1.0F);
                }

                glScalef(1.95F, 1.55F, 1.95F);

                modelJarConnection.render();

                glPopMatrix();
            }

            glPopMatrix();
        } else if (currentState.isContainerType()) {
            glPushMatrix();

            glTranslatef(xDisplace, yDisplace, zDisplace);

            modelThaumicPipe.renderArm(i + 1);

            glPopMatrix();
        }
    }

    private boolean isBigNode(ArmState[] armStateArray) {
        boolean tempBoolean = false;

        for (ArmState armState : armStateArray) {
            if (armState == null)
                continue;

            tempBoolean = armState.isJarType() || armState.isContainerType();
            if (tempBoolean)
                break;
        }

        return tempBoolean;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
        if (tileEntity instanceof TileThaumicPipe)
            renderThaumicPipeAt((TileThaumicPipe) tileEntity, x + 0.5F, y + 0.5F, z + 0.5F, tick);
    }
}
