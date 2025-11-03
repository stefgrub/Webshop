(function initMaintenance() {
  // === Konfiguration ===
  const AUTO_HIDE_MS = 5000;           // nach 5s automatisch ausblenden
  const USE_SESSION_ONLY = true;       // in dieser Session nicht erneut zeigen
  const LOCAL_KEY = USE_SESSION_ONLY ? 'maint_closed_session' : 'maint_closed_until';

  function toMsPrecision(iso) {
    return iso ? iso.replace(/(\.\d{3})\d+/, '$1') : null;
  }

  function setup() {
    const el = document.getElementById('maintFloat') || document.getElementById('maintfloat');
    if (!el) return;

    // z-index, falls Navbar Klicks blockiert
    el.style.position = 'relative';
    el.style.zIndex = '50';

    const untilAttr = el.getAttribute('data-until');
    const untilISO = toMsPrecision(untilAttr) || '2099-01-01T00:00:00.000+01:00';
    const end = new Date(untilISO);
    const now = new Date();

    // gespeicherter Status lesen
    const storage = USE_SESSION_ONLY ? sessionStorage : localStorage;
    const savedRaw = storage.getItem(LOCAL_KEY);
    const saved = savedRaw ? new Date(savedRaw) : null;

    // Nur zeigen, wenn Wartung noch läuft und nicht bereits ausgeblendet
    const shouldShow = (now < end) && (!saved || saved <= now);
    if (!shouldShow) return;

    // anzeigen
    el.classList.remove('is-hidden');

    // Auto-Hide nach X Sekunden
    const hide = () => {
      // für Session: beliebiger Timestamp reicht
      // für persistent: bis Wartungsende nicht erneut anzeigen
      const storeValue = USE_SESSION_ONLY ? new Date(now.getTime() + 86400000).toISOString()
                                          : end.toISOString();
      storage.setItem(LOCAL_KEY, storeValue);
      el.classList.add('is-hidden');
    };

    const btn = el.querySelector('.maint-close');
    if (btn) {
      btn.addEventListener('click', hide, { once: true });
    }

    // automatisch ausblenden
    setTimeout(hide, AUTO_HIDE_MS);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', setup, { once: true });
  } else {
    setup();
  }
})();