package net.refractionapi.refraction.feature.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.mixininterfaces.ILivingRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RefArmorRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    final EntityModelSet set;
    static final Collection<Item> renderers = new ArrayList<>();
    final HashMap<ArmorRegister, EntityModel<?>> cached = new HashMap<>();
    final HashMap<EquipmentSlot, EntityModel<?>> active = new HashMap<>();
    final EquipmentSlot[] armorSlots = new EquipmentSlot[]{
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
    };

    public RefArmorRenderer(RenderLayerParent<T, M> renderer, EntityModelSet set) {
        super(renderer);
        renderers.forEach((item) -> {
            if (!(item instanceof ArmorRegister register)) return;
            cached.put(register, (EntityModel<?>) register.getModel().apply(set));
        });
        this.set = set;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, LivingEntity entity, float limbSwing, float limbSwingAmount, float partial, float age, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) return;
        iterateSlots(entity, (stack, equipmentSlot) -> {
            if (stack.isEmpty()) active.remove(equipmentSlot);
            if (!(stack.getItem() instanceof ArmorRegister ext) || !(stack.getItem() instanceof ArmorItem)) return;
            safeTryPut(equipmentSlot, ext);
        });
        for (Map.Entry<EquipmentSlot, EntityModel<?>> entry : active.entrySet()) {
            EquipmentSlot slot = entry.getKey();
            EntityModel<?> model = entry.getValue();
            if (!(model instanceof ArmorModelType<?> type)) {
                Refraction.LOGGER.error("Implement ArmorModelType for {}!", model.getClass().getCanonicalName());
                continue;
            }
            EntityModel<T> castedModel = (EntityModel<T>) model;
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(type.texture()));
            this.getParentModel().copyPropertiesTo(castedModel);

            HumanoidModel<?> humanoidModel = (HumanoidModel<?>) castedModel;
            setPartVisibility(humanoidModel, slot);
            if (this.getParentModel() instanceof HumanoidModel<?> parentHumanoid) {
                humanoidModel.head.copyFrom(parentHumanoid.head);
                humanoidModel.hat.copyFrom(parentHumanoid.hat);
            }
            castedModel.prepareMobModel((T) entity, limbSwing, limbSwingAmount, packedLight);
            castedModel.setupAnim((T) entity, limbSwing, limbSwingAmount, age, netHeadYaw, headPitch);
            castedModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        }
    }

    public void safeTryPut(EquipmentSlot slot, ArmorRegister armorRegister) {
        EntityModel<?> model = cached.get(armorRegister);
        if (model == null) {
            Refraction.LOGGER.error("Invalid entity model for slot {}", slot);
            return;
        }
        active.put(slot, model);
    }

    public void iterateSlots(LivingEntity entity, BiConsumer<ItemStack, EquipmentSlot> iterator) {
        for (EquipmentSlot armorSlot : armorSlots) {
            iterator.accept(entity.getItemBySlot(armorSlot), armorSlot);
        }
    }

    protected void setPartVisibility(HumanoidModel<?> model, EquipmentSlot slot) {
        model.setAllVisible(false);
        switch (slot) {
            case HEAD:
                model.head.visible = true;
                model.hat.visible = true;
                break;
            case CHEST:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                break;
            case LEGS:
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
            case FEET:
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
        }
    }

    public static void registerOnAll(RenderDispatcherContext context, EntityModelSet modelSet) {
        for (EntityRenderer<? extends Player> renderer : context.playerRenderers().values())
            registerOn(renderer, modelSet);
        for (EntityRenderer<?> renderer : context.renderers().values())
            registerOn(renderer, modelSet);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerOn(EntityRenderer<?> entityRenderer, EntityModelSet modelSet) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        if (!(livingRenderer instanceof ILivingRenderer<?, ?> rend))
            return;
        RefArmorRenderer layer = new RefArmorRenderer(livingRenderer, modelSet);
        rend.addLayer(layer);
    }

    public static void cacheRenderer(Item item) {
        if (renderers.contains(item)) return;
        renderers.add(item);
        Refraction.LOGGER.info("Registered armor renderer for {}", Component.translatable(item.getDescriptionId()).getString());
    }
}
