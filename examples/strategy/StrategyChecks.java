import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class StrategyChecks {
    public static void main(String[] args) throws Exception {
        var input = new java.util.ArrayList<>(List.of("B","A","A"));
        equal("A, A, B",new StrategyDemo.ToolReport(new StrategyDemo.Alphabetical()).render(input));
        equal(List.of("B","A","A"),input);
        equal("B, A, A",new StrategyDemo.ToolReport(new StrategyDemo.AsProvided()).render(input));
        equal("",new StrategyDemo.ToolReport(new StrategyDemo.Alphabetical()).render(List.of()));
        var reverse = new StrategyDemo.ToolReport(values -> values.stream().sorted(Comparator.reverseOrder()).toList());
        equal("B, A, A",reverse.render(input));
        fails(NullPointerException.class, () -> new StrategyDemo.ToolReport(null));
        fails(NullPointerException.class, () -> reverse.render(java.util.Arrays.asList("A",null)));
        fails(NullPointerException.class, () -> new StrategyDemo.ToolReport(values -> null).render(List.of()));
        System.out.println("All Strategy behavior checks passed.");
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
