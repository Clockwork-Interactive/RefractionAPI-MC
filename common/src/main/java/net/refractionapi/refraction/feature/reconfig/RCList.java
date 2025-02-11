package net.refractionapi.refraction.feature.reconfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class RCList<T extends ReconfigValue<?>> extends ReconfigValue<List<T>> {
    private final Class<T> tClass;

    public RCList(Class<T> clazz, List<T> defaultValue) {
        super(defaultValue);
        this.tClass = clazz;
    }

    public void add(T value) {
        this.value.add(value);
    }

    @SuppressWarnings("unchecked")
    public <O> List<O> asList() {
        return (List<O>) this.loadAndGet().stream().map((r) -> r.value).toList();
    }

    @Override
    public void serialize(String name, JsonObject object) {
        JsonArray array = new JsonArray();
        this.value.forEach(element -> {
            JsonObject elementObject = new JsonObject();
            element.serialize(element.type(), elementObject);
            array.add(elementObject);
        });
        object.add(name, array);
    }

    @Override
    public void deserialize(String name, JsonObject object) {
        if (!object.has(name)) return;
        JsonElement primitive = object.get(name);
        if (!primitive.isJsonArray()) return;
        JsonArray array = primitive.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject elementObject = array.get(i).getAsJsonObject();
            T element = this.value.size() > i ? this.value.get(i) : this.create(tClass);
            element.deserialize(element.type(), elementObject);
        }
    }

    @Override
    public String type() {
        return "list";
    }
}
