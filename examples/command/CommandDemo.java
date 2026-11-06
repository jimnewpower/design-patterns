import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class CommandDemo {
    public static void main(String[] args) {
        var document = new Document();
        var history = new History();
        history.execute(new AppendText(document, "Engineering"));
        history.execute(new AppendText(document, " report"));
        System.out.println(document.text());
        history.undo();
        System.out.println(document.text());
    }

    static final class Document {
        private String text = "";
        public String text() { return text; }
        void replace(String value) { text = Objects.requireNonNull(value); }
    }
    interface Command { void execute(); void undo(); }
    static final class AppendText implements Command {
        private final Document document;
        private final String addition;
        private String before;
        private String after;
        private boolean applied;
        AppendText(Document document, String addition) {
            this.document = Objects.requireNonNull(document); this.addition = Objects.requireNonNull(addition);
        }
        public void execute() {
            if (applied) throw new IllegalStateException("Already applied");
            before = document.text(); after = before + addition;
            document.replace(after); applied = true;
        }
        public void undo() {
            if (!applied || !document.text().equals(after)) throw new IllegalStateException("Cannot undo current document");
            document.replace(before); applied = false;
        }
    }
    static final class History {
        private final Deque<Command> commands = new ArrayDeque<>();
        public void execute(Command command) {
            Objects.requireNonNull(command).execute(); commands.push(command);
        }
        public boolean undo() {
            if (commands.isEmpty()) return false;
            commands.peek().undo(); commands.pop(); return true;
        }
        public int size() { return commands.size(); }
    }
}
