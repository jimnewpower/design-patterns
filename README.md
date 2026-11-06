# Pattern Atlas

A searchable field guide to all 23 Gang of Four design patterns, with Java examples.

## First release

- Atlas homepage with the three categories and links to every pattern.
- Field Guide layout with category navigation, table of contents, adjacent patterns, and print styles.
- Complete Abstract Factory guide: problem, four use cases, class/sequence diagrams, Java walkthrough, runnable source, Markdown extension, verification, tradeoffs, and references.
- Complete Builder guide: GoF builder/director roles, fluent Java idiom, two representations, diagrams, lifecycle rules, extension exercise, and behavior checks.
- Complete Factory Method guide: creator/product roles, an inherited export workflow, overridable creation hooks, diagrams, comparisons, extension exercise, and behavior checks.
- Clearly labeled outlines for the other 20 guides.
- Full-text Pagefind search and a persistent light/dark toggle (light by default).

## Development

Requires Node.js 24 LTS and npm. Java checks require a JDK 21.

```sh
npm ci
npm run dev
```

Open the printed local URL with `/design-patterns/` appended. Search indexes are generated at build time, so use the production preview to test search:

```sh
npm run check
npm run build
npm test
npm run preview
```

The default base is `/design-patterns` for the GitHub Pages project URL. To host at the root, build with `SITE_BASE=/ npm run build`. `npm test` currently checks the default project base.

## Java checks

```sh
cd examples/abstract-factory
javac --release 21 AbstractFactoryDemo.java AbstractFactoryChecks.java
java AbstractFactoryChecks
java AbstractFactoryDemo text
java AbstractFactoryDemo html
```

The source listing and download are generated from `examples/abstract-factory/AbstractFactoryDemo.java`, so they remain synchronized. The example uses dependency-free behavior checks; no Maven installation is needed for this first guide.

Builder checks:

```sh
cd examples/builder
javac --release 21 BuilderDemo.java BuilderChecks.java
java BuilderChecks
java BuilderDemo text
java BuilderDemo html
```

The Builder listing and download use `examples/builder/BuilderDemo.java` directly. CI runs all three completed patterns in a Java 21 matrix.

Factory Method checks:

```sh
cd examples/factory-method
javac --release 21 FactoryMethodDemo.java FactoryMethodChecks.java
java FactoryMethodChecks
java FactoryMethodDemo text
java FactoryMethodDemo html
```

The Factory Method listing and download use `examples/factory-method/FactoryMethodDemo.java` directly.

## Content

Pattern guides live in `src/content/patterns/*.mdx`. `src/content.config.ts` validates title, category, order, intent, keywords, status, and related-pattern slugs. Set `status: complete` when a guide is implemented. The remaining outlines deliberately contain no fake Java walkthroughs.

Edit components in `src/components`, layouts in `src/layouts`, and the design tokens/responsive styles in `src/styles/global.css`. Diagrams are accessible inline SVG components, with visible captions and text descriptions. Search indexes guide content, excluding shared navigation.

## Review and deployment

The CI workflow checks TypeScript/Astro, builds and indexes the site, verifies internal links, and compiles/tests the Java example on JDK 21. The built site is retained as a workflow artifact.

Deployment is deliberately manual for this review stage. After merging, enable **Settings → Pages → Source: GitHub Actions**, then run **Deploy to GitHub Pages** on `main`. Expected URL: `https://jimnewpower.github.io/design-patterns/`. This PR does not enable hosting or deploy the site.

Use **Print / save PDF** on a guide for a print-oriented export. Automatic curated PDF generation is not part of this first release.

## References and licensing

The Abstract Factory guide links to the GoF book, Refactoring.Guru, and Java 21 documentation. Text and code here are original teaching material; the site does not reproduce the book. No project-wide redistribution license has been selected yet. Dependencies retain their respective licenses.
