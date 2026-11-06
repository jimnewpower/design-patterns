import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FacadeChecks {
    public static void main(String[] args) throws Exception {
        var events = new ArrayList<String>();
        var release = new FacadeDemo.Release("Tools", List.of("A"));
        var facade = new FacadeDemo.ReleaseFacade(r -> events.add("validate"), r -> { events.add("store"); return "id"; }, id -> events.add("notify:" + id));
        equal("id", facade.publish(release));
        equal(List.of("validate", "store", "notify:id"), events);
        events.clear();
        var invalid = new FacadeDemo.ReleaseFacade(r -> { throw new IllegalArgumentException(); }, r -> { events.add("store"); return "id"; }, id -> events.add("notify"));
        fails(IllegalArgumentException.class, () -> invalid.publish(release));
        equal(List.of(), events);
        var failedStore = new FacadeDemo.ReleaseFacade(r -> {}, r -> { throw new IllegalStateException(); }, id -> events.add("notify"));
        fails(IllegalStateException.class, () -> failedStore.publish(release));
        equal(List.of(), events);
        var failedNotify = new FacadeDemo.ReleaseFacade(r -> {}, r -> { events.add("stored"); return "id"; }, id -> { throw new IllegalStateException(); });
        fails(IllegalStateException.class, () -> failedNotify.publish(release));
        equal(List.of("stored"), events); // No fictitious transaction rollback.
        fails(NullPointerException.class, () -> facade.publish(null));
        System.out.println("All Facade behavior checks passed.");
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
