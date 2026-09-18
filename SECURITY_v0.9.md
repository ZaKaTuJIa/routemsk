# ROUTE/MSK — security report v0.9

Status: PRE-RELEASE

## Implemented

- HTTPS is provided by GitHub Pages for routemsk.ru.
- No API keys, bot tokens or credentials are stored in frontend code.
- No backend, database or automatic STS upload exists in this version.
- Selected STS files remain on the visitor's device.
- File validation accepts only JPG/JPEG/PNG/PDF up to 10 MB.
- File names and contents are not sent to Yandex Metrica.
- Analytics records only parameter-free conversion events.
- CSP restricts scripts, styles, frames, objects, forms and connections.
- External links use noopener noreferrer.
- Strict cross-origin referrer policy is enabled.
- Inline executable JavaScript was removed.

## Important limitations

1. GitHub Pages cannot configure all HTTP security headers. HSTS control and
   X-Content-Type-Options require a hosting/proxy layer.
2. Telegram and WhatsApp become independent processors after the visitor sends
   a document. Their retention is outside this static site.
3. A real upload feature requires a private backend, retention rules, rate
   limiting, malware scanning and legal consent.
4. Repository and domain administrator accounts must use MFA. Frontend code
   cannot verify account security.

## Release blockers

- Add the legal name/status and taxpayer details of the service provider.
- Approve and publish privacy policy and personal-data consent.
- Define document retention/deletion rules inside the operator workflow.
- Verify Telegram and WhatsApp test messages with the actual recipient.
- Decide whether production remains on GitHub Pages or moves behind a proxy.

## Incident rule

Never post STS images, passports, signatures, phone numbers, bot tokens or
client data to the public repository, analytics, issue tracker or bug report.
