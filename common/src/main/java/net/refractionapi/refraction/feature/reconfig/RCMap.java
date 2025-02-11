package net.refractionapi.refraction.feature.reconfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.refractionapi.refraction.Refraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class RCMap<K extends ReconfigValue<?>, V extends ReconfigValue<?>> extends ReconfigValue<HashMap<K, V>> {
    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public RCMap(Class<K> keyClass, Class<V> valueClass, HashMap<K, V> defaultValue) {
        super(defaultValue);
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @SuppressWarnings("unchecked")
    public <C, N> HashMap<C, N> asMap() {
        return (HashMap<C, N>) this.loadAndGet().entrySet().stream().collect(HashMap::new, (map, entry) -> map.put(entry.getKey().getValue(), entry.getValue().getValue()), HashMap::putAll);
    }

    @Override
    public void serialize(String name, JsonObject object) {
        JsonArray array = new JsonArray();
        this.value.forEach((key, value) -> {
            JsonObject entry = new JsonObject();
            JsonObject keyObject = new JsonObject();
            key.serialize("key", keyObject);
            JsonObject valueObject = new JsonObject();
            value.serialize("value", valueObject);
            entry.add("key", keyObject);
            entry.add("value", valueObject);
            array.add(entry);
        });
        object.add(name, array);
    }

    @Override
    public void deserialize(String name, JsonObject object) {
        if (!object.has(name)) return;
        JsonElement primitive = object.get(name);
        if (!primitive.isJsonArray()) return;
        JsonArray array = primitive.getAsJsonArray();
        this.value.clear();
        for (int i = 0; i < array.size(); i++) {
            JsonObject entryObject = array.get(i).getAsJsonObject();
            JsonObject keyObject = entryObject.getAsJsonObject("key");
            JsonObject valueObject = entryObject.getAsJsonObject("value");
            if (keyObject == null || valueObject == null) return;
            Set<K> keys = this.value.keySet();
            Collection<V> values = this.value.values();
            K key = keys.size() > i ? keys.stream().skip(i).findFirst().orElse(this.create(keyClass)) : this.create(keyClass);
            V value = values.size() > i ? values.stream().skip(i).findFirst().orElse(this.create(valueClass)) : this.create(valueClass);
            key.deserialize("key", keyObject);
            value.deserialize("value", valueObject);
            this.value.put(key, value);
        }
    }

    @Override
    public String type() {
        return "map";
    }
}
