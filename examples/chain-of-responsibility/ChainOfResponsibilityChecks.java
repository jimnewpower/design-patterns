import java.util.Objects;

public class ChainOfResponsibilityChecks {
    public static void main(String[] args) throws Exception {
        var passed = new java.util.concurrent.atomic.AtomicInteger();
        ChainOfResponsibilityDemo.Handler fallback = r -> { passed.incrementAndGet(); return "Escalated"; };
        var handler = new ChainOfResponsibilityDemo.LimitApprover("Lead", 100, fallback);
        equal("Approved by Lead", handler.handle(new ChainOfResponsibilityDemo.Request("Boundary", 100)));
        equal(0, passed.get());
        equal("Escalated", handler.handle(new ChainOfResponsibilityDemo.Request("Over", 101)));
        equal(1, passed.get());
        equal("Approved by Lead", handler.handle(new ChainOfResponsibilityDemo.Request("Free", 0)));
        equal("Rejected: no approval limit covers request", new ChainOfResponsibilityDemo.Reject().handle(new ChainOfResponsibilityDemo.Request("x", 1)));
        fails(IllegalArgumentException.class, () -> new ChainOfResponsibilityDemo.Request("x", -1));
        fails(IllegalArgumentException.class, () -> new ChainOfResponsibilityDemo.LimitApprover("x", -1, fallback));
        fails(NullPointerException.class, () -> handler.handle(null));
        System.out.println("All ChainOfResponsibility behavior checks passed.");
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
