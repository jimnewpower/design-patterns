import java.util.Objects;
import java.util.function.Supplier;

public class ProxyChecks {
    public static void main(String[] args) throws Exception {
        var loads = new java.util.concurrent.atomic.AtomicInteger();
        var proxy = new ProxyDemo.LazyDocument(() -> { loads.incrementAndGet(); return new ProxyDemo.StoredDocument("Report"); });
        equal(0, loads.get());
        try (var pool = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = new java.util.ArrayList<java.util.concurrent.Future<String>>();
            for (int i = 0; i < 20; i++) tasks.add(pool.submit(proxy::read));
            for (var task : tasks) equal("Report", task.get(10, java.util.concurrent.TimeUnit.SECONDS));
        }
        equal(1, loads.get());
        var attempts = new java.util.concurrent.atomic.AtomicInteger();
        var retry = new ProxyDemo.LazyDocument(() -> { if (attempts.getAndIncrement() == 0) throw new IllegalStateException("offline"); return new ProxyDemo.StoredDocument("Recovered"); });
        fails(IllegalStateException.class, retry::read);
        equal("Recovered", retry.read());
        equal(2, attempts.get());
        fails(NullPointerException.class, () -> new ProxyDemo.LazyDocument(null));
        fails(NullPointerException.class, () -> new ProxyDemo.LazyDocument(() -> null).read());
        System.out.println("All Proxy behavior checks passed.");
    }

    static void equal(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    static void fails(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Wrong exception", error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
