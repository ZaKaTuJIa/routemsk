# ROUTEMSK — DeepSeek release QA / critic brief (2026-09-26)

Роль: независимый QA + CRO critic + factual consistency checker перед production.

Цель: НЕ придумывать новый дизайн и НЕ расширять scope. Проверить уже утвержденный ROUTEMSK v2.1 и подготовить release blockers.

## 1. Текущий источник истины

- Визуал: ROUTEMSK UI/UX v2.1 (dark graphite + gold, синий только micro-status).
- Hero: ночная Москва / дорога / грузовой транспорт; full-bleed cinematic.
- Primary CTA: gold «Написать специалисту».
- Secondary CTA: dark/outline.
- Checker: реально работает через `routemsk-api.onrender.com` → API Assist → transport.mos.ru.
- API key только в Render Environment; не в GitHub/JS.
- Цена ROUTEMSK: годовой 8 000 ₽, временный 1 500 ₽ — это коммерческий оффер сопровождения, НЕ цена госуслуги.
- Госуслуга по внесению в реестр на официальном портале предоставляется бесплатно.
- Legal реквизиты нельзя выдумывать.

## 2. Launch architecture для проверки

- `/`
- `/propusk-mkad/`
- `/propusk-ttk/`
- `/propusk-sk/`
- `/check/`
- `/politika-konfidencialnosti/`
- `/soglasie/`
- `/404.html`
- `/robots.txt`
- `/sitemap.xml`

Не предлагать `/dlya-ip/`, `/dlya-ooo/`, `/gazel/`, десятки городских SEO-дублей, `/otzyvy/` без реальных отзывов.

## 3. Official facts — использовать как baseline

Основной источник: https://transport.mos.ru/gruzoviki/ovnesenii

Проверить, что сайт НЕ противоречит следующим пунктам:
- госуслуга бесплатна;
- оформление долгосрочного пропуска — 10 рабочих дней;
- разовый пропуск — не более 10 суток;
- дневной: 07:00–23:00;
- ночной: 23:00–07:00;
- действие дневного пропуска распространяется и на ночное время;
- по ТТК и внутри ТТК с 07:00 до 23:00 действует ограничение для грузового транспорта грузоподъемностью более 1 т;
- по МКАД и внутри МКАД для РММ >3,5 т ограничение круглосуточное;
- Евро-3 — в пределах ТТК и по нему;
- Евро-2 — от ТТК до МКАД и по МКАД.

РНИС:
- https://transport.mos.ru/gruzoviki/perevod_ckad
- https://transport.mos.ru/mostrans/all_news/119297

## 4. Checker QA — P0

Проверить сценарии:
1. валидный активный пропуск;
2. найденный истекший пропуск;
3. найденный аннулированный пропуск;
4. несколько записей на один госномер — актуальная запись должна выбираться по датам/аннулированию, не `records[0]`;
5. not found;
6. API timeout / 5xx;
7. malformed API response;
8. invalid plate format;
9. Enter submit;
10. повторный быстрый клик / loading state.

Статус считается по приоритету:
- cancellationDate / явная аннуляция;
- startDate/endDate относительно текущей даты;
- только затем textual status как fallback.

Нельзя считать `status = Выдан` достаточным доказательством, что запись активна сегодня.

## 5. API-limit / abuse QA

Тестовый API Assist: 200 запросов/месяц. Проверить backend на:
- rate limit;
- CORS whitelist;
- отсутствие API key в response/log/client bundle;
- ограничение размера body;
- нормализацию номера;
- простую защиту от повторных identical requests / caching, если реализовано;
- безопасную обработку provider errors.

Не предлагать CAPTCHA на старте, если rate limiting достаточно; CAPTCHA — только если реально есть abuse.

## 6. Legal / trust QA — P0

Блокирующие дефекты:
- placeholders вместо реальных реквизитов;
- отсутствие политики/согласия там, где собираются персональные данные;
- формулировка, будто 8 000 ₽ / 1 500 ₽ — плата государству;
- «официальный партнер», «100% гарантия», «официальный пропуск от ROUTEMSK»;
- вымышленные отзывы, компании, логотипы клиентов, сертификаты.

Обязательный дисклеймер:
> Государственная услуга по внесению сведений в реестр предоставляется уполномоченным органом бесплатно. Стоимость ROUTEMSK относится к консультационному и документарному сопровождению клиента.

## 7. Analytics / privacy QA

События:
- `telegram_click`
- `whatsapp_click`
- `phone_click`
- `temporary_permit_click`
- `annual_permit_click`
- `fleet_click`
- `permit_check_start`
- `permit_check_success`
- `permit_check_not_found`
- `permit_check_error`

Запрещено отправлять в аналитику:
- госномер;
- ФИО;
- телефон/email;
- Telegram username;
- документы/изображения;
- полный API response.

Проверить, что input госномера не захватывается Вебвизором; использовать подходящий privacy attribute/class согласно текущей документации Метрики, а не придумывать несуществующий атрибут.

## 8. Technical SEO QA

Проверить:
- unique Title / Description / H1;
- self-canonical;
- OG URL/title/description;
- JSON-LD без вымышленных данных;
- sitemap содержит только канонические launch pages;
- robots.txt содержит Sitemap и при необходимости Clean-param;
- не использовать устаревший `Host:` как обязательную директиву Яндекса;
- legal pages не закрывать автоматически через robots.txt;
- 404 реально возвращается как GitHub Pages 404 document;
- CNAME = `routemsk.ru`;
- нет ссылок на `/check.html`, если production canonical выбран `/check/` — должны быть единообразные URL.

## 9. Responsive / accessibility launch gate

Проверить минимум:
- 360×800;
- 390×844;
- 768px;
- 1440px.

Искать:
- horizontal overflow;
- sticky CTA перекрывает контент/клавиатуру;
- mobile menu focus;
- видимые `:focus-visible`;
- контраст текста;
- tap targets;
- CLS hero;
- отсутствие layout jumps при checker result.

## 10. Формат результата DeepSeek

Не писать абстрактный аудит на 20 пунктов без приоритета. Выдать:

### P0 — BLOCK RELEASE
Только дефекты, которые реально нельзя выпускать.

### P1 — FIX BEFORE TRAFFIC
SEO/CRO/analytics дефекты, которые желательно закрыть до индексации/рекламы.

### P2 — AFTER LAUNCH
Полировка и гипотезы.

Для каждого бага:
- ID;
- URL / component;
- actual;
- expected;
- evidence;
- risk;
- severity;
- exact fix;
- regression test.

Отдельно в конце:
- `FACTUAL CONTRADICTIONS`;
- `SEO CANNIBALIZATION`;
- `CHECKER EDGE CASES`;
- `GO / NO-GO` только как technical release readiness, не как маркетинговый рейтинг.
