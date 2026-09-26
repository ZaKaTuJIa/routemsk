'use strict';
const API_URL='https://routemsk-api.onrender.com/api/permit';
const $=id=>document.getElementById(id), core=window.PermitCore;
let loading=false;
const labels={active:'Действует',expired:'Истёк срок',cancelled:'Аннулирован',future:'Ещё не действует',unknown:'Статус требует уточнения'};
const classes={active:'ok',expired:'warn',cancelled:'bad',future:'neutral',unknown:'neutral'};
function showMessage(text,type='info') { $('message').className='message show '+type;$('message').textContent=text; }
function format(value) { const d=core.date(value);return d?d.split('-').reverse().join('.'):value==null||value===''?'—':String(value); }
function render(selection,plate) {
  const {record,state}=selection.primary;
  $('resultPlate').textContent=plate;
  $('zone').textContent=core.get(record,'zone')||'Не указана';
  $('regime').textContent=core.get(record,'regime')||'Не указан';
  $('status').textContent=labels[state];
  $('validFrom').textContent=format(core.get(record,'from'));
  $('validTo').textContent=format(core.get(record,'to'));
  $('cancelDate').textContent=format(core.get(record,'cancel'));
  $('permitNumber').textContent=[core.get(record,'series'),core.get(record,'number')].filter(Boolean).join(' ')||'Не указан';
  $('statusBadge').textContent=labels[state];$('statusBadge').className='badge '+classes[state];
  $('multipleNote').hidden=selection.activeCount<2;
  $('multipleNote').textContent=selection.activeCount>1?'Найдено несколько актуальных записей: '+selection.activeCount+'. Показана основная с самой поздней датой окончания. Проверьте зону и режим остальных записей в официальном реестре.':'';
  $('result').classList.add('show');
}
$('fallback').hidden=true;
$('permitForm').addEventListener('submit',async event=>{
  event.preventDefault();if(loading)return;
  $('message').className='message';$('message').textContent='';$('result').classList.remove('show');$('multipleNote').hidden=true;$('fallback').hidden=true;
  const plate=core.normalize($('plate').value);$('plate').value=plate;
  $('plate').setAttribute('aria-invalid',String(!core.valid(plate)));
  if(!core.valid(plate)){showMessage('Проверьте формат номера. Пример: А123АА777.','warn');$('plate').focus();return;}
  loading=true;$('checkBtn').disabled=true;$('plate').readOnly=true;$('checkBtn').classList.add('loading');$('checkBtnText').textContent='Проверяем…';$('permitForm').setAttribute('aria-busy','true');
  window.routeTrack('permit_check_start');
  const controller=new AbortController(), timer=setTimeout(()=>controller.abort(),25000);
  try {
    const response=await fetch(API_URL,{method:'POST',headers:{'Content-Type':'application/json','Accept':'application/json'},body:JSON.stringify({plate}),signal:controller.signal});
    if(!response.ok)throw Error('api');
    const payload=await response.json();
    if(!payload || payload.ok!==true)throw Error('api');
    const records=core.extract(payload.data);
    if(!records.length){showMessage('Пропуск не найден. Проверьте номер или обратитесь к специалисту.','warn');$('fallback').hidden=false;window.routeTrack('permit_check_not_found');}
    else{render(core.select(records),plate);window.routeTrack('permit_check_success');}
  } catch (_) {
    showMessage('Автоматическая проверка сейчас недоступна. Напишите специалисту — поможем проверить вручную.','error');$('fallback').hidden=false;window.routeTrack('permit_check_error');
  } finally {
    clearTimeout(timer);loading=false;$('checkBtn').disabled=false;$('plate').readOnly=false;$('checkBtn').classList.remove('loading');$('checkBtnText').textContent='Проверить пропуск';$('permitForm').setAttribute('aria-busy','false');
  }
});
