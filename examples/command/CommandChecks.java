import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class CommandChecks {
    public static void main(String[] args) throws Exception {
        var document = new CommandDemo.Document();
        var history = new CommandDemo.History();
        equal(false, history.undo());
        var a = new CommandDemo.AppendText(document, "A");
        var b = new CommandDemo.AppendText(document, "B");
        history.execute(a); history.execute(b);
        equal("AB", document.text());
        equal(true, history.undo()); equal("A", document.text());
        equal(true, history.undo()); equal("", document.text());
        history.execute(a); equal("A", document.text());
        fails(IllegalStateException.class, () -> history.execute(a)); equal(1, history.size());
        document.replace("External edit");
        fails(IllegalStateException.class, history::undo); equal(1, history.size());
        var broken = new CommandDemo.Command() { public void execute() { throw new IllegalArgumentException(); } public void undo() {} };
        fails(IllegalArgumentException.class, () -> history.execute(broken)); equal(1, history.size());
        System.out.println("All Command behavior checks passed.");
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
