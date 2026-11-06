import java.util.List;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class CompositeDemo {
    public static void main(String[] args) {
        Component assembly = new Assembly("Sensor", List.of(
                new Part("Housing", 100), new Assembly("Electronics", List.of(new Part("Board", 30), new Part("Cable", 10)))));
        System.out.println(assembly.name() + ": " + assembly.grams() + " g");
    }

    sealed interface Component permits Part, Assembly {
        String name();
        long grams();
    }
    record Part(String name, long grams) implements Component {
        Part { Objects.requireNonNull(name); if (grams < 0) throw new IllegalArgumentException("Negative mass"); }
    }
    // Immutable children make this an acyclic tree under ordinary construction.
    record Assembly(String name, List<Component> children) implements Component {
        Assembly { Objects.requireNonNull(name); children = List.copyOf(children); }
        public long grams() {
            long total = 0;
            for (Component child : children) total = Math.addExact(total, child.grams());
            return total;
        }
    }
}
