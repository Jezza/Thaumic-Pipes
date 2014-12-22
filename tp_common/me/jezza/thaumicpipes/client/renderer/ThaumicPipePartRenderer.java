package me.jezza.thaumicpipes.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.jezza.thaumicpipes.client.RenderUtils;
import me.jezza.thaumicpipes.client.interfaces.IPartRenderer;
import me.jezza.thaumicpipes.client.model.ModelJarConnection;
import me.jezza.thaumicpipes.client.model.ModelPipeExtension;
import me.jezza.thaumicpipes.client.model.ModelThaumicPipe;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;
import me.jezza.thaumicpipes.common.transport.connection.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.ConnectionType;
import me.jezza.thaumicpipes.common.transport.connection.NodeState;
import net.minecraftforge.common.util.ForgeDirection;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class ThaumicPipePartRenderer implements IPartRenderer {

    ModelThaumicPipe modelThaumicPipe = new ModelThaumicPipe();
    ModelJarConnection modelJarConnection = new ModelJarConnection();
    ModelPipeExtension modelPipeExtension = new ModelPipeExtension();

    public ThaumicPipePartRenderer() {
    }

    public void render(ThaumicPipePart pipe, double x, double y, double z, float tick) {
        glPushMatrix();

        glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);

        float scale = 0.3850F;
        glScalef(scale, scale, scale);

        ArmState[] armSet = pipe.getArmStateArray();
        for (int index = 0; index < armSet.length; index++) {
            ArmState currentState = armSet[index];
            if (currentState.isPartValid())
                renderArm(currentState, index + 1);
        }

        renderNodeState(pipe.getNodeState());

        glPopMatrix();
    }

    private void renderNodeState(NodeState nodeState) {
        float scale = 1.01F;
        switch (nodeState.getId()) {
            case 0:
                scale = 1.33F;
            case 1:
            default:
                RenderUtils.bindTexture(TextureMaps.THAUMIC_PIPE_CENTRE[TextureMaps.THAUMIC_TEXTURE_INDEX]);
                glScalef(scale, scale, scale);
                modelThaumicPipe.renderPart("centre");
                break;
            case 2:
                RenderUtils.bindBorderlessTexture();
                switch (nodeState.getDirection()) {
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
        }
    }

    private void renderArm(ArmState currentState, int index) {
        RenderUtils.bindPipeTexture();
        modelThaumicPipe.renderArm(index);

        glPushMatrix();
        ConnectionType connectionType = currentState.getConnectionType();
        switch (connectionType) {
            case JAR:
                renderJarConnections(currentState, index);
                break;
            case PIPE:
                break;
            default:
                float extensionSize = connectionType.getExtensionSize();
                if (extensionSize == 0.0F)
                    break;
                ForgeDirection currentDir = currentState.getDirection();
                glTranslatef(currentDir.offsetX * extensionSize, currentDir.offsetY * extensionSize, currentDir.offsetZ * extensionSize);
                glScalef(0.9999F, 0.9999F, 0.9999F);
                modelThaumicPipe.renderArm(index);
                break;
        }
        glPopMatrix();
    }

    private void renderJarConnections(ArmState currentState, int index) {
        ForgeDirection direction = currentState.getDirection();

        float xDisplace = direction.offsetX;
        float yDisplace = direction.offsetY;
        float zDisplace = direction.offsetZ;

        glTranslatef(xDisplace, yDisplace, zDisplace);

        if (direction != ForgeDirection.UP) {
            glPushMatrix();

            float distance = 0.4F;
            glTranslatef(xDisplace * distance, yDisplace * distance, zDisplace * distance);

            glRotatef(90F, xDisplace, zDisplace, yDisplace);

            if (direction == ForgeDirection.NORTH)
                glRotatef(180F, 0.0F, 0.0F, 1.0F);

            if (direction == ForgeDirection.DOWN) {
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

        if (direction != ForgeDirection.DOWN) {
            glPushMatrix();
            RenderUtils.bindTexture(TextureMaps.JAR_CONNECTION);

            float distance = 0.173F;
            glTranslatef(direction.offsetX * distance, direction.offsetY * distance, direction.offsetZ * distance);

            if (direction != ForgeDirection.UP) {
                float secondaryTranslate = 0.455F;

                glTranslatef(direction.offsetX * secondaryTranslate, 0.0F, direction.offsetZ * secondaryTranslate);
                glRotatef(90F, direction.offsetZ, direction.offsetY, direction.offsetX);
            }

            glScalef(1.95F, 1.55F, 1.95F);

            modelJarConnection.renderAll();

            glPopMatrix();
        }
    }

    @Override
    public void renderAt(PipePartAbstract part, double x, double y, double z, float tick) {
        if (part instanceof ThaumicPipePart)
            render((ThaumicPipePart) part, x, y, z, tick);
    }
}
