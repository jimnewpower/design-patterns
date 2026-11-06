import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class MediatorDemo {
    public static void main(String[] args) {
        var mediator = new ExportMediator();
        var format = new FormatSelector(mediator);
        var preview = new Preview();
        mediator.connect(format, preview);
        format.select("html");
        System.out.println(preview.text());
    }

    interface Mediator { void formatChanged(FormatSelector sender, String format); }
    static final class FormatSelector {
        private final Mediator mediator;
        private String format = "text";
        FormatSelector(Mediator mediator) { this.mediator = Objects.requireNonNull(mediator); }
        public void select(String format) {
            mediator.formatChanged(this, Objects.requireNonNull(format));
            this.format = format;
        }
        String format() { return format; }
    }
    static final class Preview {
        private String text = "No preview";
        void show(String value) { text = value; }
        String text() { return text; }
    }
    static final class ExportMediator implements Mediator {
        private final Map<FormatSelector, Preview> connections = new HashMap<>();
        void connect(FormatSelector selector, Preview preview) {
            connections.put(Objects.requireNonNull(selector), Objects.requireNonNull(preview));
        }
        public void formatChanged(FormatSelector sender, String format) {
            Preview preview = connections.get(sender);
            if (preview == null) throw new IllegalStateException("Unregistered selector");
            String text = switch (format) {
                case "text" -> "Preview: Tools";
                case "html" -> "Preview: <h1>Tools</h1>";
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            };
            preview.show(text);
        }
    }
}
