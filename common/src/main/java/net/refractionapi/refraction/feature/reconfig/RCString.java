package net.refractionapi.refraction.feature.reconfig;

import com.google.gson.JsonObject;

public class RCString extends ReconfigValue<String> {
    public RCString(String defaultValue) {
        super(defaultValue);
    }

    public RCString() {
        super("");
    }

    @Override
    public void serialize(String name, JsonObject object) {
        object.addProperty(name, value);
    }

    @Override
    public void deserialize(String name, JsonObject object) {
        if (object.has(name)) {
            this.value = object.get(name).getAsString();
        }
    }

    @Override
    public String type() {
        return "string";
    }
}
