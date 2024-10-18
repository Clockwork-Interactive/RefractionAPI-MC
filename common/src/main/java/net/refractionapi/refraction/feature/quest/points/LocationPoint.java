package net.refractionapi.refraction.feature.quest.points;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.feature.quest.Quest;

public class LocationPoint extends QuestPoint {

    private Vec3 point;
    private double radius;

    public LocationPoint(Quest quest, Vec3 point, double radius) {
        super(quest);
        this.point = point;
        this.radius = radius;
    }

    @Override
    public void tick() {
        if (this.quest.getPlayer().distanceToSqr(this.point) < this.radius * this.radius) {
            this.completed = true;
        }
    }

    @Override
    public Component description() {
        return Component.literal("Go to %.1f %.1f %.1f (%.1f blocks)".formatted(this.point.x, this.point.y, this.point.z, Mth.sqrt((float) this.quest.getPlayer().distanceToSqr(this.point))));
    }

    @Override
    public void serialize(CompoundTag tag) {
        super.serialize(tag);
        tag.putDouble("x", this.point.x);
        tag.putDouble("y", this.point.y);
        tag.putDouble("z", this.point.z);
        tag.putDouble("radius", this.radius);
    }

    @Override
    public String id() {
        return "location";
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        this.point = new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
        this.radius = tag.getDouble("radius");
    }

}
