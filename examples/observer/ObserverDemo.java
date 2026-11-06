import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/** Self-contained Java 21 teaching example. */
public class ObserverDemo {
    public static void main(String[] args) {
        var subject = new ReleaseEvents();
        try (Subscription subscription = subject.subscribe(name -> System.out.println("Released: " + name))) {
            subject.publish("Engineering Tools");
        }
        subject.publish("No listener remains");
    }

    interface Subscription extends AutoCloseable { void close(); }
    static final class ReleaseEvents {
        private final Map<Object, Consumer<String>> listeners = new LinkedHashMap<>();
        Subscription subscribe(Consumer<String> listener) {
            Objects.requireNonNull(listener);
            Object token = new Object(); listeners.put(token, listener);
            return () -> listeners.remove(token);
        }
        void publish(String name) {
            Objects.requireNonNull(name);
            // Snapshot membership: changes during delivery apply to the next event.
            for (Consumer<String> listener : List.copyOf(listeners.values())) listener.accept(name);
        }
    }
}
