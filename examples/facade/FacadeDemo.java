import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class FacadeDemo {
    public static void main(String[] args) {
        var events = new ArrayList<String>();
        var facade = new ReleaseFacade(
                release -> { if (release.items().isEmpty()) throw new IllegalArgumentException("Empty release"); },
                release -> { events.add("Archived " + release.name()); return "R-001"; },
                id -> events.add("Notified " + id));
        System.out.println(facade.publish(new Release("Tools", List.of("Cameo"))));
        events.forEach(System.out::println);
    }

    record Release(String name, List<String> items) {
        Release { Objects.requireNonNull(name); items = List.copyOf(items); }
    }
    interface Validator { void validate(Release release); }
    interface Archive { String store(Release release); }
    interface Notifier { void notifyRelease(String id); }
    static final class ReleaseFacade {
        private final Validator validator;
        private final Archive archive;
        private final Notifier notifier;
        ReleaseFacade(Validator validator, Archive archive, Notifier notifier) {
            this.validator = Objects.requireNonNull(validator);
            this.archive = Objects.requireNonNull(archive);
            this.notifier = Objects.requireNonNull(notifier);
        }
        public String publish(Release release) {
            validator.validate(Objects.requireNonNull(release));
            String id = Objects.requireNonNull(archive.store(release));
            notifier.notifyRelease(id);
            return id;
        }
    }
}
