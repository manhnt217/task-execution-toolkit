package io.github.manhnt217.task.core.container;

/**
 * A very simple class to increase parallelism, yet to maintain the data integrity.
 * Should be replaced by a better implementation (but right now I don't have to do the research)
 * @author manhnguyen
 */
class SyncHelper {
    private static final int DEFAULT_KEY_CAPACITY = 10000; //level of paralellism
    private final SyncKey[] cache;
    private final int capacity;

    public SyncHelper(int capacity) {
        this.capacity = capacity <= 0 ? DEFAULT_KEY_CAPACITY : capacity;
        this.cache = new SyncKey[this.capacity];
    }
    public SyncHelper() {
        this(DEFAULT_KEY_CAPACITY);
    }

    /**
     * Gurantee that 2 concurent calls with the same key will be executed in sequential order.
     * Notice: In case of key collision (as the result of hash algorithm), 2 concurrent calls with different key may still be executed in sequential order.
     */
    public <R, E extends Throwable> R doSync(String key, CallableAction<R, E> callableAction) throws E {
        synchronized (getKey(key)) {
            return callableAction.call();
        }
    }

    public <E extends Throwable> void doSync(String key, Action<E> action) throws E {
        synchronized (getKey(key)) {
            action.act();
        }
    }

    /**
     * Gurantee to return the same key (object) given the same keyName
     * Notice: Diffent inputs might return the same key (key collision)
     * @param keyName the key you need to do synchronize
     * @return the coresponding SyncKey
     */
    private synchronized SyncKey getKey(String keyName) {
        if (keyName == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        int keyLocation = Math.abs(keyName.hashCode()) % capacity;
        SyncKey syncKey = cache[keyLocation];
        if (syncKey == null) {
            syncKey = new SyncKey();
            cache[keyLocation] = syncKey;
        }
        return syncKey;
    }

    private static class SyncKey {

    }
}
