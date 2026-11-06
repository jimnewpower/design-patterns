import { defineCollection } from 'astro:content';
import { z } from 'zod';
import { glob } from 'astro/loaders';
const patterns = defineCollection({
  loader: glob({ pattern: '**/*.mdx', base: './src/content/patterns' }),
  schema: z.object({
    title: z.string(), category: z.enum(['Creational','Structural','Behavioral']),
    order: z.number(), intent: z.string(), keywords: z.array(z.string()),
    status: z.enum(['complete','planned']), related: z.array(z.string()).default([]),
  }),
});
export const collections = { patterns };
