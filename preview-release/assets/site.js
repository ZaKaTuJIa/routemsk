'use strict';
// TODO production: verified Metrika counter ID and privacy configuration.
// Webvisor is not loaded. Never pass input values or API payloads to analytics.
window.routeTrack = function (name) {
  const allowed = ['telegram_click','whatsapp_click','phone_click','temporary_permit_click','annual_permit_click','fleet_click','permit_check_start','permit_check_success','permit_check_not_found','permit_check_error'];
  if (!allowed.includes(name)) return;
  try {
    if (typeof window.ym === 'function' && window.ROUTEMSK_METRIKA_ID) window.ym(window.ROUTEMSK_METRIKA_ID, 'reachGoal', name);
    if (typeof window.gtag === 'function') window.gtag('event', name);
  } catch (_) { /* Analytics must never interrupt navigation or checking. */ }
};
document.addEventListener('click', event => {
  const el = event.target.closest('a, [data-event]');
  if (!el) return;
  const events = new Set();
  if (el.dataset.event) events.add(el.dataset.event);
  const href = el.getAttribute('href') || '';
  if (href.startsWith('https://t.me/')) events.add('telegram_click');
  if (href.startsWith('https://wa.me/')) events.add('whatsapp_click');
  if (href.startsWith('tel:')) events.add('phone_click');
  events.forEach(window.routeTrack);
});
const menuToggle = document.getElementById('menuToggle');
const mobileMenu = document.getElementById('mobileMenu');
if (menuToggle && mobileMenu) {
  const setMenu = open => {
    menuToggle.setAttribute('aria-expanded', String(open));
    menuToggle.setAttribute('aria-label', open ? 'Закрыть меню' : 'Открыть меню');
    mobileMenu.classList.toggle('open', open);
    mobileMenu.setAttribute('aria-hidden', String(!open));
    document.body.classList.toggle('menu-open', open);
    document.querySelector('main').inert = open;
    document.querySelector('footer').inert = open;
    if (open) mobileMenu.querySelector('a').focus();
    else menuToggle.focus();
  };
  menuToggle.addEventListener('click', () => setMenu(menuToggle.getAttribute('aria-expanded') !== 'true'));
  mobileMenu.querySelectorAll('a').forEach(a => a.addEventListener('click', () => setMenu(false)));
  document.addEventListener('keydown', e => {
    if (menuToggle.getAttribute('aria-expanded') !== 'true') return;
    if (e.key === 'Escape') setMenu(false);
    if (e.key === 'Tab') {
      const targets = [menuToggle, ...mobileMenu.querySelectorAll('a')];
      const index = targets.indexOf(document.activeElement);
      e.preventDefault();
      targets[(index + (e.shiftKey ? targets.length - 1 : 1)) % targets.length].focus();
    }
  });
  window.matchMedia('(min-width:768px)').addEventListener('change', e => { if (e.matches && menuToggle.getAttribute('aria-expanded') === 'true') setMenu(false); });
}
document.querySelectorAll('.faq-q').forEach(button => button.addEventListener('click', () => {
  const item=button.closest('.faq-item'), open=!item.classList.contains('open');
  item.classList.toggle('open',open);button.setAttribute('aria-expanded',String(open));button.querySelector('.faq-symbol').textContent=open?'−':'+';
}));
