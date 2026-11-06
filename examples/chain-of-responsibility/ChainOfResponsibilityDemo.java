import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        Handler chain = new LimitApprover("Team lead", 100,
                new LimitApprover("Manager", 1000, new Reject()));
        System.out.println(chain.handle(new Request("Tool purchase", 75)));
        System.out.println(chain.handle(new Request("Equipment", 500)));
        System.out.println(chain.handle(new Request("Facility", 2000)));
    }

    record Request(String purpose, long cost) {
        Request { Objects.requireNonNull(purpose); if (cost < 0) throw new IllegalArgumentException("Negative cost"); }
    }
    interface Handler { String handle(Request request); }
    static final class LimitApprover implements Handler {
        private final String name;
        private final long limit;
        private final Handler next;
        LimitApprover(String name, long limit, Handler next) {
            this.name = Objects.requireNonNull(name);
            if (limit < 0) throw new IllegalArgumentException("Negative limit");
            this.limit = limit; this.next = Objects.requireNonNull(next);
        }
        public String handle(Request request) {
            Objects.requireNonNull(request);
            return request.cost() <= limit ? "Approved by " + name : next.handle(request);
        }
    }
    static final class Reject implements Handler {
        public String handle(Request request) { Objects.requireNonNull(request); return "Rejected: no approval limit covers request"; }
    }
}
