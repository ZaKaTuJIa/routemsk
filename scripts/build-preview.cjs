// Static allowlist only: never copy backend, secrets, CNAME, or production root files.
const fs=require('fs'),path=require('path');
const root=path.resolve(__dirname,'..'),out=path.resolve(process.argv[2]||path.join(root,'../preview-release'));
if(out===root || out===path.dirname(root))throw Error('Unsafe preview destination');
const files=['index.html','check.html','404.html','propusk-mkad/index.html','propusk-ttk/index.html','propusk-sk/index.html','check/index.html','politika-konfidencialnosti/index.html','soglasie/index.html',...fs.readdirSync(path.join(root,'assets')).map(f=>'assets/'+f)];
for(const file of files){let data=fs.readFileSync(path.join(root,file),'utf8');if(file.endsWith('.html')){
  data=data.replace(/<meta name="robots"[^>]*>/g,'').replace('</head>','<meta name="robots" content="noindex,nofollow,noarchive"></head>');
  // Canonicals stay on intended production URLs; all interactive local navigation stays in preview.
  data=data.replace(/(href|src)="\/(?!\/)/g,'$1="/preview-release/').replace(/content="0;url=\//g,'content="0;url=/preview-release/');
  data=data.replace(/<script type="application\/ld\+json">[\s\S]*?<\/script>/g,'');
  data=data.replace(/<body>/,'<body><aside style="padding:10px 16px;background:#202329;color:#D9B968;font:14px/1.5 sans-serif;text-align:center">Предварительный просмотр RC · юридические документы — черновики</aside>');
  data=data.replace(/(<meta property="og:url" content=")https:\/\/routemsk.ru\//g,'$1https://routemsk.ru/preview-release/');
}fs.mkdirSync(path.dirname(path.join(out,file)),{recursive:true});fs.writeFileSync(path.join(out,file),data)}
console.log('Preview files: '+files.length+'; output: '+out);
