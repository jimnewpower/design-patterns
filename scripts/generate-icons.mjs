import { readFile, writeFile, mkdir } from 'node:fs/promises';
import { Resvg } from '@resvg/resvg-js';
const icons=JSON.parse(await readFile('src/data/pattern-icons.json','utf8'));
const examples=JSON.parse(await readFile('src/data/examples.json','utf8'));
const escape = value => value.replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;');
const title = slug => slug.split('-').map(word=>word[0].toUpperCase()+word.slice(1)).join(' ');
const attributes='fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"';
await mkdir('public/icons',{recursive:true});
for(const {slug} of examples) {
  const item=icons[slug];
  if(!item) throw new Error(`Missing icon: ${slug}`);
  await writeFile(`public/icons/${slug}.svg`,`<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32" ${attributes} role="img" aria-labelledby="title desc"><title id="title">${escape(title(slug))}</title><desc id="desc">${escape(item.meaning)}</desc>${item.markup}</svg>\n`);
}
const groups=[['Creational',examples.slice(0,5),'#365aca'],['Structural',examples.slice(5,12),'#197562'],['Behavioral',examples.slice(12),'#8b4e1f']];
let y=130;
const parts=['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1100 1160" role="img" aria-labelledby="title desc"><title id="title">Pattern Atlas icon set</title><desc id="desc">23 original icons grouped by creational, structural, and behavioral patterns.</desc><rect width="1100" height="1160" fill="#f8f9fb"/><g font-family="DejaVu Sans, sans-serif" fill="#182437"><text x="40" y="55" font-size="28" font-weight="700">Pattern Atlas / Icon set</text><text x="40" y="85" font-size="15" fill="#5e6b7e">23 patterns · 32-unit grid</text>'];
for(const [category,entries,color] of groups) {
  parts.push(`<text x="40" y="${y}" font-size="17" font-weight="700" fill="${color}">${category}</text>`);
  entries.forEach(({slug},i)=>{
    const x=40+i%5*208,top=y+25+Math.floor(i/5)*143;
    parts.push(`<rect x="${x}" y="${top}" width="192" height="125" rx="10" fill="#fff" stroke="#dce2eb"/><g transform="translate(${x+72} ${top+18}) scale(1.5)" color="${color}" ${attributes}>${icons[slug].markup}</g>`);
    if(slug==='chain-of-responsibility') {
      parts.push(`<text x="${x+96}" y="${top+91}" text-anchor="middle" font-size="13">Chain of</text><text x="${x+96}" y="${top+109}" text-anchor="middle" font-size="13">Responsibility</text>`);
    } else {
      parts.push(`<text x="${x+96}" y="${top+99}" text-anchor="middle" font-size="13">${escape(title(slug))}</text>`);
    }
  });
  y+=40+Math.ceil(entries.length/5)*143;
}
parts.push('</g></svg>');
const sheet=parts.join('')+'\n';
await writeFile('public/icons/pattern-atlas-icons.svg',sheet);
await writeFile('public/icons/pattern-atlas-icons.png',new Resvg(sheet,{fitTo:{mode:'width',value:1100}}).render().asPng());
console.log(`Generated ${examples.length} SVG icons and SVG/PNG contact sheets.`);
