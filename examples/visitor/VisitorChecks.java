import java.util.List;
import java.util.Objects;

public class VisitorChecks {
    public static void main(String[] args) throws Exception {
        var leaf = new VisitorDemo.Part("A",10);
        var nested = new VisitorDemo.Assembly("Outer",List.of(new VisitorDemo.Assembly("Inner",List.of(leaf)),new VisitorDemo.Part("B",5)));
        equal(15L,nested.accept(new VisitorDemo.MassVisitor()));
        equal("Outer[Inner[A], B]",nested.accept(new VisitorDemo.OutlineVisitor()));
        equal(0L,new VisitorDemo.Assembly("Empty",List.of()).accept(new VisitorDemo.MassVisitor()));
        // Add an operation without changing the element classes.
        VisitorDemo.Visitor<Integer> count = new VisitorDemo.Visitor<>() {
            public Integer visitPart(VisitorDemo.Part part) { return 1; }
            public Integer visitAssembly(VisitorDemo.Assembly group) { return group.children().stream().mapToInt(item -> item.accept(this)).sum(); }
        };
        equal(2,nested.accept(count));
        fails(IllegalArgumentException.class, () -> new VisitorDemo.Part("Bad",-1));
        fails(NullPointerException.class, () -> new VisitorDemo.Assembly("Bad",null));
        fails(ArithmeticException.class, () -> new VisitorDemo.Assembly("Overflow",List.of(new VisitorDemo.Part("Huge",Long.MAX_VALUE),new VisitorDemo.Part("One",1))).accept(new VisitorDemo.MassVisitor()));
        System.out.println("All Visitor behavior checks passed.");
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
