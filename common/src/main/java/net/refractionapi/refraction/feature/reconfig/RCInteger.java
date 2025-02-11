package net.refractionapi.refraction.feature.reconfig;

import com.google.gson.JsonObject;

public class RCInteger extends ReconfigValue<Integer> {
    public RCInteger(Integer defaultValue) {
        super(defaultValue);
    }

    public RCInteger() {
        super(0);
    }

    @Override
    public void serialize(String name,JsonObject object) {
        object.addProperty(name, value);
    }

    @Override
    public void deserialize(String name, JsonObject object) {
        if (object.has(name)) {
            this.value = object.get(name).getAsInt();
        }
    }

    @Override
    public String type() {
        return "int";
    }
}
