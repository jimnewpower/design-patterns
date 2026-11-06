import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class StateDemo {
    public static void main(String[] args) {
        var workflow = new Workflow();
        System.out.println(workflow.status()); workflow.submit();
        System.out.println(workflow.status()); workflow.approve();
        System.out.println(workflow.status());
    }

    interface ReviewState {
        String label();
        default void submit(Workflow context) { throw new IllegalStateException("Cannot submit from " + label()); }
        default void approve(Workflow context) { throw new IllegalStateException("Cannot approve from " + label()); }
        default void reopen(Workflow context) { throw new IllegalStateException("Cannot reopen from " + label()); }
    }
    // Stateless state objects can be shared; each context owns its current state.
    enum Draft implements ReviewState {
        INSTANCE;
        public String label() { return "Draft"; }
        public void submit(Workflow context) { context.transition(Review.INSTANCE); }
    }
    enum Review implements ReviewState {
        INSTANCE;
        public String label() { return "Review"; }
        public void approve(Workflow context) { context.transition(Approved.INSTANCE); }
        public void reopen(Workflow context) { context.transition(Draft.INSTANCE); }
    }
    enum Approved implements ReviewState {
        INSTANCE;
        public String label() { return "Approved"; }
        public void reopen(Workflow context) { context.transition(Draft.INSTANCE); }
    }
    static final class Workflow {
        private ReviewState state = Draft.INSTANCE;
        private void transition(ReviewState next) { state = Objects.requireNonNull(next); }
        String status() { return state.label(); }
        void submit() { state.submit(this); }
        void approve() { state.approve(this); }
        void reopen() { state.reopen(this); }
    }
}
