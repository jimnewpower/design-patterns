import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ObserverChecks {
    public static void main(String[] args) throws Exception {
        var subject = new ObserverDemo.ReleaseEvents();
        var calls = new java.util.ArrayList<String>();
        var a = subject.subscribe(value -> calls.add("A:"+value));
        var b = subject.subscribe(value -> calls.add("B:"+value));
        subject.publish("one"); equal(List.of("A:one","B:one"),calls);
        calls.clear(); a.close(); a.close(); subject.publish("two"); equal(List.of("B:two"),calls);
        b.close(); calls.clear(); subject.publish("none"); equal(List.of(),calls);
        var second = new ObserverDemo.Subscription[1];
        subject.subscribe(value -> { calls.add("first"); second[0].close(); });
        second[0] = subject.subscribe(value -> calls.add("second"));
        subject.publish("snapshot"); equal(List.of("first","second"),calls);
        calls.clear(); subject.publish("next"); equal(List.of("first"),calls);
        var failing = new ObserverDemo.ReleaseEvents();
        failing.subscribe(value -> { throw new IllegalStateException(); });
        failing.subscribe(value -> calls.add("late"));
        fails(IllegalStateException.class, () -> failing.publish("failure")); equal(List.of("first"),calls);
        fails(NullPointerException.class, () -> subject.subscribe(null));
        fails(NullPointerException.class, () -> subject.publish(null));
        System.out.println("All Observer behavior checks passed.");
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
