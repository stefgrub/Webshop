(function () {
  'use strict';

  var KEY = 'webshop_theme';
  var DOC = document.documentElement;

  /**
   * Interne Helper
   */
  function setThemeAttr(theme) {
    var t = theme === 'dark' ? 'dark' : 'light';
    DOC.setAttribute('data-theme', t);
    DOC.style.colorScheme = t;
    return t;
  }

  function saveTheme(theme) {
    try { localStorage.setItem(KEY, theme); } catch (_) {}
  }

  function readSavedTheme() {
    try {
      var v = localStorage.getItem(KEY);
      return (v === 'dark' || v === 'light') ? v : null;
    } catch (_) { return null; }
  }

  function systemPrefersDark() {
    return !!(window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);
  }

  function currentTheme() {
    return DOC.getAttribute('data-theme') || (systemPrefersDark() ? 'dark' : 'light');
  }

  /**
   * Public API (am window, falls andere Scripts das Theme brauchen)
   */
  function applyTheme(theme) {
    var t = setThemeAttr(theme);
    saveTheme(t);
    reflectButtons(t);
    return t;
  }

  function toggleTheme() {
    var next = currentTheme() === 'dark' ? 'light' : 'dark';
    return applyTheme(next);
  }

  // Exponieren
  window.WebshopTheme = {
    apply: applyTheme,
    toggle: toggleTheme,
    get: currentTheme
  };
  // Für Kompatibilität mit bestehendem Code:
  window.toggleTheme = toggleTheme;

  /**
   * Buttons/ARIA synchronisieren
   */
  function reflectButtons(theme) {
    var ids = ['themeToggle', 'themeToggleMobile'];
    ids.forEach(function (id) {
      var btn = document.getElementById(id);
      if (!btn) return;
      // ARIA
      btn.setAttribute('aria-pressed', theme === 'dark' ? 'true' : 'false');
      // Optional: data-state zum Stylen
      btn.setAttribute('data-theme', theme);
      // Optional: Icon/Text umschalten
      var txt = btn.querySelector('[data-theme-label]');
      if (txt) txt.textContent = theme === 'dark' ? 'Dark' : 'Light';
    });
  }

  /**
   * Systemwechsel (z. B. OS schaltet auf dark) – respektieren,
   * sofern der Nutzer kein explizites Theme gespeichert hat.
   */
  function listenSystemChanges() {
    if (!window.matchMedia) return;
    var mq = window.matchMedia('(prefers-color-scheme: dark)');
    if (!mq.addEventListener) {
      // ältere Browser
      mq.addListener(function () {
        if (readSavedTheme() == null) {
          applyTheme(systemPrefersDark() ? 'dark' : 'light');
        }
      });
      return;
    }
    mq.addEventListener('change', function () {
      if (readSavedTheme() == null) {
        applyTheme(systemPrefersDark() ? 'dark' : 'light');
      }
    });
  }

  /**
   * DOM Ready
   */
  document.addEventListener('DOMContentLoaded', function () {
    // Hier NICHT erneut initial setzen (das macht das Head-Boot-Snippet schon vor dem Paint),
    // sondern nur Buttons verdrahten + State spiegeln.
    var theme = currentTheme();
    reflectButtons(theme);

    // Buttons anklemmen
    var btn = document.getElementById('themeToggle');
    if (btn) btn.addEventListener('click', function (e) {
      e.preventDefault();
      toggleTheme();
    });

    var btnM = document.getElementById('themeToggleMobile');
    if (btnM) btnM.addEventListener('click', function (e) {
      e.preventDefault();
      toggleTheme();
    });

    // Systemwechsel beachten, falls kein explizites User-Setting
    listenSystemChanges();
  });

})();