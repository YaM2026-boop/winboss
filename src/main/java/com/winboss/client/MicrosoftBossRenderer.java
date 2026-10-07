package com.winboss.client;

import com.winboss.ModBlocks;
import com.winboss.entity.MicrosoftBossEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

/**
 * 「微软」Boss 渲染器：一个悬浮旋转的四色方块（微软遗物模型）。
 * 第二形态「巨硬」：放大 1.6 倍 + 金属光泽变色。
 */
public class MicrosoftBossRenderer extends EntityRenderer<MicrosoftBossEntity> {

    public MicrosoftBossRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(MicrosoftBossEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // 悬浮 + 缓慢自转
        float bob = (float) (Math.sin((entity.age + tickDelta) * 0.08) * 0.15);
        matrices.translate(0, 0.8 + bob, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
            (entity.age + tickDelta) * (entity.isPhase2() ? 4 : 2)));

        float scale = entity.isPhase2() ? 1.6f : 1.0f;
        matrices.scale(scale, scale, scale);

        // 四色方块 + 金属色优化（巨硬形态更暗更硬）
        ItemStack relic = new ItemStack(ModBlocks.WINDOWS_RELIC);
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        BakedModel model = renderer.getModel(relic, null, null, 0);
        renderer.renderItem(
            relic, ModelTransformationMode.FIXED, false,
            matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(MicrosoftBossEntity entity) {
        return new Identifier("textures/misc/white.png");
    }
}
