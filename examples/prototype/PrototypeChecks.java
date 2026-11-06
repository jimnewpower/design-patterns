public class PrototypeChecks {
    public static void main(String[] args) {
        var original = new PrototypeDemo.ReportTemplate("Engineering");
        original.addTool("Cameo");
        original.footer("Internal");
        var copy = original.copy();
        if (copy == original) throw new AssertionError("Copy must have distinct identity");
        equal(original.render(), copy.render());
        copy.rename("Software");
        copy.addTool("GitLab");
        copy.footer("Draft");
        equal("Engineering\n- Cameo\n[Footer: Internal]", original.render());
        equal("Software\n- Cameo\n- GitLab\n[Footer: Draft]", copy.render());
        original.addTool("Windchill");
        original.footer("Approved");
        equal("Software\n- Cameo\n- GitLab\n[Footer: Draft]", copy.render());
        var registry = new PrototypeDemo.PrototypeRegistry();
        registry.register("baseline", copy);
        copy.addTool("Later");
        copy.footer("Changed");
        var first = registry.create("baseline");
        equal("Software\n- Cameo\n- GitLab\n[Footer: Draft]", first.render());
        first.addTool("Custom");
        first.footer("");
        var second = registry.create("baseline");
        equal("Software\n- Cameo\n- GitLab\n[Footer: Draft]", second.render());
        if (first == second) throw new AssertionError("Registry returns independent copies");
        equal("Empty", new PrototypeDemo.ReportTemplate("Empty").copy().render());
        registry.register("baseline", new PrototypeDemo.ReportTemplate("Replacement"));
        equal("Replacement", registry.create("baseline").render());
        fails(IllegalArgumentException.class, () -> registry.create("missing"));
        fails(IllegalArgumentException.class, () -> registry.register(" ", original));
        fails(NullPointerException.class, () -> registry.register("null", null));
        fails(NullPointerException.class, () -> registry.create(null));
        fails(IllegalArgumentException.class, () -> original.rename(" "));
        fails(IllegalArgumentException.class, () -> original.addTool(""));
        fails(NullPointerException.class, () -> original.footer(null));
        fails(NullPointerException.class, () -> new PrototypeDemo.ReportTemplate(null));
        System.out.println("All Prototype behavior checks passed.");
    }
    static void equal(String expected, String actual) {
        if (!expected.equals(actual)) throw new AssertionError("Expected:\n" + expected + "\nActual:\n" + actual);
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
