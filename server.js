const http = require('http');

const PORT = process.env.PORT || 10000;
const API_ASSIST_URL = 'https://service.api-assist.com/parser/transport_mos_api/';
const API_ASSIST_KEY = String(process.env.API_ASSIST_KEY || '').trim();

const LATIN_TO_CYR = Object.freeze({
  A:'А', B:'В', E:'Е', K:'К', M:'М', H:'Н', O:'О', P:'Р', C:'С', T:'Т', Y:'У', X:'Х'
});

function normalizePlate(value='') {
  return String(value)
    .trim()
    .toUpperCase()
    .replace(/[\s-]+/g, '')
    .replace(/[ABEKMHOPCTYX]/g, ch => LATIN_TO_CYR[ch] || ch);
}

function isValidPlate(value) {
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
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS, GET');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  res.setHeader('Cache-Control', 'no-store');
}

function sendJson(res, status, payload) {
  res.statusCode = status;
  res.setHeader('Content-Type', 'application/json; charset=utf-8');
  res.end(JSON.stringify(payload));
}

async function readJson(req) {
  let body = '';
  for await (const chunk of req) {
    body += chunk;
    if (body.length > 20_000) throw new Error('Payload too large');
  }
  return body ? JSON.parse(body) : {};
}

async function handlePermit(req, res) {
  let body;
  try {
    body = await readJson(req);
  } catch (_) {
    return sendJson(res, 400, { ok:false, error:'Некорректный JSON.' });
  }

  const plate = normalizePlate(body.plate || body.byTransportRegNumber || '');
  if (!isValidPlate(plate)) {
    return sendJson(res, 400, { ok:false, error:'Проверьте формат госномера.', plate });
  }

  if (!API_ASSIST_KEY) {
    return sendJson(res, 503, {
      ok:false,
      error:'API проверки пока не настроен: отсутствует ключ API Assist.',
      code:'API_ASSIST_NOT_CONFIGURED'
    });
  }

  const url = new URL(API_ASSIST_URL);
  url.searchParams.set('key', API_ASSIST_KEY);
  url.searchParams.set('regNumber', plate);

  let upstream;
  try {
    upstream = await fetch(url, {
      method:'GET',
      headers:{
        'Accept':'application/json',
        'User-Agent':'ROUTEMSK/1.0 (+https://routemsk.ru)'
      },
      signal: AbortSignal.timeout(15000)
    });
  } catch (error) {
    console.error('API Assist network error:', error);
    return sendJson(res, 502, {
      ok:false,
      error:'Не удалось связаться с сервисом проверки пропусков.',
      code:'API_ASSIST_NETWORK_ERROR'
    });
  }

  const raw = await upstream.text();
  let data;
  try { data = raw ? JSON.parse(raw) : null; }
  catch (_) { data = { raw }; }

  if (!upstream.ok) {
    console.error('API Assist HTTP error:', upstream.status, raw.slice(0,500));
    return sendJson(res, upstream.status === 403 ? 503 : 502, {
      ok:false,
      error:(data && data.error) || 'Сервис проверки вернул ошибку.',
      provider:'api-assist',
      upstreamStatus:upstream.status,
      errorCode:data && data.error_code ? data.error_code : undefined
    });
  }

  if (!data || Number(data.success) !== 1) {
    const providerError = data && data.error ? String(data.error) : '';
    console.error('API Assist unsuccessful response:', raw.slice(0,500));
    return sendJson(res, providerError ? 502 : 503, {
      ok:false,
      error:providerError || 'Источник временно не ответил. Повторите проверку через минуту.',
      provider:'api-assist',
      errorCode:data && data.error_code ? data.error_code : undefined
    });
  }

  return sendJson(res, 200, {
    ok:true,
    plate,
    provider:'api-assist',
    data
  });
}

const server = http.createServer(async (req, res) => {
  setCors(req, res);

  if (req.method === 'OPTIONS') {
    res.statusCode = 204;
    return res.end();
  }

  if (req.method === 'GET' && (req.url === '/' || req.url === '/health')) {
    return sendJson(res, 200, {
      ok:true,
      service:'routemsk-api',
      provider:'api-assist',
      configured:Boolean(API_ASSIST_KEY)
    });
  }

  if (req.method === 'POST' && req.url === '/api/permit') {
    return handlePermit(req, res);
  }

  return sendJson(res, 404, { ok:false, error:'Not found' });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`ROUTEMSK API listening on ${PORT}; API Assist configured: ${Boolean(API_ASSIST_KEY)}`);
});
