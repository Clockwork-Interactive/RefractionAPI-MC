package net.refractionapi.refraction.debug.debuggers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import imgui.ImGui;
import imgui.type.ImString;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.debug.RDebugRenderer;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArraySet;

public class TraceEntitiesRenderer extends RDebugRenderer {
    float maxDistance = 100.0F;
    CopyOnWriteArraySet<String> match = new CopyOnWriteArraySet<>();

    public TraceEntitiesRenderer() {
        super("trace_entities");
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource multiBufferSource) {
        Level level = this.minecraft.level;
        if (level == null) return;
        level.getEntities(ClientData.getPlayer(), AABB.ofSize(ClientData.getPlayer().position(), this.maxDistance, this.maxDistance, this.maxDistance)).forEach(entity -> renderDebug(entity, poseStack, multiBufferSource));
    }

    protected void renderDebug(Entity entity, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        String[] entityType = entity.getType().toString().split("[.]");
        if ((!match.isEmpty() && !match.contains("")) && !match.contains(entityType[entityType.length - 1])) return;
        Vec3 pos = entity.position();
        Vec3 cameraPos = cameraPosition();
        Tesselator tesselator = Tesselator.getInstance();

        BufferBuilder consumer = tesselator.getBuilder();
        consumer.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(2.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        consumer.vertex(poseStack.last().pose(), (float) pos.x, (float) pos.y, (float) pos.z).color(1.0F, 0.0F, 0.0F, 1.0F);
        consumer.vertex(poseStack.last().pose(), (float) cameraPos.x, (float) cameraPos.y - 0.5F, (float) cameraPos.z).color(1.0F, 0.0F, 0.0F, 1.0F);
        poseStack.popPose();
        BufferUploader.drawWithShader(consumer.end());
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();

        float distance = (float) pos.subtract(cameraPos).length();
        float y = (float) (pos.y + entity.getBbHeight()) + distance / 10;
        float scale = distance / 100 / 2;
        DebugRenderer.renderFloatingText(poseStack, multiBufferSource, entity.getType().toString(), pos.x, y + 0.2F, pos.z, Color.WHITE.getRGB(), scale, true, 0.0F, true);
    }

    @Override
    public void renderGUI() {
        float[] newValue = {this.maxDistance};
        ImGui.sliderFloat("Max Distance", newValue, 0.0F, 1000.0F);
        this.maxDistance = Mth.clamp(newValue[0], 0.0F, 1000.0F);
        String previousState = String.join(";", this.match);
        ImString matchString = new ImString();
        matchString.set(previousState);
        ImGui.inputText("Match (Split with ;)", matchString);
        if (!matchString.get().equals(previousState)) {
            this.match.clear();
            String[] split = matchString.get().split(";");
            this.match.addAll(Arrays.asList(split));
        }
    }

    @Override
    protected void fromPacket(FriendlyByteBuf buf) {

    }
}
