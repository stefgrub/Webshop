// Modern WebShop UI – modern.js
const THEME_KEY = "webshop_theme";
function applyTheme(theme){ document.documentElement.setAttribute('data-theme', theme); }
function loadTheme(){
  const saved = localStorage.getItem(THEME_KEY);
  if(saved){ applyTheme(saved); return saved; }
  const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
  const theme = prefersDark ? 'dark' : 'light';
  applyTheme(theme); return theme;
}
function toggleTheme(){
  const current = document.documentElement.getAttribute('data-theme') || 'light';
  const next = current === 'dark' ? 'light' : 'dark';
  applyTheme(next); localStorage.setItem(THEME_KEY, next);
}
document.addEventListener('alpine:init', () => {
  Alpine.store('cart', {
    open: false, items: [],
    add(item){
      const existing = this.items.find(i => i.id === item.id);
      if(existing){ existing.qty += item.qty || 1; } else { this.items.push({...item, qty: item.qty || 1}); }
      Alpine.store('toast').push(`${item.name} zum Warenkorb hinzugefügt`);
      this.open = true;
    },
    remove(id){ this.items = this.items.filter(i => i.id !== id); },
    total(){ return this.items.reduce((s,i) => s + i.price * i.qty, 0); },
    count(){ return this.items.reduce((s,i) => s + i.qty, 0); }
  });
  Alpine.store('toast', { list: [], push(msg){ const id = Date.now() + Math.random(); this.list.push({ id, msg }); setTimeout(() => { this.list = this.list.filter(t => t.id !== id); }, 3200); } });
  Alpine.data('liveSearch', () => ({ q: '', submit(){ const url = new URL(window.location.href); if(this.q.trim().length){ url.searchParams.set('q', this.q.trim()); } else { url.searchParams.delete('q'); } window.location.href = url.toString(); } }));
});
document.addEventListener('DOMContentLoaded', () => {
  loadTheme();
  document.querySelectorAll('img').forEach(img => {
    if(!img.hasAttribute('loading')) img.setAttribute('loading', 'lazy');
    if(!img.hasAttribute('decoding')) img.setAttribute('decoding','async');
  });
});
window.toggleTheme = toggleTheme;
