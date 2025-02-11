package net.refractionapi.refraction.feature.reconfig;

import com.google.gson.JsonObject;

public class RCBoolean extends ReconfigValue<Boolean> {
    public RCBoolean(Boolean defaultValue) {
        super(defaultValue);
    }

    public RCBoolean() {
        super(false);
    }

    @Override
    public void serialize(String name,JsonObject object) {
        object.addProperty(name, this.getValue());
    }

    @Override
    public void deserialize(String name, JsonObject object) {
        if (object.has(name)) {
            this.value = object.get(name).getAsBoolean();
        }
    }

    @Override
    public String type() {
        return "boolean";
    }
}
