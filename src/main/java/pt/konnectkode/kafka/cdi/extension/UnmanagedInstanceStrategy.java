package pt.konnectkode.kafka.cdi.extension;

import javax.enterprise.inject.spi.Unmanaged;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class UnmanagedInstanceStrategy {

    private List<Unmanaged.UnmanagedInstance> unmanagedInstances = new ArrayList<>();

    private CacheStrategy cacheStrategy = new CacheStrategy();

    private AlwaysNewStrategy alwaysNewStrategy = new AlwaysNewStrategy();

    <T> T cacheInstance(Class<T> klass) {
        return cacheStrategy.getInstance(klass);
    }

    <T> T newInstance(Class<T> klass) {
        return alwaysNewStrategy.getInstance(klass);
    }

    void dispose() {
        unmanagedInstances.forEach(unmanagedInstance -> unmanagedInstance.preDestroy().dispose());
    }

    private <T> T registerUnmanagedInstance(Class<T> klass) {
        Unmanaged<T> unmanaged = new Unmanaged<>(klass);
        Unmanaged.UnmanagedInstance<T> unmanagedInstance = unmanaged.newInstance();

        unmanagedInstances.add(unmanagedInstance);

        return unmanagedInstance.produce()
                .inject()
                .postConstruct()
                .get();
    }

    public interface Strategy {

        <T> T getInstance(Class<T> klass);

    }

    private class CacheStrategy implements Strategy {

        private ConcurrentHashMap<Class<?>, Object> cache = new ConcurrentHashMap<>();

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getInstance(Class<T> klass) {
            T instance;

            if (cache.containsKey(klass)) {
                instance = (T) cache.get(klass);
            }
            else {
                instance = registerUnmanagedInstance(klass);
                cache.put(klass, instance);
            }

            return instance;
        }

    }

    private class AlwaysNewStrategy implements Strategy {

        @Override
        public <T> T getInstance(Class<T> klass) {
            return registerUnmanagedInstance(klass);
        }
    }

}
