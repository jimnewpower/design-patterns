import java.util.Objects;
import java.util.Set;

/** Self-contained Java 21 teaching example. */
public class InterpreterDemo {
    public static void main(String[] args) {
        Expr rule = new And(new Tag("approved"), new Not(new Tag("obsolete")));
        System.out.println("Approved item: " + rule.evaluate(Set.of("approved", "mechanical")));
        System.out.println("Obsolete item: " + rule.evaluate(Set.of("approved", "obsolete")));
    }

    // Grammar: expr := tag | AND(expr, expr) | OR(expr, expr) | NOT(expr).
    // The example constructs an AST directly; it does not parse text.
    sealed interface Expr permits Tag, And, Or, Not {
        boolean evaluate(Set<String> tags);
    }
    record Tag(String name) implements Expr {
        Tag { Objects.requireNonNull(name); if (name.isBlank()) throw new IllegalArgumentException("Empty tag"); }
        public boolean evaluate(Set<String> tags) { return Objects.requireNonNull(tags).contains(name); }
    }
    record And(Expr left, Expr right) implements Expr {
        And { Objects.requireNonNull(left); Objects.requireNonNull(right); }
        public boolean evaluate(Set<String> tags) { Objects.requireNonNull(tags); return left.evaluate(tags) && right.evaluate(tags); }
    }
    record Or(Expr left, Expr right) implements Expr {
        Or { Objects.requireNonNull(left); Objects.requireNonNull(right); }
        public boolean evaluate(Set<String> tags) { Objects.requireNonNull(tags); return left.evaluate(tags) || right.evaluate(tags); }
    }
    record Not(Expr operand) implements Expr {
        Not { Objects.requireNonNull(operand); }
        public boolean evaluate(Set<String> tags) { return !operand.evaluate(Objects.requireNonNull(tags)); }
    }
}
