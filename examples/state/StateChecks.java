import java.util.Objects;

public class StateChecks {
    public static void main(String[] args) throws Exception {
        var a = new StateDemo.Workflow(); var b = new StateDemo.Workflow();
        fails(IllegalStateException.class,a::approve); equal("Draft",a.status());
        a.submit(); equal("Review",a.status()); equal("Draft",b.status());
        fails(IllegalStateException.class,a::submit); equal("Review",a.status());
        a.reopen(); equal("Draft",a.status());
        a.submit(); a.approve(); equal("Approved",a.status());
        fails(IllegalStateException.class,a::approve); fails(IllegalStateException.class,a::submit);
        a.reopen(); equal("Draft",a.status());
        fails(IllegalStateException.class,a::reopen);
        System.out.println("All State behavior checks passed.");
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
