import { readFile, readdir, stat } from 'node:fs/promises';
import assert from 'node:assert/strict';
import path from 'node:path';
const base = '/design-patterns/';
const root = path.resolve('dist');
const patternDirs = await readdir(path.join(root,'patterns'));
assert.equal(patternDirs.length,23,'All 23 pattern pages must exist');
const source = await readFile('examples/abstract-factory/AbstractFactoryDemo.java','utf8');
assert.equal(await readFile('dist/examples','utf8'),source,'Download must match tested Java source');
const builderSource = await readFile('examples/builder/BuilderDemo.java','utf8');
assert.equal(await readFile('dist/downloads/BuilderDemo.java','utf8'), builderSource, 'Builder download must match tested source');
const builderGuide = await readFile('dist/patterns/builder/index.html','utf8');
assert.ok(builderGuide.includes('Complete guide') && !builderGuide.includes('This guide is planned'));
assert.ok(builderGuide.includes('TextReportBuilder') && builderGuide.includes('Runtime sequence') && builderGuide.includes('<svg'));
const home = await readFile('dist/index.html','utf8');
assert.ok(home.includes('4 complete guides'), 'Homepage reflects completed guides');
for(const slug of patternDirs) assert.ok(home.includes(`patterns/${slug}/`),`Homepage links ${slug}`);
let count=0;
async function scan(dir) {
 for(const entry of await readdir(dir,{withFileTypes:true})) {
  const filename=path.join(dir,entry.name);
  if(entry.isDirectory()) { await scan(filename); continue; }
  if(!entry.name.endsWith('.html')) continue;
  count++;
  const html=await readFile(filename,'utf8');
  assert.ok(html.includes('id="main"'),`Skip target on ${filename}`);
  for(const match of html.matchAll(/(?:href|src)="([^"#]+)"/g)) {
   const url=match[1].replaceAll('&amp;','&');
   if (/^(?:https?:|mailto:|data:)/.test(url)) continue;
   const resolved = new URL(url, `https://atlas.test${base}${path.relative(root,filename).replace(/index\.html$/, '')}`);
   if (!resolved.pathname.startsWith(base)) assert.fail(`URL escapes project base: ${url}`);
   const relative=decodeURIComponent(resolved.pathname.slice(base.length));
   let target=path.join(root,relative);
   let info;
   try { info=await stat(target); } catch { assert.fail(`Broken local URL ${url} in ${filename}`); }
   if(info.isDirectory()) await stat(path.join(target,'index.html'));
  }
 }
}
await scan(root);
const guide=await readFile('dist/patterns/abstract-factory/index.html','utf8');
assert.ok(guide.includes('HtmlReportFactory') && guide.includes('Complete runnable example'));
assert.ok(guide.includes('<svg') && guide.includes('Runtime sequence'));
assert.ok((await readdir('dist/pagefind')).includes('pagefind.js'),'Search bundle generated');
console.log(`Verified ${count} HTML pages, 23 pattern links, local assets, source download, and search bundle.`);

const factorySource = await readFile('examples/factory-method/FactoryMethodDemo.java','utf8');
assert.equal(await readFile('dist/downloads/FactoryMethodDemo.java','utf8'), factorySource, 'Factory Method download matches tested source');
const factoryGuide = await readFile('dist/patterns/factory-method/index.html','utf8');
assert.ok(factoryGuide.includes('Complete guide') && !factoryGuide.includes('This guide is planned'));
assert.ok(factoryGuide.includes('createFormatter') && factoryGuide.includes('Runtime sequence') && factoryGuide.includes('<svg'));

const prototypeSource = await readFile('examples/prototype/PrototypeDemo.java','utf8');
assert.equal(await readFile('dist/downloads/PrototypeDemo.java','utf8'), prototypeSource, 'Prototype download matches tested source');
const prototypeGuide = await readFile('dist/patterns/prototype/index.html','utf8');
assert.ok(prototypeGuide.includes('Complete guide') && !prototypeGuide.includes('This guide is planned'));
assert.ok(prototypeGuide.includes('ReportPrototype') && prototypeGuide.includes('Runtime copy workflow') && prototypeGuide.includes('<svg'));
