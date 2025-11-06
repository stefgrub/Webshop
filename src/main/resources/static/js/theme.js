(function () {
  const DOC = document;
  const HTML = DOC.documentElement;
  const KEY = 'theme'; // localStorage Schlüssel
  const btn = DOC.getElementById('theme-toggle');
  const icon = DOC.getElementById('theme-icon');

  if (!btn || !icon) return; // Falls Header/Fragment mal fehlt, Fehler vermeiden

  // Aktuelles Theme lesen
  const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
  const saved = localStorage.getItem(KEY);
  let theme = saved || (prefersDark ? 'dark' : 'light');

  // Anwenden
  function apply(t) {
    HTML.setAttribute('data-theme', t);
    HTML.style.colorScheme = t; // bessere native Form Controls
    icon.textContent = t === 'dark' ? '🌞' : '🌙'; // Icon umschalten
    theme = t;
  }
  apply(theme);

  // Toggle Logik
  btn.addEventListener('click', function () {
    const next = theme === 'dark' ? 'light' : 'dark';
    localStorage.setItem(KEY, next);
    apply(next);
  });

  // Reagieren auf Systemwechsel (nur wenn der Nutzer nichts gespeichert hat)
  if (!saved && window.matchMedia) {
    const mq = window.matchMedia('(prefers-color-scheme: dark)');
    mq.addEventListener?.('change', (e) => {
      if (!localStorage.getItem(KEY)) {
        apply(e.matches ? 'dark' : 'light');
      }
    });
  }
})();