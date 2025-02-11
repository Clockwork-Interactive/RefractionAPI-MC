package net.refractionapi.refraction.feature.reconfig;

import com.google.gson.JsonObject;

public class RCDouble extends ReconfigValue<Double> {
    public RCDouble(Double defaultValue) {
        super(defaultValue);
    }

    public RCDouble() {
        super(0.0);
    }

    @Override
    public void serialize(String name, JsonObject object) {
        object.addProperty(name, this.getValue());
    }

    @Override
    public void deserialize(String name, JsonObject object) {
        if (object.has(name)) {
            this.value = object.get(name).getAsDouble();
        }
    }

    @Override
    public String type() {
        return "double";
    }
}
