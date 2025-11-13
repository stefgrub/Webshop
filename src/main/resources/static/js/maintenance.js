// src/main/resources/static/js/maintenance.js
(function () {
  'use strict';

  var SNOOZE_KEY = 'hide_maint_until';
  var SNOOZE_MS  = 60 * 60 * 1000; // 1h

  var banner, rafId = null, scrollTicking = false, lastScrollY = 0;
  var onScroll, onResize, onOrient, onMut, mo;

  function pickBanner() {
    return document.getElementById('maintFloat') || document.querySelector('.maint-banner') || null;
  }

  function applyFixedTop(el) {
    // nur minimal, Rest per CSS
    el.style.position = 'fixed';
    el.style.top = '0';
    el.style.left = '0';
    el.style.right = '0';
    el.style.zIndex = el.style.zIndex || '1000';
  }

  function shouldSnoozeHide() {
    try {
      var v = parseInt(localStorage.getItem(SNOOZE_KEY) || '0', 10);
      return v && Date.now() < v;
    } catch (_) { return false; }
  }

  function setSnooze() {
    try { localStorage.setItem(SNOOZE_KEY, String(Date.now() + SNOOZE_MS)); } catch (_) {}
  }

  function closeBanner() {
    if (!banner) return;
    cancelAnimationFrame(rafId);
    if (mo) mo.disconnect();
    window.removeEventListener('scroll', onScroll, { passive: true });
    window.removeEventListener('resize', onResize, { passive: true });
    window.removeEventListener('orientationchange', onOrient, { passive: true });
    banner.remove();
    banner = null;
  }

  function initCloseButton() {
    var btn = banner.querySelector('.maint-close');
    if (!btn) return;
    btn.addEventListener('click', function () {
      setSnooze();
      closeBanner();
    }, { once: true });
  }

  // ---------- Scroll Hide/Show ----------
  function updateScroll() {
    if (!banner) return;
    var y = window.scrollY || window.pageYOffset || 0;
    // Schwellen: runter -> verstecken, rauf -> einblenden
    if (y > lastScrollY + 20) {
      banner.classList.add('hidden');
    } else if (y < lastScrollY - 10) {
      banner.classList.remove('hidden');
    }
    lastScrollY = y;
    scrollTicking = false;
  }

  function initScrollHideShow() {
    lastScrollY = window.scrollY || 0;
    onScroll = function () {
      if (!scrollTicking) {
        scrollTicking = true;
        requestAnimationFrame(updateScroll);
      }
    };
    window.addEventListener('scroll', onScroll, { passive: true });
  }

  // ---------- Wave Animation (optional) ----------
  function prefersReducedMotion() {
    return !!(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);
  }

  function docHeight() {
    var b = document.body, d = document.documentElement;
    return Math.max(b.scrollHeight, d.scrollHeight, b.offsetHeight, d.offsetHeight, b.clientHeight, d.clientHeight);
  }

  function measure() {
    if (!banner) return { h: 0, maxY: 0 };
    var prev = banner.style.transform;
    banner.style.transform = 'translate3d(0,0,0)';
    var h = (banner.getBoundingClientRect && banner.getBoundingClientRect().height) || banner.offsetHeight || 0;
    banner.style.transform = prev || 'translate3d(0,0,0)';
    return { h: h, maxY: Math.max(0, docHeight() - h) };
  }

  function startWaveIfNeeded() {
    if (!banner) return;
    var wantWave = banner.getAttribute('data-wave') === 'true';
    if (!wantWave || prefersReducedMotion()) return;

    var M = measure();
    var T = 20;         // Sekunden für top->bottom->top
    var jitter = 0.35;  // leichte Zufallsbewegung
    var ease = function (p) { return 0.5 * (1 - Math.cos(2 * Math.PI * p)); };

    var t0 = performance.now(), last = t0;
    function tick(now) {
      if (!banner) return;
      var elapsed = (now - t0) / 1000;
      var dt = (now - last) / 1000; last = now;
      var p = (elapsed % T) / T;

      // neu vermessen, falls sich Layout stark ändert (MutationObserver triggert auch remeasure)
      var m = M;
      var y = ease(p) * m.maxY;
      var j = Math.sin(elapsed * 3.173) * jitter + Math.sin(elapsed * 5.411) * (jitter * 0.5);
      banner.style.transform = 'translate3d(0,' + (y + j).toFixed(2) + 'px,0)';

      // großer Tab-Sleep fix
      if (dt > 5) t0 = now - (T * 1000) / 2;
      rafId = requestAnimationFrame(tick);
    }

    var remeasure = function () { M = measure(); };
    onResize = remeasure;
    onOrient = remeasure;
    window.addEventListener('resize', onResize, { passive: true });
    window.addEventListener('orientationchange', onOrient, { passive: true });

    mo = new MutationObserver(remeasure);
    mo.observe(document.documentElement, { childList: true, subtree: true });

    banner.classList.add('wave-active');
    rafId = requestAnimationFrame(tick);
  }

  function init() {
    banner = pickBanner();
    if (!banner) return;

    if (shouldSnoozeHide()) {
      // bereits „weggedrückt“
      banner.remove();
      banner = null;
      return;
    }

    // sichere Grund-Positionierung (Rest per CSS)
    applyFixedTop(banner);

    // Close-Button
    initCloseButton();

    // Scroll-basierte Sichtbarkeit
    initScrollHideShow();

    // Wave nur wenn explizit gewünscht (data-wave="true") und keine reduzierte Bewegung
    startWaveIfNeeded();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init, { once: true });
  } else {
    init();
  }
})();