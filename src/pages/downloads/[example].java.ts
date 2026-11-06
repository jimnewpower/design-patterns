import type { APIRoute, GetStaticPaths } from 'astro';
import examples from '../../data/examples.json';
const sources = import.meta.glob<string>('../../../examples/*/*Demo.java', { query: '?raw', import: 'default', eager: true });
export const getStaticPaths: GetStaticPaths = () => examples.filter(example => example.dynamicDownload).map(({ slug, prefix }) => ({
  params: { example: `${prefix}Demo` },
  props: { source: sources[`../../../examples/${slug}/${prefix}Demo.java`], filename: `${prefix}Demo.java` },
}));
export const GET: APIRoute = ({ props }) => new Response(props.source, { headers: {
  'Content-Type': 'text/plain; charset=utf-8',
  'Content-Disposition': `attachment; filename="${props.filename}"`,
} });
