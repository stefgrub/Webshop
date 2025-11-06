(function () {
  const KEY = 'webshop_theme';
  const root = document.documentElement;

  // -------------------------
  // Initialisierung beim Laden
  // -------------------------
  try {
    const saved = localStorage.getItem(KEY);
    const prefersDark = window.matchMedia &&
      window.matchMedia('(prefers-color-scheme: dark)').matches;
    const theme = saved || (prefersDark ? 'dark' : 'light');

    root.setAttribute('data-theme', theme);
    root.style.colorScheme = theme;

    // Falls Meta-Tag für color-scheme existiert, aktualisieren
    const meta = document.querySelector('meta[name="color-scheme"]');
    if (meta) meta.setAttribute('content', theme === 'dark' ? 'dark light' : 'light dark');
  } catch (_) {
    console.warn('Theme initialization failed', _);
  }

  // -------------------------
  // Toggle-Funktion
  // -------------------------
  window.toggleTheme = function toggleTheme() {
    try {
      const current = root.getAttribute('data-theme') || 'light';
      const next = current === 'dark' ? 'light' : 'dark';

      root.setAttribute('data-theme', next);
      root.style.colorScheme = next;
      localStorage.setItem(KEY, next);

      // Meta-Tag auch beim Umschalten aktualisieren
      const meta = document.querySelector('meta[name="color-scheme"]');
      if (meta) meta.setAttribute('content', next === 'dark' ? 'dark light' : 'light dark');

      // Optional: sanfte Übergangsanimation
      root.classList.add('theme-transition');
      setTimeout(() => root.classList.remove('theme-transition'), 300);

    } catch (err) {
      console.error('Theme toggle failed', err);
    }
  };
})();