# Pattern Atlas

A searchable field guide to all **23 Gang of Four design patterns**, with self-contained Java 21 examples.

## Catalog

| Category | Guides |
| --- | --- |
| Creational | Abstract Factory, Builder, Factory Method, Prototype, Singleton |
| Structural | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| Behavioral | Chain of Responsibility, Command, Interpreter, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor |

Every guide includes a plain-English explanation, a problem statement, at least three use cases, a role diagram, implementation walkthrough, runtime trace, complete source listing and download, compile/run commands, expected output, behavior checks, extension work, tradeoffs, related-pattern links, and references.

The homepage groups patterns by category. Guides include category navigation, a table of contents, previous/next links, light/dark themes, copy controls, and print styles. Pagefind indexes all guides without a search server. Examples are teaching implementations with explicit limits, not production SDK integrations or substitutes for domain validation.

## Development

Requires Node.js 24 LTS, npm, and a JDK 21 for the Java checks.

```sh
npm ci
npm run dev
```

Use the printed local URL under `/design-patterns/`. Search is generated at build time, so use production preview to test it:

```sh
npm run check
npm run build
npm test
npm run preview
```

The default base is `/design-patterns` for GitHub Pages. To host at the root, build and verify with:

```sh
SITE_BASE=/ npm run build
SITE_BASE=/ npm test
```

## Java examples

Each `examples/<pattern>/` directory contains its own `*Demo.java` and `*Checks.java`. There are no external Java dependencies or shared classpath requirements.

```sh
npm run test:java
# Or select one pattern:
node scripts/run-java.mjs visitor
```

The runner compiles into temporary directories, executes behavior checks with timeouts, and runs each demo. For the 17 newly completed guides it compares actual stdout against `expected.txt`; site checks verify the same fixture appears in the guide. No generated class files are retained.

You can also run examples manually:

```sh
cd examples/visitor
javac --release 21 VisitorDemo.java VisitorChecks.java
java VisitorChecks
java VisitorDemo
# Single-file source launcher:
java VisitorDemo.java
```

Abstract Factory, Builder, and Factory Method additionally accept `text` or `html` as demo arguments. The other demos take no arguments. The Singleton and Proxy checks include concurrent access tests; most teaching implementations deliberately assume single-threaded use.

## Content and source integrity

Guides are in `src/content/patterns/*.mdx`. Content collection validation checks category, order, intent, status, keywords, and related pattern slugs. The registry `src/data/examples.json` maps all 23 patterns to demo/check classes, download paths, and smoke-run arguments.

The displayed listings and downloads import the same Java files CI compiles. The six earlier guides retain their existing source components and download URLs; the remaining guides share `SourceListing.astro` and a static download route. This keeps links stable while avoiding one new component and route per pattern.

`PatternDiagram.astro` renders accessible, focused UML-style role diagrams. They show selected types and labeled collaborations; the role table covers the full example. `RuntimeSteps.astro` shows an ordered concrete execution trace. Diagrams and content use theme-aware colors and print styling.

`npm test` checks all guides are complete, catalog links and local anchors resolve, diagrams exist, displayed code and downloads match source, expected-output blocks match fixtures, and Pagefind assets were generated. It does not replace browser layout or interaction testing.

## CI and deployment

PR CI runs the Astro check, production build, Pagefind indexing, and site integrity checks. A separate Java job compiles/tests all 23 examples on JDK 21 and verifies demo outputs. The production site is retained as a workflow artifact.

GitHub Pages deployment remains opt-in. After merging, enable **Settings → Pages → Source: GitHub Actions**, then run **Deploy to GitHub Pages** on `main`. Expected project URL: `https://jimnewpower.github.io/design-patterns/`. Content PRs do not deploy automatically.

Use **Print / save PDF** on a guide for a print-oriented export. Automated curated PDF generation is not included.

## References and licensing

Each guide links to the original GoF book and relevant Java documentation, with secondary explanations where useful. Text, diagrams, and examples are original teaching material and do not reproduce the book. No project-wide redistribution license has been selected. Dependencies retain their respective licenses.
