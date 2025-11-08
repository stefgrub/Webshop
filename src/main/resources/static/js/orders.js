/**
 * /static/js/orders.js – Admin-Bestellübersicht (CSP-konform)
 * - Sendet application/x-www-form-urlencoded (passend zu Spring MVC)
 * - Nutzt die Controller-Routen:
 *     POST /admin/orders/{id}/status  (body: status=...)
 *     POST /admin/orders/cancel/{id}
 *     POST /admin/orders/delete/{id}
 */

(function () {
  'use strict';

  // --- CSRF aus <meta> holen (layout.html muss die zwei Metas enthalten) ---
  function getCsrf() {
    const tokenMeta  = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');
    return {
      token:  tokenMeta ? tokenMeta.getAttribute('content')  : '',
      header: headerMeta ? headerMeta.getAttribute('content') : 'X-CSRF-TOKEN'
    };
  }

  // --- Toast ---
  function toast(msg, kind) {
    const el = document.createElement('div');
    el.className = `toast ${kind || ''}`;
    Object.assign(el.style, {
      position: 'fixed', right: '24px', bottom: '24px', zIndex: 9999,
      background: kind === 'err' ? '#ef4444' : kind === 'ok' ? '#16a34a' : '#374151',
      color: '#fff', padding: '10px 16px', borderRadius: '8px',
      boxShadow: '0 2px 12px rgba(0,0,0,.2)', opacity: 0, transition: 'opacity .25s'
    });
    el.textContent = msg;
    document.body.appendChild(el);
    requestAnimationFrame(() => el.style.opacity = 1);
    setTimeout(() => { el.style.opacity = 0; setTimeout(() => el.remove(), 300); }, 2200);
  }

  // --- POST als x-www-form-urlencoded ---
  async function postForm(url, obj) {
    const { header, token } = getCsrf();
    const headers = {
      'Accept': 'text/html,application/json',
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    };
    if (token) headers[header] = token;

    const body = new URLSearchParams();
    if (obj) Object.entries(obj).forEach(([k, v]) => body.append(k, v));

    const res = await fetch(url, {
      method: 'POST',
      headers,
      body,
      redirect: 'follow',
      credentials: 'same-origin'
    });

    if (!res.ok) {
      const txt = await res.text().catch(() => '');
      throw new Error(`HTTP ${res.status}${txt ? `: ${txt.slice(0, 200)}` : ''}`);
    }
    return res; // Inhalt ist hier egal, es folgt meist Redirect
  }

  // --- UI-Helfer: Statuszelle aktualisieren ---
  function updateRowStatus(orderId, newStatus) {
    const row = document.querySelector(`tr[data-id="${orderId}"]`);
    if (!row) return;
    const badge = row.querySelector('.order-status');
    if (badge) {
      badge.textContent = newStatus;
      // CSS-Klasse für Farbe aktualisieren (z.B. .order-status.shipped)
      badge.className = `badge order-status ${String(newStatus).toLowerCase()}`;
    }
  }

  // --- Event Delegation auf Tabelle ---
  const table = document.querySelector('#orders-table, table.orders');
  if (!table) return;

  table.addEventListener('click', async (e) => {
    const btn = e.target.closest('[data-action]');
    if (!btn) return;

    const id = btn.getAttribute('data-id');
    const action = btn.getAttribute('data-action');
    if (!id || !action) return;

    btn.disabled = true; btn.style.opacity = 0.6;
    try {
      if (action === 'mark-shipped') {
        // passt zu @PostMapping("/{id}/status")
        await postForm(`/admin/orders/${id}/status`, { status: 'SHIPPED' });
        updateRowStatus(id, 'SHIPPED');
        toast(`Bestellung #${id} als versendet markiert`, 'ok');
        // optional: location.reload();
      } else if (action === 'mark-cancelled') {
        if (!confirm('Diese Bestellung stornieren?')) return;
        // passt zu @PostMapping("/cancel/{id}")
        await postForm(`/admin/orders/cancel/${id}`);
        updateRowStatus(id, 'CANCELED');
        toast(`Bestellung #${id} storniert`, 'ok');
        // optional: row ausgrauen etc.
      } else if (action === 'delete-order') {
        if (!confirm('Diese Bestellung endgültig löschen?')) return;
        // passt zu @PostMapping("/delete/{id}")
        await postForm(`/admin/orders/delete/${id}`);
        const row = btn.closest('tr');
        if (row) row.remove();
        toast(`Bestellung #${id} gelöscht`, 'ok');
      }
    } catch (err) {
      console.error(err);
      toast('Fehler: ' + err.message, 'err');
    } finally {
      btn.disabled = false; btn.style.opacity = 1;
    }
  });
})();