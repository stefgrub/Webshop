(function initMaintenance() {
  function setup() {
    const el = document.getElementById('maintFloat') || document.getElementById('maintfloat');
    if (!el) return;

    // Fixiert am oberen Rand
    el.style.position = 'fixed';
    el.style.top = '0';
    el.style.left = '0';
    el.style.right = '0';
    el.style.zIndex = '1000';

    const closeBtn = el.querySelector('.maint-close');
    if (closeBtn) {
      closeBtn.addEventListener('click', () => {
        el.style.display = 'none';
      }, { once: true });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => setTimeout(setup, 50), { once: true });
  } else {
    setTimeout(setup, 50);
  }
})();

// === Scroll hide/show effect ===
(function () {
  let lastScrollY = window.scrollY;
  let ticking = false;
  const banner = document.querySelector('.maint-banner');
  if (!banner) return;

  function update() {
    const currentY = window.scrollY;
    if (currentY > lastScrollY + 20) {
      banner.classList.add('hidden');
    } else if (currentY < lastScrollY - 10) {
      banner.classList.remove('hidden');
    }
    lastScrollY = currentY;
    ticking = false;
  }

  window.addEventListener('scroll', () => {
    if (!ticking) {
      window.requestAnimationFrame(update);
      ticking = true;
    }
  });
})();

// === WAVE: Banner über gesamte Seite (top <-> bottom) ===
(function () {
  function pick(){ return document.getElementById("maintFloat") || document.querySelector(".maint-banner"); }
  function docH(){ const b=document.body,d=document.documentElement;
    return Math.max(b.scrollHeight,d.scrollHeight,b.offsetHeight,d.offsetHeight,b.clientHeight,d.clientHeight); }
  function measure(el){
    const prev=el.style.transform;
    el.style.transform="translate3d(0,0,0)";
    const h=el.getBoundingClientRect().height||0;
    el.style.transform=prev||"translate3d(0,0,0)";
    return { bannerH:h, maxY:Math.max(0, docH()-h) };
  }
  function start(){
    const el = pick(); if(!el) return;
    if (el.parentElement !== document.body) document.body.appendChild(el);
    el.classList.add("wave-active"); // siehe CSS unten
    let M = measure(el);

    const remeasure = ()=>{ M = measure(el); };
    window.addEventListener("resize", remeasure, {passive:true});
    window.addEventListener("orientationchange", remeasure, {passive:true});
    new MutationObserver(remeasure).observe(document.documentElement, {childList:true,subtree:true});

    const T = 20; // Sekunden für top->bottom->top
    const jitter = 0.35;
    const ease = p => 0.5*(1-Math.cos(2*Math.PI*p));
    let t0 = performance.now(), last = t0;

    function tick(now){
      const elapsed=(now-t0)/1000, dt=(now-last)/1000; last=now;
      const p=(elapsed%T)/T, y=ease(p)*M.maxY;
      const j=Math.sin(elapsed*3.173)*jitter + Math.sin(elapsed*5.411)*(jitter*0.5);
      el.style.transform = `translate3d(0, ${(y+j).toFixed(2)}px, 0)`;
      if (dt>5) t0 = now - (T*1000)/2;
      requestAnimationFrame(tick);
    }
    requestAnimationFrame(tick);
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", start, { once:true });
  } else {
    start();
  }
})();