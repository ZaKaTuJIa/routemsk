const UPSTREAM = 'https://transport.mos.ru/api/reestr-gruzoviki/search';

const LATIN_TO_CYR = Object.freeze({
  A:'А', B:'В', E:'Е', K:'К', M:'М', H:'Н', O:'О', P:'Р', C:'С', T:'Т', Y:'У', X:'Х'
});

function normalizePlate(value='') {
  return String(value)
    .trim()
    .toUpperCase()
    .replace(/\s+/g, '')
    .replace(/[ABEKMHOPCTYX]/g, ch => LATIN_TO_CYR[ch] || ch);
}

function isValidPlate(value) {
  // Российский ГРЗ в практическом формате для реестра: буква + 3 цифры + 2 буквы + регион 2–3 цифры.
  return /^[АВЕКМНОРСТУХ]\d{3}[АВЕКМНОРСТУХ]{2}\d{2,3}$/.test(value);
}

function setCors(req, res) {
  const origin = req.headers.origin || '';
  const allowed = new Set([
    'https://routemsk.ru',
    'https://www.routemsk.ru',
    'https://zakatujia.github.io'
  ]);

  if (allowed.has(origin)) {
    res.setHeader('Access-Control-Allow-Origin', origin);
    res.setHeader('Vary', 'Origin');
  }
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  res.setHeader('Cache-Control', 'no-store');
}

export default async function handler(req, res) {
  setCors(req, res);

  if (req.method === 'OPTIONS') {
    return res.status(204).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ ok:false, error:'method_not_allowed' });
  }

  const plate = normalizePlate(req.body?.plate || req.body?.byTransportRegNumber || '');

  if (!isValidPlate(plate)) {
    return res.status(400).json({
      ok:false,
      error:'invalid_plate',
      message:'Проверьте госномер. Пример: А123АА777.'
    });
  }

  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 10000);

  try {
    const upstreamResponse = await fetch(UPSTREAM, {
      method:'POST',
      headers:{
        'Content-Type':'application/json',
        'Accept':'application/json, text/plain, */*',
        'User-Agent':'RouteMSK/1.0 (+https://routemsk.ru)'
      },
      body:JSON.stringify({
        type:'byTransportRegNumber',
        byTransportRegNumber:plate
      }),
      signal:controller.signal
    });

    const raw = await upstreamResponse.text();
    let data;
    try {
      data = raw ? JSON.parse(raw) : null;
    } catch {
      data = { raw };
    }

    if (!upstreamResponse.ok) {
      return res.status(502).json({
        ok:false,
        error:'upstream_error',
        upstreamStatus:upstreamResponse.status,
        data
      });
    }

    return res.status(200).json({
      ok:true,
      plate,
      source:'transport.mos.ru',
      data
    });
  } catch (error) {
    const timedOut = error?.name === 'AbortError';
    return res.status(502).json({
      ok:false,
      error:timedOut ? 'upstream_timeout' : 'proxy_failed',
      message:timedOut ? 'Реестр Москвы не ответил вовремя.' : 'Не удалось получить данные из реестра Москвы.'
    });
  } finally {
    clearTimeout(timeout);
  }
}
