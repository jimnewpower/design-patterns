import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class FlyweightDemo {
    public static void main(String[] args) {
        var factory = new StyleFactory();
        var a = new Label("Cameo", 10, factory.get("Mono", 12));
        var b = new Label("GitLab", 40, factory.get("Mono", 12));
        System.out.println("Shared style: " + (a.style() == b.style()));
        System.out.println(a.draw());
        System.out.println(b.draw());
    }

    // Intrinsic state is immutable and reusable.
    record Style(String font, int size) {
        Style { Objects.requireNonNull(font); if (font.isBlank() || size <= 0) throw new IllegalArgumentException("Invalid style"); }
    }
    static final class StyleFactory {
        private final Map<Style, Style> cache = new HashMap<>();
        public Style get(String font, int size) {
            Style key = new Style(font, size);
            return cache.computeIfAbsent(key, value -> value);
        }
        public int size() { return cache.size(); }
    }
    // Extrinsic state belongs to the individual placement, not the shared style.
    record Label(String text, int y, Style style) {
        Label { Objects.requireNonNull(text); Objects.requireNonNull(style); }
        String draw() { return text + " @" + y + " [" + style.font() + " " + style.size() + "]"; }
    }
}
