import java.util.List;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class VisitorDemo {
    public static void main(String[] args) {
        Item product = new Assembly("Sensor", List.of(new Part("Housing", 100), new Part("Board", 30)));
        System.out.println("Mass: " + product.accept(new MassVisitor()) + " g");
        System.out.println(product.accept(new OutlineVisitor()));
    }

    sealed interface Item permits Part, Assembly { <R> R accept(Visitor<R> visitor); }
    interface Visitor<R> {
        R visitPart(Part part);
        R visitAssembly(Assembly assembly);
    }
    record Part(String name, long grams) implements Item {
        Part { Objects.requireNonNull(name); if (grams < 0) throw new IllegalArgumentException("Negative mass"); }
        public <R> R accept(Visitor<R> visitor) { return visitor.visitPart(this); }
    }
    record Assembly(String name, List<Item> children) implements Item {
        Assembly { Objects.requireNonNull(name); children = List.copyOf(children); }
        public <R> R accept(Visitor<R> visitor) { return visitor.visitAssembly(this); }
    }
    static final class MassVisitor implements Visitor<Long> {
        public Long visitPart(Part part) { return part.grams(); }
        public Long visitAssembly(Assembly assembly) {
            long total = 0;
            for (Item child : assembly.children()) total = Math.addExact(total,child.accept(this));
            return total;
        }
    }
    static final class OutlineVisitor implements Visitor<String> {
        public String visitPart(Part part) { return part.name(); }
        public String visitAssembly(Assembly assembly) {
            return assembly.name() + "[" + String.join(", ",assembly.children().stream().map(item -> item.accept(this)).toList()) + "]";
        }
    }
}
