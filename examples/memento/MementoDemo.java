import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class MementoDemo {
    public static void main(String[] args) {
        var editor = new Editor();
        var history = new History(editor);
        editor.set("Draft", List.of("Cameo")); history.checkpoint();
        editor.set("Revised", List.of("Cameo", "GitLab"));
        history.undo();
        System.out.println(editor.view());
    }

    static final class Editor {
        private String title = "Untitled";
        private List<String> tools = List.of();
        // Opaque snapshot: caretaker can hold it but cannot inspect its fields.
        static final class Snapshot {
            private final Editor owner;
            private final String title;
            private final List<String> tools;
            private Snapshot(Editor owner, String title, List<String> tools) {
                this.owner = owner; this.title = title; this.tools = List.copyOf(tools);
            }
        }
        void set(String title, List<String> tools) {
            String validTitle = Objects.requireNonNull(title);
            List<String> validTools = List.copyOf(tools);
            this.title = validTitle; this.tools = validTools;
        }
        Snapshot save() { return new Snapshot(this, title, tools); }
        void restore(Snapshot snapshot) {
            Objects.requireNonNull(snapshot);
            if (snapshot.owner != this) throw new IllegalArgumentException("Snapshot belongs to another editor");
            title = snapshot.title; tools = snapshot.tools;
        }
        String view() { return title + ": " + String.join(", ",tools); }
    }
    static final class History {
        private final Editor editor;
        private final Deque<Editor.Snapshot> snapshots = new ArrayDeque<>();
        History(Editor editor) { this.editor = Objects.requireNonNull(editor); }
        void checkpoint() { snapshots.push(editor.save()); }
        boolean undo() {
            if (snapshots.isEmpty()) return false;
            editor.restore(snapshots.peek()); snapshots.pop(); return true;
        }
    }
}
