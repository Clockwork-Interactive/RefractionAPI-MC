package net.refractionapi.refraction.helper.data;

import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class DoubleMap<K, X, Y> {

    private final HashMap<K, Entry> map = new HashMap<>();

    public DoubleMap() {

    }

    public void put(K key, X x, Y y) {
        map.put(key, new Entry(x, y));
    }

    private Entry getHandler(K key) {
        return map.get(key);
    }

    public X getX(K key) {
        return getHandler(key).getX();
    }

    public Y getY(K key) {
        return getHandler(key).getY();
    }

    public void setX(K key, X x) {
        getHandler(key).setX(x);
    }

    public void setY(K key, Y y) {
        getHandler(key).setY(y);
    }

    public void remove(K key) {
        this.map.remove(key);
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public void forEach(TriConsumer<K, X, Y> consumer) {
        this.map.forEach((key, entry) -> consumer.accept(key, entry.getX(), entry.getY()));
    }

    public void removeIf(BiPredicate<X, Y> predicate) {
        this.map.entrySet().removeIf(entry -> predicate.test(entry.getValue().getX(), entry.getValue().getY()));
    }

    public Stream<K> keyStream() {
        return this.map.keySet().stream();
    }

    public Stream<Entry> entryStream() {
        return this.map.values().stream();
    }

    public class Entry {
        private X x;
        private Y y;

        public Entry(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        public X getX() {
            return x;
        }

        public Y getY() {
            return y;
        }

        public void setX(X x) {
            this.x = x;
        }

        public void setY(Y y) {
            this.y = y;
        }
    }

}
