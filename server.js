const http = require('http');

const PORT = process.env.PORT || 10000;
const API_ASSIST_URL = 'https://service.api-assist.com/parser/transport_mos_api/';
const API_ASSIST_KEY = String(process.env.API_ASSIST_KEY || '').trim();
// Bounded in-memory protection; Render deployment must be verified separately.
const allowedOrigins = new Set(['https://routemsk.ru','https://www.routemsk.ru','https://zakatujia.github.io']);
const cache = new Map(), inFlight = new Map();
let windowStart = Date.now(), requestCount = 0;
const MAX_REQUESTS_PER_MINUTE = 10;
const CACHE_TTL = 60_000;

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
  if (req.headers.origin && !allowedOrigins.has(req.headers.origin)) {
    return sendJson(res, 403, {ok:false, error:'Запрос недоступен.'});
  }
  if (Date.now() - windowStart >= 60_000) { windowStart = Date.now(); requestCount = 0; }
  if (++requestCount > MAX_REQUESTS_PER_MINUTE) {
    res.setHeader('Retry-After', '60');
    return sendJson(res, 429, {ok:false, error:'Повторите проверку через минуту.'});
  }
  let body;
  try {
    body = await readJson(req);
    if (!body || typeof body !== 'object' || Array.isArray(body)) throw new Error('body');
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
      error:'Проверка временно недоступна.',
      code:'API_ASSIST_NOT_CONFIGURED'
    });
  }

  for (const [key,value] of cache) if (value.expires <= Date.now()) cache.delete(key);
  if (cache.has(plate)) return sendJson(res, 200, cache.get(plate).payload);
  if (inFlight.has(plate)) {
    const result = await inFlight.get(plate);
    return sendJson(res, result.status, result.payload);
  }
  let settle;
  const pending = new Promise(resolve => { settle = resolve; });
  inFlight.set(plate, pending);
  const finish = (status, payload) => {
    if (status === 200) {
      if (cache.size >= 100) cache.delete(cache.keys().next().value);
      cache.set(plate, {expires:Date.now()+CACHE_TTL,payload});
    }
    settle({status,payload});inFlight.delete(plate);
    return sendJson(res,status,payload);
  };

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
    console.error('API Assist network error');
    return finish(502, {
      ok:false,
      error:'Не удалось связаться с сервисом проверки пропусков.',
      code:'API_ASSIST_NETWORK_ERROR'
    });
  }

  let raw;
  try { raw = await upstream.text(); }
  catch (_) { return finish(502, {ok:false,error:'Проверка временно недоступна.'}); }
  let data;
  try { data = raw ? JSON.parse(raw) : null; }
  catch (_) { data = { raw }; }

  if (!upstream.ok) {
    console.error('API Assist HTTP error:', upstream.status);
    return finish(upstream.status === 403 ? 503 : 502, {
      ok:false,
      error:'Сервис проверки временно недоступен.',
      provider:'api-assist',
      upstreamStatus:upstream.status,
    });
  }

  if (!data || Number(data.success) !== 1) {
    const providerError = data && data.error ? String(data.error) : '';
    console.error('API Assist unsuccessful response');
    return finish(providerError ? 502 : 503, {
      ok:false,
      error:'Источник временно не ответил. Повторите проверку через минуту.',
      provider:'api-assist',
    });
  }

  return finish(200, {
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
