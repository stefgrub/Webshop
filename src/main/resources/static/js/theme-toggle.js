(function () {
  'use strict';

  var KEY = 'webshop_theme';
  var LANG_KEY = 'webshop_lang'; // 🔹 NEU: Sprache
  var DOC = document.documentElement;

  /**
   * Interne Helper (Theme)
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
   * 🔹 Sprach-Helper
   */
  function saveLang(lang) {
    if (!lang) return;
    if (lang !== 'de' && lang !== 'en') return;
    try { localStorage.setItem(LANG_KEY, lang); } catch (_) {}
  }

  function readSavedLang() {
    try {
      var v = localStorage.getItem(LANG_KEY);
      return (v === 'de' || v === 'en') ? v : null;
    } catch (_) { return null; }
  }

  // Falls noch keine Sprache gespeichert ist -> aus <html lang="..."> übernehmen
  function initLangFromHtml() {
    if (readSavedLang() != null) return;
    var htmlLang = (DOC.lang || '').toLowerCase();
    if (htmlLang === 'de' || htmlLang === 'en') {
      saveLang(htmlLang);
    }
  }

  /**
   * 🔹 Auto-Redirect für Impressum/Datenschutz je nach Sprache
   */
  function handleLegalAutoRedirect() {
    var savedLang = readSavedLang();
    if (!savedLang) return;

    var path = window.location.pathname;

    var isImpressum = (path === '/impressum' || path === '/impressum-en');
    var isDatenschutz = (path === '/datenschutz' || path === '/datenschutz-en');

    if (!isImpressum && !isDatenschutz) return;

    // EN bevorzugt
    if (savedLang === 'en') {
      if (path === '/impressum') {
        window.location.replace('/impressum-en');
      } else if (path === '/datenschutz') {
        window.location.replace('/datenschutz-en');
      }
    }

    // DE bevorzugt
    if (savedLang === 'de') {
      if (path === '/impressum-en') {
        window.location.replace('/impressum');
      } else if (path === '/datenschutz-en') {
        window.location.replace('/datenschutz');
      }
    }
  }

  /**
   * DOM Ready
   */
  document.addEventListener('DOMContentLoaded', function () {
    // Theme-Buttons initialisieren
    var theme = currentTheme();
    reflectButtons(theme);

    // Theme-Buttons anklemmen
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

    // 🔹 Sprache initial aus <html lang> ableiten (falls noch nicht gesetzt)
    initLangFromHtml();

    // 🔹 Klicks auf Language-Links verfolgen:
    // z. B. <a data-lang="de">Deutsch</a> / <a data-lang="en">English</a>
    document.body.addEventListener('click', function (e) {
      var link = e.target.closest('[data-lang]');
      if (!link) return;
      var lang = link.getAttribute('data-lang');
      saveLang(lang);
      // Kein Redirect hier, der Link selbst macht den Seitenwechsel
    });

    // 🔹 Auto-Redirect für Impressum/Datenschutz nach gespeicherter Sprache
    handleLegalAutoRedirect();
  });

})();