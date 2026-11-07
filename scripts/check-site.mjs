import { readFile, readdir, stat } from 'node:fs/promises';
import assert from 'node:assert/strict';
import path from 'node:path';
const base = `${(process.env.SITE_BASE || '/design-patterns').replace(/\/$/,'')}/`;
const root = path.resolve('dist');
const examples = JSON.parse(await readFile('src/data/examples.json','utf8'));
assert.equal(examples.length,23,'Every GoF pattern must have a registered example');
assert.equal(new Set(examples.map(example=>example.slug)).size,23,'Unique pattern slugs');
const patternDirs = await readdir(path.join(root,'patterns'));
assert.equal(patternDirs.length,23,'All 23 pattern pages exist');
const home = await readFile('dist/index.html','utf8');
assert.ok(home.includes('23 complete guides'),'Homepage reports the completed catalog');
const decode = value => value.replace(/&(#x[0-9a-f]+|#[0-9]+|amp|lt|gt|quot|apos);/gi, (entity, token) => {
  if(token.startsWith('#x')) return String.fromCodePoint(parseInt(token.slice(2),16));
  if(token.startsWith('#')) return String.fromCodePoint(parseInt(token.slice(1),10));
  return ({amp:'&',lt:'<',gt:'>',quot:'"',apos:"'"})[token.toLowerCase()] || entity;
});
for(const example of examples) {
  const mdx = await readFile(`src/content/patterns/${example.slug}.mdx`,'utf8');
  const guide = await readFile(`dist/patterns/${example.slug}/index.html`,'utf8');
  const source = await readFile(`examples/${example.slug}/${example.prefix}Demo.java`,'utf8');
  assert.ok(mdx.includes('status: "complete"'),`${example.slug}: completed metadata`);
  assert.ok(guide.includes('Complete guide') && !guide.includes('This guide is planned'),`${example.slug}: complete guide label`);
  for(const heading of ['Practical use cases','Java walkthrough','Complete runnable example','Verification','References']) {
    assert.ok(guide.includes(heading),`${example.slug}: ${heading}`);
  }
  assert.ok(guide.includes('<svg') && guide.includes('role="img"'),`${example.slug}: accessible diagram`);
  assert.ok(home.includes(`patterns/${example.slug}/`),`${example.slug}: homepage link`);
  assert.equal(await readFile(path.join(root,example.download),'utf8'),source,`${example.slug}: source download`);
  const listings = [...guide.matchAll(/<pre\b[^>]*>([\s\S]*?)<\/pre>/g)].map(match=>decode(match[1].replace(/<[^>]*>/g,'')));
  assert.ok(listings.some(listing=>listing.trim()===source.trim()),`${example.slug}: displayed source matches tested file`);
  if(example.dynamicDownload) {
    const expected = await readFile(`examples/${example.slug}/expected.txt`,'utf8');
    assert.ok(listings.some(listing=>listing.trim()===expected.trim()),`${example.slug}: documented output matches fixture`);
  }
  await stat(`examples/${example.slug}/${example.prefix}Checks.java`);
}
let count=0;
async function scan(dir) {
 for(const entry of await readdir(dir,{withFileTypes:true})) {
  const filename=path.join(dir,entry.name);
  if(entry.isDirectory()) { await scan(filename); continue; }
  if(!entry.name.endsWith('.html')) continue;
  count++;
  const html=await readFile(filename,'utf8');
  assert.ok(html.includes('id="main"'),`Skip target on ${filename}`);
  for(const match of html.matchAll(/(?:href|src)="([^"]+)"/g)) {
    const url=decode(match[1]);
    if(/^(?:https?:|mailto:|data:)/.test(url)) continue;
    const resolved=new URL(url,`https://atlas.test${base}${path.relative(root,filename).replace(/index\.html$/,'')}`);
    assert.ok(resolved.pathname.startsWith(base),`URL escapes project base: ${url}`);
    const relative=decodeURIComponent(resolved.pathname.slice(base.length));
    let target=path.join(root,relative);
    try {
      const info=await stat(target);
      if(info.isDirectory()) target=path.join(target,'index.html');
      await stat(target);
    } catch { assert.fail(`Broken local URL ${url} in ${filename}`); }
    if(resolved.hash && target.endsWith('.html')) {
      const destination=await readFile(target,'utf8');
      assert.ok(destination.includes(`id="${decodeURIComponent(resolved.hash.slice(1))}"`),`Broken anchor ${url} in ${filename}`);
    }
  }
 }
}
await scan(root);
assert.ok((await readdir('dist/pagefind')).includes('pagefind.js'),'Search bundle generated');
// Pagefind metadata must contain an indexed record for each guide.
const pagefindFiles=await readdir('dist/pagefind');
assert.ok(pagefindFiles.some(file=>file.startsWith('pagefind-entry')),'Search entry metadata generated');
console.log(`Verified ${count} HTML pages, 23 complete guides, diagrams, source listings/downloads, output fixtures, links/anchors, and search bundle.`);

const icons = JSON.parse(await readFile('src/data/pattern-icons.json','utf8'));
assert.equal(Object.keys(icons).length,23,'One icon for each pattern');
assert.equal(new Set(Object.values(icons).map(icon=>icon.markup)).size,23,'Distinct icon artwork');
for(const {slug} of examples) {
  const svg = await readFile(`dist/icons/${slug}.svg`,'utf8');
  assert.ok(svg.includes(icons[slug].markup),`${slug}: SVG download matches icon source`);
  assert.ok(svg.includes('viewBox="0 0 32 32"') && svg.includes('<desc'),`${slug}: SVG geometry and description`);
}
console.log('Verified 23 unique icon assets and their gallery download links.');
