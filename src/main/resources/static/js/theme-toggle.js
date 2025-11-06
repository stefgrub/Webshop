(function () {
  const KEY = 'webshop_theme';

  /**
   * Wendet ein Theme an ("light" oder "dark")
   */
  function applyTheme(theme) {
    const t = theme === 'dark' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', t);
    document.documentElement.style.colorScheme = t;
    try {
      localStorage.setItem(KEY, t);
    } catch (_) {}
  }

  /**
   * Liest gespeichertes Theme oder Systempräferenz
   */
  function initTheme() {
    let saved;
    try {
      saved = localStorage.getItem(KEY);
    } catch (_) {}

    if (saved === 'dark' || saved === 'light') {
      applyTheme(saved);
    } else {
      // keine Speicherung vorhanden → Systempräferenz übernehmen
      const prefersDark =
        window.matchMedia &&
        window.matchMedia('(prefers-color-scheme: dark)').matches;
      applyTheme(prefersDark ? 'dark' : 'light');
    }
  }

  /**
   * Umschalten zwischen hell / dunkel
   */
  window.toggleTheme = function () {
    const current =
      document.documentElement.getAttribute('data-theme') || 'light';
    applyTheme(current === 'dark' ? 'light' : 'dark');
  };

  /**
   * DOM-Ready
   */
  document.addEventListener('DOMContentLoaded', function () {
    // Initiales Theme setzen
    initTheme();

    // Desktop-Button
    const btn = document.getElementById('themeToggle');
    if (btn)
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        toggleTheme();
      });

    // Mobile-Button
    const btnM = document.getElementById('themeToggleMobile');
    if (btnM)
      btnM.addEventListener('click', (e) => {
        e.preventDefault();
        toggleTheme();
      });
  });
})();