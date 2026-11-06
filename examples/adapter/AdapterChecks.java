import java.math.BigDecimal;
import java.util.Map;

/** Dependency-free behavior checks; no -ea flag required. */
public class AdapterChecks {
    public static void main(String[] args) {
        var legacy = new AdapterDemo.LegacyCadClient(Map.of(
                "BRACKET", new BigDecimal("2.5"), "ZERO", BigDecimal.ZERO,
                "FRACTION", new BigDecimal("0.125"), "BAD", new BigDecimal("-1")));
        var source = new AdapterDemo.CadLengthAdapter(legacy);
        equal("63.50", source.lengthMillimeters("BRACKET").toPlainString());
        equal("3.1750", source.lengthMillimeters("FRACTION").toPlainString());
        if (source.lengthMillimeters("ZERO").signum() != 0) throw new AssertionError("Zero length");
        // Adaptation does not modify the original API's inch-valued data.
        equal("2.5", legacy.lookupPart("BRACKET").lengthInches().toPlainString());
        var check = new AdapterDemo.ClearanceCheck(source);
        if (!check.fits("BRACKET", new BigDecimal("63.500"))) throw new AssertionError("Equal boundary should fit");
        if (check.fits("BRACKET", new BigDecimal("63.49"))) throw new AssertionError("Over limit should fail");
        if (!check.fits("ZERO", BigDecimal.ZERO)) throw new AssertionError("Zero boundary");
        // An alternate target implementation works without changing the client.
        var fakeCheck = new AdapterDemo.ClearanceCheck(id -> BigDecimal.TEN);
        if (!fakeCheck.fits("ANY", BigDecimal.TEN)) throw new AssertionError("Injected source");
        try {
            source.lengthMillimeters("MISSING");
            throw new AssertionError("Unknown part should fail");
        } catch (IllegalArgumentException expected) {
            if (!(expected.getCause() instanceof AdapterDemo.LegacyPartMissingException)) {
                throw new AssertionError("Preserve the vendor failure as cause", expected);
            }
        }
        fails(IllegalArgumentException.class, () -> source.lengthMillimeters(" "));
        fails(NullPointerException.class, () -> source.lengthMillimeters(null));
        fails(IllegalStateException.class, () -> source.lengthMillimeters("BAD"));
        fails(NullPointerException.class, () -> new AdapterDemo.CadLengthAdapter(null));
        fails(NullPointerException.class, () -> new AdapterDemo.ClearanceCheck(null));
        fails(NullPointerException.class, () -> check.fits("BRACKET", null));
        fails(IllegalArgumentException.class, () -> check.fits("BRACKET", new BigDecimal("-1")));
        equal("63.50", source.lengthMillimeters("BRACKET").toPlainString());
        System.out.println("All Adapter behavior checks passed.");
    }
    static void equal(String expected, String actual) {
        if (!expected.equals(actual)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    static void fails(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Wrong exception type", error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
