(function () {

  function pick() {
    return document.getElementById("maintFloat") || document.querySelector(".maint-banner");
  }

  function docH() {
    const b = document.body, d = document.documentElement;
    return Math.max(
      b.scrollHeight, d.scrollHeight,
      b.offsetHeight, d.offsetHeight,
      b.clientHeight, d.clientHeight
    );
  }

  function measure(el) {
    const prev = el.style.transform;
    el.style.transform = "translate3d(0,0,0)";
    const h = el.getBoundingClientRect().height || 0;
    el.style.transform = prev || "translate3d(0,0,0)";
    const total = docH();
    const vp = window.innerHeight || document.documentElement.clientHeight || 0;
    let maxY = Math.max(0, total - h);
    if (maxY < vp - h) maxY = Math.max(maxY, vp - h);
    return maxY;
  }

  function start() {
    const el = pick();
    if (!el) return;

    // sicher im <body>
    if (el.parentElement !== document.body) document.body.appendChild(el);

    el.classList.add("wave-active");
    el.style.opacity = "1";
    el.style.visibility = "visible";
    el.style.pointerEvents = "none";

    let maxY = measure(el);

    let resizeTimer;
    window.addEventListener("resize", () => {
      clearTimeout(resizeTimer);
      resizeTimer = setTimeout(() => { maxY = measure(el); }, 150);
    }, { passive: true });

    const T = 20;
    const jitterPx = 0.25;
    const ease = p => 0.5 * (1 - Math.cos(2 * Math.PI * p));

    let t0 = performance.now(), last = t0;
    let paused = false;
    let resumeAt = 0;
    let scrollTimer;
    let running = true;
    let rafId = 0;

    // Hover-Pause
    el.addEventListener("mouseenter", () => { paused = true; });
    el.addEventListener("mouseleave", () => { resumeAt = performance.now() + 1500; });

    // Scroll-Pause
    window.addEventListener("scroll", () => {
      paused = true;
      clearTimeout(scrollTimer);
      scrollTimer = setTimeout(() => { resumeAt = performance.now() + 1500; }, 200);
    }, { passive: true });

    // === Close-Button (X) ===
    const closeBtn = el.querySelector(".maint-close");
    if (closeBtn) {
      closeBtn.style.pointerEvents = "auto";
      closeBtn.addEventListener("click", (ev) => {
        ev.preventDefault();
        ev.stopPropagation();
        running = false;                // Animation stoppen
        if (rafId) cancelAnimationFrame(rafId);
        el.style.display = "none";      // Banner ausblenden
      });
      closeBtn.addEventListener("keydown", (ev) => {
        if (ev.key === "Enter" || ev.key === " ") {
          ev.preventDefault();
          closeBtn.click();
        }
      });
    }

    // --- Haupt-Loop ---
    function tick(now) {
      if (!running) return;
      const elapsed = (now - t0) / 1000;
      const dt = (now - last) / 1000; last = now;

      if (paused && now >= resumeAt) paused = false;

      if (!paused) {
        const p = (elapsed % T) / T;
        const y = ease(p) * maxY;
        const j = Math.sin(elapsed * 3.173) * jitterPx +
                  Math.sin(elapsed * 5.411) * (jitterPx * 0.5);
        el.style.transform = `translate3d(0, ${(y + j).toFixed(2)}px, 0)`;
      }

      if (dt > 5) t0 = now - (T * 1000) / 2;
      rafId = requestAnimationFrame(tick);
    }

    rafId = requestAnimationFrame(tick);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", start, { once: true });
  } else {
    start();
  }

})();