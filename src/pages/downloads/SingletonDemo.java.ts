import type { APIRoute } from 'astro';
import source from '../../../examples/singleton/SingletonDemo.java?raw';
export const GET: APIRoute = () => new Response(source, { headers: {
  'Content-Type': 'text/plain; charset=utf-8',
  'Content-Disposition': 'attachment; filename="SingletonDemo.java"',
} });
