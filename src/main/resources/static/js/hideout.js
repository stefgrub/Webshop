document.addEventListener('DOMContentLoaded', () => {
    const backdrop = document.getElementById('secretBanner');
    const card = document.getElementById('secretCard');
    const ok = document.getElementById('secretOk');

    function dismiss() {
      if (!backdrop) return;
        backdrop.style.transition = 'opacity .25s ease';
        backdrop.style.opacity = '0';
        setTimeout(() => backdrop.remove(), 260);
    }

    // Automatisch ausblenden nach 4 Sekunden
    const timer = setTimeout(dismiss, 4000);

    // OK-Button
    if (ok) ok.addEventListener('click', () => { clearTimeout(timer); dismiss(); });

    // Klick außerhalb der Karte
    if (backdrop) backdrop.addEventListener('click', (e) => {
      if (!card.contains(e.target)) { clearTimeout(timer); dismiss(); }
    });

    // ESC schließt Banner
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') { clearTimeout(timer); dismiss(); }
    });
});