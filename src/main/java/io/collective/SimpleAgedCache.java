package io.collective;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

public class SimpleAgedCache {

    private final Clock clock;

    private final Map<Object, ExpirableEntry> values = new HashMap<>();

    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
    }

    public SimpleAgedCache() {
        this(Clock.systemDefaultZone());
    }

    public void put(Object key, Object value, int retentionInMillis) {

        ExpirableEntry entry = new ExpirableEntry(clock.millis(), retentionInMillis, value);

        values.put(key, entry);
    }

    public boolean isEmpty() {

        removeExpired();

        return values.isEmpty();
    }

    public int size() {

        removeExpired();

        return values.size();
    }

    public Object get(Object key) {
        if (values.containsKey(key)) {

            ExpirableEntry entry = values.get(key);

            if ((clock.millis() - entry.start) < entry.retentionInMillis) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void removeExpired() {

        for (Map.Entry<Object, ExpirableEntry> entryObject : new HashMap<>(values).entrySet()) {

            ExpirableEntry entry = entryObject.getValue();

            if ((clock.millis() - entry.start) >= entry.retentionInMillis) {
                values.remove(entryObject.getKey());
            }
        }
    }

    public class ExpirableEntry {

        long retentionInMillis;

        long start;

        private final Object entry; 

        public ExpirableEntry(long startTime, long retentionInMillis, Object entry) {
            this.start = startTime;
            this.retentionInMillis = retentionInMillis;
            this.entry = entry;
        }

        public Object getValue() {
            return entry;
        }
    }
}