import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class DecoratorDemo {
    public static void main(String[] args) {
        Report report = new Footer(new Header(new PlainReport("Tools: Cameo, GitLab"), "Engineering"), "Internal");
        System.out.println(report.render());
    }

    interface Report { String render(); }
    record PlainReport(String text) implements Report {
        PlainReport { Objects.requireNonNull(text); }
        public String render() { return text; }
    }
    abstract static class ReportDecorator implements Report {
        protected final Report wrapped;
        ReportDecorator(Report wrapped) { this.wrapped = Objects.requireNonNull(wrapped); }
    }
    static final class Header extends ReportDecorator {
        private final String title;
        Header(Report wrapped, String title) { super(wrapped); this.title = Objects.requireNonNull(title); }
        public String render() { return title + "\n" + wrapped.render(); }
    }
    static final class Footer extends ReportDecorator {
        private final String note;
        Footer(Report wrapped, String note) { super(wrapped); this.note = Objects.requireNonNull(note); }
        public String render() { return wrapped.render() + "\n[" + note + "]"; }
    }
}
