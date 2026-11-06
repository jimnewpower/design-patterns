import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class MementoChecks {
    public static void main(String[] args) throws Exception {
        var editor = new MementoDemo.Editor(); var history = new MementoDemo.History(editor);
        equal(false,history.undo());
        var tools = new java.util.ArrayList<>(List.of("A")); editor.set("First",tools); history.checkpoint(); tools.add("Leak");
        editor.set("Second",List.of("B")); history.checkpoint(); editor.set("Third",List.of());
        equal(true,history.undo()); equal("Second: B",editor.view());
        equal(true,history.undo()); equal("First: A",editor.view());
        var snapshot = editor.save(); editor.set("Fourth",List.of()); editor.restore(snapshot); equal("First: A",editor.view());
        var other = new MementoDemo.Editor(); fails(IllegalArgumentException.class, () -> other.restore(snapshot));
        fails(NullPointerException.class, () -> editor.set("Partial",null)); equal("First: A",editor.view());
        fails(NullPointerException.class, () -> editor.restore(null));
        System.out.println("All Memento behavior checks passed.");
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
