package me.jezza.thaumicpipes.client.renderer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import me.jezza.thaumicpipes.client.RenderUtils;
import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.client.model.ModelJarConnection;
import me.jezza.thaumicpipes.client.model.ModelPipeExtension;
import me.jezza.thaumicpipes.client.model.ModelThaumicPipe;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import me.jezza.thaumicpipes.common.tileentity.TileThaumicPipe;
import me.jezza.thaumicpipes.common.transport.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.ConnectionType;
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

        ArmState[] armSet = thaumicPipe.getArmStateArray();

        for (int index = 0; index < armSet.length; index++) {
            ArmState currentState = armSet[index];
            if (currentState == null || !currentState.isValid())
                continue;

            renderArm(currentState, index + 1);
        }

        renderNodeState(thaumicPipe.getNodeState());

        if (RenderUtils.canRenderPriority())
            for (ArmState state : armSet)
                if (state.isPriority()) {
                    RenderUtils.bindPriorityTexture(thaumicPipe.getAnimationFrame());
                    renderPriority(state.getDirection(), state.getPosition());
                }
        glPopMatrix();
    }

    private void renderNodeState(NodeState nodeState) {
        if (nodeState == null)
            return;

        if (nodeState.isNode())
            renderNode(nodeState.isBigNode());
        else
            renderNodeReplacement(nodeState.getDirection());
    }

    private void renderNodeReplacement(ForgeDirection flag) {
        glPushMatrix();
        RenderUtils.bindBorderlessTexture();
        switch (flag) {
            case DOWN:
                glRotatef(90, 1.0F, 0.0F, 0.0F);
            case NORTH:
                glRotatef(90, 0.0F, 1.0F, 0.0F);
            case WEST:
                glTranslatef(-0.2F, 0.0F, 0.0F);
                modelPipeExtension.renderAll();
                glTranslatef(0.2F, 0.0F, 0.0F);
                modelPipeExtension.renderAll();
                glTranslatef(0.2F, 0.0F, 0.0F);
                modelPipeExtension.renderAll();
                break;
            default:
                break;
        }
        glPopMatrix();
    }

    private void renderArm(ArmState armState, int index) {
        glPushMatrix();
        RenderUtils.bindPipeTexture();

        modelThaumicPipe.renderArm(index);

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
        ForgeDirection currentDir = currentState.getDirection();
        ConnectionType type = currentState.getType();

        float xDisplace = currentDir.offsetX;
        float yDisplace = currentDir.offsetY;
        float zDisplace = currentDir.offsetZ;

        if (type.isJar()) {
            glPushMatrix();
            glTranslatef(xDisplace, yDisplace, zDisplace);

            if (currentDir != ForgeDirection.UP) {
                glPushMatrix();

                float distance = 0.4F;
                glTranslatef(xDisplace * distance, yDisplace * distance, zDisplace * distance);

                glRotatef(90F, xDisplace, zDisplace, yDisplace);

                if (currentDir == ForgeDirection.NORTH)
                    glRotatef(180F, 0.0F, 0.0F, 1.0F);

                if (currentDir == ForgeDirection.DOWN) {
                    glRotatef(180, 0.0F, 1.0F, 0.0F);
                    RenderUtils.bindBorderlessTexture();
                    modelPipeExtension.renderAll();

                    distance = 0.20F;
                    glTranslatef(yDisplace * distance, zDisplace * distance, xDisplace * distance);
                }
                RenderUtils.bindBorderedTexture();
                modelPipeExtension.renderAll();

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
                    glRotatef(90F, currentDir.offsetZ, currentDir.offsetY, currentDir.offsetX);
                }

                glScalef(1.95F, 1.55F, 1.95F);

                modelJarConnection.renderAll();

                glPopMatrix();
            }

            glPopMatrix();
        } else if (type.isAlembic()) {
            glPushMatrix();

            glTranslatef(xDisplace, yDisplace, zDisplace);

            modelThaumicPipe.renderArm(index);

            glPopMatrix();
        }
    }

    private void renderPriority(ForgeDirection armAxis, int position) {
        glColor4f(0.49F, 0.976F, 1F, 0.4F);
        renderPriorityOnAxis(armAxis, position);
    }

    private void renderPriorityOnAxis(ForgeDirection axis, int pixelsToShift) {
        glPushMatrix();
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        float xDisplace = axis.offsetX;
        float yDisplace = axis.offsetY;
        float zDisplace = axis.offsetZ;
        float pixelStep = 0.05F;

        glTranslatef(xDisplace * pixelStep * pixelsToShift, yDisplace * pixelStep * pixelsToShift, zDisplace * pixelStep * pixelsToShift);

        switch (axis) {
            case DOWN:
            case UP:
                glRotatef(90, 1.0F, 0.0F, 0.0F);
            case SOUTH:
            case NORTH:
                glRotatef(90, 0.0F, 1.0F, 0.0F);
            default:
                break;
        }

        float scale = 1.015F;
        glScalef(scale, scale, scale);

        modelPipeExtension.renderAll();

        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glDisable(GL_BLEND);
        glEnable(GL_LIGHTING);
        glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
        if (tileEntity instanceof TileThaumicPipe)
            renderThaumicPipeAt((TileThaumicPipe) tileEntity, x + 0.5F, y + 0.5F, z + 0.5F, tick);
    }
}
