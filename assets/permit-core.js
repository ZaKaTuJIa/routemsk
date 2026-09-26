(function (root) {
  'use strict';
  const aliases = {
    zone:['AllowedZona','AllowedZone','Zone','PermitZone','Зона'],
    status:['Status','PermitStatus','Статус'],
    from:['StartDate','ValidFrom','DateFrom','Дата начала'],
    to:['EndDate','ValidTo','DateTo','Дата окончания'],
    cancel:['CancellationDate','CancelDate','AnnulDate','Дата аннулирования'],
    number:['LicenseNumber','PermitNumber','Number','Номер'],
    series:['Series','Серия'],
    regime:['Type','PermitType','Режим']
  };
  const clean = s => s.toLowerCase().replace(/[^a-zа-я0-9]/g,'');
  function get(record, field) {
    for (const name of aliases[field]) {
      const pair=Object.entries(record).find(([key,value]) => clean(key)===clean(name) && value!==null && value!==undefined && value!=='');
      if(pair) return pair[1];
    }
    return null;
  }
  const empty = value => value==null || /^(|—|-|null|нет|0000-00-00(?:.*)?)$/i.test(String(value).trim());
  // Compare calendar dates in Moscow; date-only permits include their full end day.
  function date(value) {
    if(empty(value)) return null;
    const s=String(value).trim();
    let m=s.match(/^(\d{4})-(\d{2})-(\d{2})(?:$|[T ])/), y,mo,d;
    if(m) [,y,mo,d]=m;
    else {m=s.match(/^(\d{2})[./-](\d{2})[./-](\d{4})(?:$|[ T])/);if(!m)return null;[,d,mo,y]=m;}
    const dt=new Date(Date.UTC(+y,+mo-1,+d));
    if(dt.getUTCFullYear()!==+y || dt.getUTCMonth()!==+mo-1 || dt.getUTCDate()!==+d)return null;
    return `${y}-${mo}-${d}`;
  }
  function today(now=new Date()) {
    return new Intl.DateTimeFormat('en-CA',{timeZone:'Europe/Moscow',year:'numeric',month:'2-digit',day:'2-digit'}).format(now);
  }
  function classify(record, now=new Date()) {
    const start=date(get(record,'from')), end=date(get(record,'to')), cancel=get(record,'cancel'), status=String(get(record,'status')||'').trim().toLowerCase(), day=today(now);
    if(!empty(cancel) || /аннул|отмен|недейств|cancel|invalid|revoked|not\s+valid|inactive/.test(status)) return 'cancelled';
    if(start && end && start>end)return 'unknown';
    if(end && end<day)return 'expired';
    if(start && start>day)return 'future';
    if(start && end)return 'active';
    // Partial or invalid dates cannot establish current validity; "Выдан" is insufficient.
    if(!empty(get(record,'from')) || !empty(get(record,'to')))return 'unknown';
    if(/ист[её]к|просроч|expired/.test(status))return 'expired';
    if(/^(действует|действующий|active|valid)$/.test(status))return 'active';
    return 'unknown';
  }
  function extract(payload, depth=0) {
    if(depth>8)throw Error('schema');
    if(Array.isArray(payload)) {
      if(!payload.every(r=>r && typeof r==='object' && !Array.isArray(r) && ['from','to','cancel','number','zone','status'].some(k=>get(r,k)!==null)))throw Error('schema');
      return payload;
    }
    if(!payload || typeof payload!=='object')throw Error('schema');
    for(const key of ['records','data','items','result','results','rows','list']) {
      if(Object.prototype.hasOwnProperty.call(payload,key))return extract(payload[key],depth+1);
    }
    if(['from','to','number'].some(k=>get(payload,k)!==null))return [payload];
    throw Error('schema');
  }
  function select(records,now=new Date()) {
    const ranked=records.map((record,index)=>({record,index,state:classify(record,now)}));
    const active=ranked.filter(r=>r.state==='active').sort((a,b)=>(date(get(b.record,'to'))||'').localeCompare(date(get(a.record,'to'))||'') || (date(get(b.record,'from'))||'').localeCompare(date(get(a.record,'from'))||'') || a.index-b.index);
    const recent=r=>[date(get(r.record,'from')),date(get(r.record,'to')),date(get(r.record,'cancel'))].filter(Boolean).sort().pop()||'';
    ranked.sort((a,b)=>recent(b).localeCompare(recent(a)) || a.index-b.index);
    return {primary:active[0]||ranked[0]||null,activeCount:active.length};
  }
  const latin={A:'А',B:'В',E:'Е',K:'К',M:'М',H:'Н',O:'О',P:'Р',C:'С',T:'Т',Y:'У',X:'Х'};
  const normalize = s => String(s).toUpperCase().replace(/[\s-]/g,'').replace(/[ABEKMHOPCTYX]/g,c=>latin[c]);
  const valid = s => /^[АВЕКМНОРСТУХ]\d{3}[АВЕКМНОРСТУХ]{2}\d{2,3}$/.test(s);
  const api={get,date,classify,extract,select,normalize,valid};
  if(typeof module==='object' && module.exports)module.exports=api;else root.PermitCore=api;
})(typeof window==='object'?window:globalThis);
