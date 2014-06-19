package me.jezza.thaumicpipes.client.renderer;

import static org.lwjgl.opengl.GL11.*;

import me.jezza.thaumicpipes.client.RenderUtils;
import me.jezza.thaumicpipes.client.model.ModelJarConnection;
import me.jezza.thaumicpipes.client.model.ModelPipeExtension;
import me.jezza.thaumicpipes.client.model.ModelThaumicPipe;
import me.jezza.thaumicpipes.common.core.ArmState;
import me.jezza.thaumicpipes.common.core.ConnectionType;
import me.jezza.thaumicpipes.common.core.TPLogger;
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
        TextureMaps.THAUMIC_TEXTURE_INDEX = 2;
        glPushMatrix();

        glTranslated(x, y, z);

        float scale = 0.3850F;

        glScalef(scale, scale, scale);

        ArmState[] armSet = thaumicPipe.getArmStateArray();

        for (int index = 0; index < armSet.length; index++) {
            ArmState currentState = armSet[index];
            if (currentState == null || !currentState.isValid())
                continue;

            renderArm(currentState, index);
        }

        ForgeDirection flag = shouldRenderNode(armSet);

        if (flag == ForgeDirection.UNKNOWN)
            renderNode(isBigNode(armSet));
        else
            renderNodeReplacement(flag);

        glPopMatrix();
    }

    private void renderNodeReplacement(ForgeDirection flag) {
        RenderUtils.bindTexture(TextureMaps.PIPE_EXTENSION_BORDERLESS[TextureMaps.THAUMIC_TEXTURE_INDEX]);
        switch (flag) {
            case DOWN:
                glRotatef(90, 1.0F, 0.0F, 0.0F);
            case NORTH:
                glRotatef(90, 0.0F, 1.0F, 0.0F);
            case WEST:
                glTranslatef(-0.2F, 0.0F, 0.0F);
                modelPipeExtension.render();
                glTranslatef(0.2F, 0.0F, 0.0F);
                modelPipeExtension.render();
                glTranslatef(0.2F, 0.0F, 0.0F);
                modelPipeExtension.render();
                break;
            default:
                break;
        }
    }

    private ForgeDirection shouldRenderNode(ArmState[] armStateArray) {
        int count = 0;
        boolean flag = true;
        int side = 0;

        for (int i = 0; i <= 5; i += 2) {
            ArmState firstState = armStateArray[i];
            ArmState secondState = armStateArray[i + 1];

            boolean flag2 = firstState.isValid();
            boolean flag3 = secondState.isValid();

            if (flag2)
                count++;
            if (flag3)
                count++;

            if (flag2 && flag3)
                side = i;

            if (confirmArmState(firstState, secondState))
                flag = false;
        }

        if (count > 2)
            flag = true;

        if (flag)
            return ForgeDirection.UNKNOWN;
        return ForgeDirection.getOrientation(side);
    }

    private boolean confirmArmState(ArmState firstState, ArmState secondState) {
        if (firstState.isValid() && secondState.isValid())
            return firstState.getDirection().getOpposite().equals(secondState.getDirection());
        return false;
    }

    private void renderArm(ArmState armState, int index) {
        glPushMatrix();
        TextureMaps.bindPipeTexture(armState);

        modelThaumicPipe.renderArm(index + 1);

        processPostArmRender(armState, index);
        glPopMatrix();
    }

    private void renderNode(boolean flag) {
        glPushMatrix();
        RenderUtils.bindTexture(TextureMaps.THAUMIC_PIPE_CENTRE[TextureMaps.THAUMIC_TEXTURE_INDEX]);

        float scale = 1.01F;
        if (flag)
            scale += 0.32F;

        glScalef(scale, scale, scale);

        modelThaumicPipe.renderPart("Centre");
        glPopMatrix();
    }

    private void processPostArmRender(ArmState currentState, int index) {
        ConnectionType type = currentState.getConnectionState().getType();
        ForgeDirection currentDir = currentState.getDirection();

        float xDisplace = currentDir.offsetX;
        float yDisplace = currentDir.offsetY;
        float zDisplace = currentDir.offsetZ;

        if (type.isJarType()) {
            glPushMatrix();
            glTranslatef(xDisplace, yDisplace, zDisplace);

            if (currentDir != ForgeDirection.UP) {
                glPushMatrix();

                float distance = 0.4F;
                glTranslatef(currentDir.offsetX * distance, currentDir.offsetY * distance, currentDir.offsetZ * distance);

                glRotatef(90F, xDisplace, zDisplace, yDisplace);

                if (currentDir == ForgeDirection.NORTH)
                    glRotatef(180F, 0.0F, 0.0F, 1.0F);

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
        } else if (type.isContainerType()) {
            glPushMatrix();

            glTranslatef(xDisplace, yDisplace, zDisplace);

            modelThaumicPipe.renderArm(index + 1);

            glPopMatrix();
        }
    }

    private boolean isBigNode(ArmState[] armStateArray) {
        for (ArmState armState : armStateArray) {
            if (armState == null)
                continue;

            if (armState.getConnectionState().getType().isBigNode())
                return true;
        }

        return false;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
        if (tileEntity instanceof TileThaumicPipe)
            renderThaumicPipeAt((TileThaumicPipe) tileEntity, x + 0.5F, y + 0.5F, z + 0.5F, tick);
    }
}
