const fs=require('node:fs'),vm=require('node:vm'),assert=require('node:assert/strict');
const html=fs.readFileSync('index.html','utf8');
const ids=[...html.matchAll(/\bid="([^"]+)"/g)].map(x=>x[1]);
assert.equal(new Set(ids).size,ids.length);
for(const m of html.matchAll(/href="#([^"]+)"/g))assert(ids.includes(m[1]),m[1]);
assert.equal((html.match(/<h1>/g)||[]).length,1);
assert.equal((html.match(/rel="canonical"/g)||[]).length,1);
const schema=JSON.parse(html.match(/<script type="application\/ld\+json">([\s\S]*?)<\/script>/)[1]);
assert.equal(schema['@graph'].length,2);
assert.equal((html.match(/<details class="faq-item">/g)||[]).length,6);
assert(!html.includes("querySelectorAll('.faq-item"));
for(const file of ['favicon.svg','assets/og-cover.png','robots.txt','sitemap.xml','404.html','MVP_SETUP.md'])assert(fs.existsSync(''+file));
const png=fs.readFileSync('assets/og-cover.png');assert.equal(png.readUInt32BE(16),1200);assert.equal(png.readUInt32BE(20),630);
const script=html.match(/<script>([\s\S]*?)<\/script>/)[1];
function setup(code=script){
 const elements={};for(const id of ['stsFile','uploadTitle','uploadStatus'])elements[id]={textContent:'',value:'x',files:[],events:{},addEventListener(k,f){this.events[k]=f;}};
 const links=['telegram_click','whatsapp_click','phone_click'].map(goal=>({dataset:{goal},events:{},addEventListener(k,f){this.events[k]=f;}}));
 const calls=[];const context=vm.createContext({window:{ym:(...args)=>calls.push(args)},document:{getElementById:id=>elements[id],querySelectorAll:()=>links}});
 vm.runInContext(code,context);return {elements,links,calls,context};
}
const original=setup();original.links.forEach(l=>l.events.click());original.elements.stsFile.events.click();assert.equal(original.calls.length,0);
const enabled=setup(script.replace('metrikaCounterId: null','metrikaCounterId: 123456'));
enabled.links.forEach(l=>l.events.click());enabled.elements.stsFile.events.click();assert.deepEqual(enabled.calls.map(c=>c[2]),['telegram_click','whatsapp_click','phone_click','sts_upload_start']);
const {elements}=enabled;
for(const [file,ok] of [
 [{name:'test.pdf',type:'application/pdf',size:20},true],
 [{name:'test.PNG',type:'image/png',size:10*1024*1024},true],
 [{name:'test.jpg',type:'',size:20},true],
 [{name:'test.exe',type:'application/pdf',size:20},false],
 [{name:'test.pdf',type:'image/png',size:20},false],
 [{name:'test.pdf',type:'application/pdf',size:0},false],
 [{name:'test.pdf',type:'application/pdf',size:10*1024*1024+1},false]
]) {elements.stsFile.files=[file];elements.stsFile.events.change();assert.equal(elements.uploadStatus.textContent.includes('Выбран файл:'),ok);}
elements.stsFile.files=[];elements.stsFile.events.change();assert.equal(elements.uploadStatus.textContent,'');
assert(!enabled.calls.some(c=>c[2]==='sts_upload_success'));
console.log('PASS: metadata, anchors, six native FAQ, assets, PNG dimensions, disabled/enabled goal dispatch, seven file cases and cancellation; no false success.');
