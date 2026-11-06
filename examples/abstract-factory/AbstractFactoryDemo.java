import java.util.List;

/** Run with Java 21: java AbstractFactoryDemo.java text (or html). */
public class AbstractFactoryDemo {
    public static void main(String[] args) {
        String format = args.length == 0 ? "text" : args[0];
        ReportComponentFactory factory = switch (format.toLowerCase(java.util.Locale.ROOT)) {
            case "html" -> new HtmlReportFactory();
            case "text" -> new TextReportFactory();
            default -> throw new IllegalArgumentException("Use html or text: " + format);
        };
        System.out.println(new ReportGenerator(factory).generate(
                "Engineering Tools", List.of("Cameo", "Windchill", "GitLab")));
    }

    // Abstract products: capabilities the client uses.
    interface HeadingRenderer {
        String render(String title);
    }
    interface TableRenderer {
        String render(String columnName, List<String> values);
    }

    // HTML product family.
    static final class HtmlHeadingRenderer implements HeadingRenderer {
        public String render(String title) {
            return "<h1>" + Html.escape(title) + "</h1>";
        }
    }
    static final class HtmlTableRenderer implements TableRenderer {
        public String render(String columnName, List<String> values) {
            StringBuilder output = new StringBuilder("<table>\n  <tr><th>");
            output.append(Html.escape(columnName)).append("</th></tr>\n");
            for (String value : values) {
                output.append("  <tr><td>").append(Html.escape(value)).append("</td></tr>\n");
            }
            return output.append("</table>").toString();
        }
    }

    // Plain-text product family.
    static final class TextHeadingRenderer implements HeadingRenderer {
        public String render(String title) {
            return title + "\n" + "=".repeat(title.length());
        }
    }
    static final class TextTableRenderer implements TableRenderer {
        public String render(String columnName, List<String> values) {
            StringBuilder output = new StringBuilder(columnName);
            for (String value : values) output.append("\n- ").append(value);
            return output.toString();
        }
    }

    // Abstract factory: one creation method per product type.
    interface ReportComponentFactory {
        HeadingRenderer createHeadingRenderer();
        TableRenderer createTableRenderer();
    }
    static final class HtmlReportFactory implements ReportComponentFactory {
        public HeadingRenderer createHeadingRenderer() { return new HtmlHeadingRenderer(); }
        public TableRenderer createTableRenderer() { return new HtmlTableRenderer(); }
    }
    static final class TextReportFactory implements ReportComponentFactory {
        public HeadingRenderer createHeadingRenderer() { return new TextHeadingRenderer(); }
        public TableRenderer createTableRenderer() { return new TextTableRenderer(); }
    }

    // Client: it assembles reports without knowing the selected format.
    static final class ReportGenerator {
        private final HeadingRenderer heading;
        private final TableRenderer table;
        ReportGenerator(ReportComponentFactory factory) {
            heading = factory.createHeadingRenderer();
            table = factory.createTableRenderer();
        }
        String generate(String title, List<String> tools) {
            return heading.render(title) + "\n\n" + table.render("Tool", tools);
        }
    }

    // Escapes HTML text content. Not a general-purpose sanitizer or attribute escaper.
    static final class Html {
        private Html() { }
        static String escape(String value) {
            return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
