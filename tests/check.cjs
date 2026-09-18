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
for(const file of ['favicon.svg','assets/og-cover.png','robots.txt','sitemap.xml','MVP_SETUP.md'])assert(fs.existsSync(''+file));
const png=fs.readFileSync('assets/og-cover.png');assert.equal(png.readUInt32BE(16),1200);assert.equal(png.readUInt32BE(20),630);
const script=html.match(/<script>([\s\S]*?)<\/script>/)[1];
function setup(code=script,mode='normal'){
 const elements={};for(const id of ['stsFile','uploadTitle','uploadStatus'])elements[id]={textContent:'',value:'x',files:[],events:{},addEventListener(k,f){this.events[k]=f;}};
 const links=['telegram_click','whatsapp_click','phone_click'].map(goal=>({dataset:{goal},events:{},addEventListener(k,f){this.events[k]=f;}}));
 const calls=[],loaded=[];
 const window=mode==='queued'?{}:{ym:(...args)=>{if(mode==='throws')throw Error('blocked');calls.push(args);}};
 const document={getElementById:id=>elements[id],querySelectorAll:()=>links,
  createElement:()=>({}),getElementsByTagName:()=>[{parentNode:{insertBefore(s){loaded.push(s);}}}]};
 const context=vm.createContext({window,document});
 vm.runInContext(code,context);return {elements,links,calls,context,loaded,window};
}
const disabled=setup(script.replace('metrikaCounterId: 112716166','metrikaCounterId: null'));
disabled.links.forEach(l=>l.events.click());assert.equal(disabled.calls.length,0);assert.equal(disabled.loaded.length,0);
const enabled=setup();
assert.equal(enabled.loaded.length,1);assert.equal(enabled.loaded[0].src,'https://mc.yandex.ru/metrika/tag.js');assert.equal(enabled.loaded[0].async,1);
assert.deepEqual(JSON.parse(JSON.stringify(enabled.calls)),[
 [112716166,'init',{defer:true,clickmap:false,trackLinks:false,webvisor:false,trackHash:false,ecommerce:false,sendTitle:false,accurateTrackBounce:true}],
 [112716166,'hit','https://routemsk.ru/',{referer:'',title:'ROUTE/MSK'}]
]);
const clickGoals=['telegram_click','whatsapp_click','phone_click'];
enabled.links.forEach(l=>l.events.click());
assert.deepEqual(enabled.calls.slice(2),clickGoals.map(goal=>[112716166,'reachGoal',goal]));
vm.runInContext("trackGoal('sts_upload_success');trackGoal('private.pdf');",enabled.context);
assert.equal(enabled.calls.length,5);
for(const mode of ['queued','throws']){
 const blocked=setup(script,mode);blocked.links.forEach(l=>l.events.click());
 blocked.elements.stsFile.files=[{name:'private.pdf',type:'application/pdf',size:20}];blocked.elements.stsFile.events.change();
 assert(blocked.elements.uploadStatus.textContent.includes('Не отправлен'));
 if(mode==='queued')assert.deepEqual(Array.from(blocked.window.ym.a,args=>Array.from(args)).slice(2),[...clickGoals,'sts_upload_start'].map(goal=>[112716166,'reachGoal',goal]));
}
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
assert.equal(enabled.calls.filter(c=>c[2]==='sts_upload_start').length,3,'Only three valid file selections should emit sts_upload_start');
assert(html.includes('<meta name="referrer" content="no-referrer">'));
assert(fs.readFileSync('yandex_a1a1feeff8ab616a.html','utf8').includes('Verification: a1a1feeff8ab616a'));
assert(!fs.readFileSync('sitemap.xml','utf8').includes('yandex_'));
console.log('PASS: SEO/assets/FAQ; official loader and real counter; minimal init/hit; click goals plus valid-selection upload start; queued/blocked analytics; seven file cases and cancellation; no false success; Webmaster verification file.');
