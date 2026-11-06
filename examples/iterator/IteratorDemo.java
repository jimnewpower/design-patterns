import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Self-contained Java 21 teaching example. */
public class IteratorDemo {
    public static void main(String[] args) {
        var catalog = new ToolCatalog(List.of("Cameo", "GitLab"));
        for (String tool : catalog) System.out.println(tool);
    }

    static final class ToolCatalog implements Iterable<String> {
        private final List<String> tools;
        ToolCatalog(List<String> tools) { this.tools = List.copyOf(tools); }
        public Iterator<String> iterator() {
            return new Iterator<>() {
                private int index;
                public boolean hasNext() { return index < tools.size(); }
                public String next() {
                    if (!hasNext()) throw new NoSuchElementException("End of catalog");
                    return tools.get(index++);
                }
            };
        }
    }
}
