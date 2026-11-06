import java.util.Objects;
import java.util.function.Supplier;

/** Self-contained Java 21 teaching example. */
public class ProxyDemo {
    public static void main(String[] args) {
        Document document = new LazyDocument(() -> new StoredDocument("Engineering report"));
        System.out.println("Proxy ready");
        System.out.println(document.read());
        System.out.println(document.read());
    }

    interface Document { String read(); }
    record StoredDocument(String content) implements Document {
        StoredDocument { Objects.requireNonNull(content); }
        public String read() { return content; }
    }
    // Virtual proxy. Initialization failures leave it empty so a later call may retry.
    static final class LazyDocument implements Document {
        private final Supplier<? extends Document> loader;
        private Document delegate;
        LazyDocument(Supplier<? extends Document> loader) { this.loader = Objects.requireNonNull(loader); }
        public synchronized String read() {
            if (delegate == null) delegate = Objects.requireNonNull(loader.get(), "Loaded document");
            return delegate.read();
        }
    }
}
