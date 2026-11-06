import { readFile, mkdtemp, rm } from 'node:fs/promises';
import { execFileSync } from 'node:child_process';
import { tmpdir } from 'node:os';
import path from 'node:path';
import assert from 'node:assert/strict';
const examples = JSON.parse(await readFile(new URL('../src/data/examples.json',import.meta.url),'utf8'));
const javaBin = name => process.env.JAVA_HOME ? path.join(process.env.JAVA_HOME, 'bin', name) : name;
const requested = process.argv[2];
const selected = requested ? examples.filter(example => example.slug === requested) : examples;
assert.ok(selected.length, `Unknown example: ${requested}`);
for(const example of selected) {
  const directory = path.resolve('examples',example.slug);
  const output = await mkdtemp(path.join(tmpdir(),'pattern-atlas-java-'));
  const env = { ...process.env, JAVA_TOOL_OPTIONS: '-Dfile.encoding=UTF-8' };
  try {
    execFileSync(javaBin('javac'),['--release','21','-d',output,`${example.prefix}Demo.java`,`${example.prefix}Checks.java`], {cwd:directory, env, stdio:'inherit',timeout:60000});
    execFileSync(javaBin('java'),['-cp',output,`${example.prefix}Checks`],{cwd:directory,env,stdio:'inherit',timeout:60000});
    for(const args of example.smokeArgs) {
      const actual = execFileSync(javaBin('java'),['-cp',output,`${example.prefix}Demo`,...args],{cwd:directory,env,encoding:'utf8',timeout:30000});
      if(example.dynamicDownload) {
        const expected = await readFile(path.join(directory,'expected.txt'),'utf8');
        assert.equal(actual.replaceAll('\r\n','\n'),expected,`${example.slug}: documented demo output`);
      }
      process.stdout.write(actual);
    }
    console.log(`Verified ${example.slug}`);
  } finally { await rm(output,{recursive:true,force:true}); }
}
