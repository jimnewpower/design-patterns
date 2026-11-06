import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/** Dependency-free behavior checks; Java 21 virtual threads exercise concurrent first access. */
public class SingletonChecks {
    public static void main(String[] args) throws Exception {
        // Do this before any active use of ReportFormats on the main thread.
        int workers = 32;
        var ready = new CountDownLatch(workers);
        var start = new CountDownLatch(1);
        var tasks = new ArrayList<Future<SingletonDemo.ReportFormats>>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            try {
                for (int i = 0; i < workers; i++) {
                    tasks.add(executor.submit(() -> {
                        ready.countDown();
                        if (!start.await(10, TimeUnit.SECONDS)) throw new AssertionError("Start timeout");
                        var catalog = SingletonDemo.ReportFormats.INSTANCE;
                        equal(".html", catalog.extensionFor("HTML"));
                        equal(".txt", catalog.extensionFor("text"));
                        return catalog;
                    }));
                }
                if (!ready.await(10, TimeUnit.SECONDS)) throw new AssertionError("Workers not ready");
                start.countDown();
                var first = tasks.get(0).get(10, TimeUnit.SECONDS);
                for (var task : tasks) same(first, task.get(10, TimeUnit.SECONDS));
            } finally {
                start.countDown();
                tasks.forEach(task -> task.cancel(true));
            }
        }
        var catalog = SingletonDemo.ReportFormats.INSTANCE;
        same(catalog, SingletonDemo.ReportFormats.INSTANCE);
        var bytes = new ByteArrayOutputStream();
        try (var output = new ObjectOutputStream(bytes)) { output.writeObject(catalog); }
        try (var input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            same(catalog, input.readObject());
        }
        var namer = new SingletonDemo.ReportNamer(catalog);
        equal("tools.txt", namer.fileName("tools", "text"));
        equal("tools.html", namer.fileName("tools", "HTML"));
        // Consumer tests can inject a fake without resetting or replacing the singleton.
        var fakeNamer = new SingletonDemo.ReportNamer(format -> ".test");
        equal("tools.test", fakeNamer.fileName("tools", "anything"));
        fails(IllegalArgumentException.class, () -> catalog.extensionFor("pdf"));
        fails(IllegalArgumentException.class, () -> catalog.extensionFor(""));
        fails(NullPointerException.class, () -> catalog.extensionFor(null));
        fails(IllegalArgumentException.class, () -> namer.fileName(" ", "text"));
        fails(NullPointerException.class, () -> namer.fileName(null, "text"));
        fails(NullPointerException.class, () -> new SingletonDemo.ReportNamer(null));
        System.out.println("All Singleton behavior checks passed.");
    }
    static void same(Object expected, Object actual) {
        if (expected != actual) throw new AssertionError("Expected identical instances");
    }
    static void equal(String expected, String actual) {
        if (!expected.equals(actual)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    static void fails(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Wrong exception type", error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
