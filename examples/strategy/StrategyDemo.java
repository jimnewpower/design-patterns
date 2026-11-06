import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class StrategyDemo {
    public static void main(String[] args) {
        var tools = List.of("Windchill", "Cameo", "GitLab");
        System.out.println(new ToolReport(new Alphabetical()).render(tools));
        System.out.println(new ToolReport(new AsProvided()).render(tools));
    }

    interface Ordering { List<String> order(List<String> values); }
    static final class Alphabetical implements Ordering {
        public List<String> order(List<String> values) { return values.stream().sorted(Comparator.naturalOrder()).toList(); }
    }
    static final class AsProvided implements Ordering {
        public List<String> order(List<String> values) { return List.copyOf(values); }
    }
    static final class ToolReport {
        private final Ordering ordering;
        ToolReport(Ordering ordering) { this.ordering = Objects.requireNonNull(ordering); }
        String render(List<String> values) {
            List<String> ordered = List.copyOf(ordering.order(List.copyOf(values)));
            return String.join(", ",ordered);
        }
    }
}
