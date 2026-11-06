import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/** Java 21: java AdapterDemo.java */
public class AdapterDemo {
    public static void main(String[] args) {
        var legacy = new LegacyCadClient(Map.of("BRACKET-01", new BigDecimal("2.5")));
        PartLengthSource source = new CadLengthAdapter(legacy);
        System.out.println("BRACKET-01: " + source.lengthMillimeters("BRACKET-01").toPlainString() + " mm");
        System.out.println("Fits 70 mm limit: " + new ClearanceCheck(source).fits("BRACKET-01", new BigDecimal("70")));
    }

    // Target: the interface expected by the application.
    interface PartLengthSource {
        // Nonnegative millimeters; unknown/invalid IDs throw IllegalArgumentException.
        BigDecimal lengthMillimeters(String partId);
    }

    // Simulated external API: incompatible method, result shape, units, and exception.
    record LegacyPart(String number, BigDecimal lengthInches) { }
    static final class LegacyPartMissingException extends RuntimeException {
        LegacyPartMissingException(String number) { super("Legacy part missing: " + number); }
    }
    static final class LegacyCadClient {
        private final Map<String, BigDecimal> lengths;
        LegacyCadClient(Map<String, BigDecimal> lengths) { this.lengths = Map.copyOf(lengths); }
        public LegacyPart lookupPart(String number) {
            BigDecimal inches = lengths.get(number);
            if (inches == null) throw new LegacyPartMissingException(number);
            return new LegacyPart(number, inches);
        }
    }

    // Object adapter: wrap the existing API, implement the target contract.
    static final class CadLengthAdapter implements PartLengthSource {
        private static final BigDecimal MM_PER_INCH = new BigDecimal("25.4");
        private final LegacyCadClient legacy;
        CadLengthAdapter(LegacyCadClient legacy) { this.legacy = Objects.requireNonNull(legacy, "legacy"); }
        @Override
        public BigDecimal lengthMillimeters(String partId) {
            Objects.requireNonNull(partId, "partId");
            if (partId.isBlank()) throw new IllegalArgumentException("Part ID must not be blank");
            try {
                LegacyPart part = legacy.lookupPart(partId);
                if (part.lengthInches().signum() < 0) throw new IllegalStateException("Negative legacy length: " + partId);
                return part.lengthInches().multiply(MM_PER_INCH);
            } catch (LegacyPartMissingException missing) {
                throw new IllegalArgumentException("Unknown part: " + partId, missing);
            }
        }
    }

    // Client: uses millimeters and the target interface only.
    static final class ClearanceCheck {
        private final PartLengthSource source;
        ClearanceCheck(PartLengthSource source) { this.source = Objects.requireNonNull(source, "source"); }
        public boolean fits(String partId, BigDecimal maxMillimeters) {
            Objects.requireNonNull(maxMillimeters, "maxMillimeters");
            if (maxMillimeters.signum() < 0) throw new IllegalArgumentException("Limit must not be negative");
            return source.lengthMillimeters(partId).compareTo(maxMillimeters) <= 0;
        }
    }
}
