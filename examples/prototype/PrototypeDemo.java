import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Java 21: java PrototypeDemo.java */
public class PrototypeDemo {
    public static void main(String[] args) {
        ReportTemplate original = new ReportTemplate("Engineering Tools");
        original.addTool("Cameo");
        original.addTool("Windchill");
        original.footer("Internal review");
        PrototypeRegistry registry = new PrototypeRegistry();
        registry.register("engineering", original);

        ReportPrototype copy = registry.create("engineering");
        copy.rename("Software Tools");
        copy.addTool("GitLab");
        copy.footer("Draft");
        System.out.println("ORIGINAL\n" + original.render());
        System.out.println("\nCOPY\n" + copy.render());
    }

    // Prototype: clients request a copy through a domain contract.
    interface ReportPrototype {
        ReportPrototype copy();
        void rename(String title);
        void addTool(String tool);
        void footer(String text);
        String render();
    }

    // Mutable nested state: copying the outer object alone is insufficient.
    static final class Layout {
        private String footer = "";
        Layout() { }
        Layout(Layout source) { footer = source.footer; }
    }

    static final class ReportTemplate implements ReportPrototype {
        private String title;
        private final List<String> tools;
        private final Layout layout;

        ReportTemplate(String title) {
            this.title = requiredText(title);
            tools = new ArrayList<>();
            layout = new Layout();
        }

        private ReportTemplate(ReportTemplate source) {
            title = source.title;                     // Immutable string: share safely.
            tools = new ArrayList<>(source.tools);     // Independent mutable list.
            layout = new Layout(source.layout);       // Independent nested object.
        }

        @Override
        public ReportTemplate copy() { return new ReportTemplate(this); }
        @Override
        public void rename(String title) { this.title = requiredText(title); }
        @Override
        public void addTool(String tool) { tools.add(requiredText(tool)); }
        @Override
        public void footer(String text) { layout.footer = Objects.requireNonNull(text, "footer"); }
        @Override
        public String render() {
            StringBuilder output = new StringBuilder(title);
            for (String tool : tools) output.append("\n- ").append(tool);
            if (!layout.footer.isEmpty()) output.append("\n[Footer: ").append(layout.footer).append("]");
            return output.toString();
        }
    }

    // Optional registry: protect the stored prototype on registration and retrieval.
    static final class PrototypeRegistry {
        private final Map<String, ReportPrototype> prototypes = new HashMap<>();
        public void register(String name, ReportPrototype prototype) {
            prototypes.put(requiredText(name), Objects.requireNonNull(prototype, "prototype").copy());
        }
        public ReportPrototype create(String name) {
            ReportPrototype prototype = prototypes.get(requiredText(name));
            if (prototype == null) throw new IllegalArgumentException("Unknown prototype: " + name);
            return prototype.copy();
        }
    }

    private static String requiredText(String value) {
        Objects.requireNonNull(value, "text");
        if (value.isBlank()) throw new IllegalArgumentException("Text must not be blank");
        return value;
    }
}
