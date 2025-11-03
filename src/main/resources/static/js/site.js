// site.js
// Kleine, robuste Helferlein für deinen Webshop.

// ================ Theme (Dark/Light) =================
(function themeInit() {
  const STORAGE_KEY = "ws-theme"; // "dark" | "light"
  const root = document.documentElement;
  const btn = document.getElementById("themeToggle");

  function apply(theme) {
    if (theme === "light") root.classList.add("light");
    else root.classList.remove("light");
  }
  function current() {
    return root.classList.contains("light") ? "light" : "dark";
  }

  // Beim Laden anwenden (Fallback: Dark)
  const saved = localStorage.getItem(STORAGE_KEY);
  if (saved) apply(saved);

  if (btn) {
    btn.addEventListener("click", () => {
      const next = current() === "light" ? "dark" : "light";
      apply(next);
      localStorage.setItem(STORAGE_KEY, next);
      btn.blur();
    });
  }
})();

// ================ Suchfeld Shortcut ===================
// Tipp auf "/" fokussiert die Suche (wie GitHub)
(function searchShortcut() {
  const input = document.querySelector(".nav .search input[name='q']");
  if (!input) return;
  window.addEventListener("keydown", (e) => {
    if (e.key === "/" && !e.ctrlKey && !e.metaKey && !e.altKey) {
      // Nicht in einem Eingabefeld überschreiben
      const tag = (document.activeElement?.tagName || "").toLowerCase();
      if (!["input", "textarea", "select"].includes(tag)) {
        e.preventDefault();
        input.focus();
        input.select();
      }
    }
  });
})();

// ================ Delete-Bestätigung ===================
(function confirmDeletes() {
  const forms = Array.from(document.querySelectorAll("form"))
    .filter(f => /\/delete$/.test(f.getAttribute("action") || ""));

  forms.forEach(f => {
    f.addEventListener("submit", (e) => {
      const ok = confirm("Wirklich löschen? Das kann nicht rückgängig gemacht werden.");
      if (!ok) e.preventDefault();
    });
  });
})();

// ================ Mengen-Felder absichern =============
// Verhindert versehentliches Scrollen in number-Inputs
(function hardenNumberInputs() {
  document.addEventListener("wheel", (e) => {
    const el = e.target;
    if (el instanceof HTMLInputElement && el.type === "number" && document.activeElement === el) {
      el.blur(); // erst scrollen, dann wieder focus möglich
    }
  }, { passive: true });

  // Min=1 erzwingen für qty-Felder
  document.querySelectorAll("input[name='qty'][type='number']").forEach(inp => {
    inp.addEventListener("change", () => {
      const v = Math.max(1, parseInt(inp.value || "1", 10));
      inp.value = String(v);
    });
  });
})();

// ================ Mini-Toast ==========================
// Kleines, simples Toast-System für Feedback
const Toast = (() => {
  let box;
  function ensure() {
    if (!box) {
      box = document.createElement("div");
      box.style.position = "fixed";
      box.style.left = "50%";
      box.style.bottom = "18px";
      box.style.transform = "translateX(-50%)";
      box.style.background = "var(--card)";
      box.style.border = "1px solid var(--border)";
      box.style.borderRadius = "12px";
      box.style.padding = "0.7rem 1rem";
      box.style.boxShadow = "0 12px 28px rgba(0,0,0,.25)";
      box.style.zIndex = "9999";
      box.style.opacity = "0";
      box.style.transition = "opacity .15s ease";
      document.body.appendChild(box);
    }
  }
  return {
    show(msg, ms = 1600) {
      ensure();
      box.textContent = msg;
      box.style.opacity = "1";
      clearTimeout(box._t);
      box._t = setTimeout(() => (box.style.opacity = "0"), ms);
    }
  };
})();

// ================ AJAX: In den Warenkorb ==============
// Interzeptiert nur Add-to-Cart-Formulare; alles andere bleibt klassisch.
// Progressive Enhancement: Fällt automatisch auf normales Submit zurück, wenn etwas schief geht.
(function ajaxAddToCart() {
  const forms = Array
    .from(document.querySelectorAll("form.inline[action*='/cart/add/'][method='post']"));

  if (forms.length === 0) return;

  // CSRF-Token aus Cookie (falls CSRF später wieder aktiv)
  function readCookie(name) {
    return document.cookie
      .split("; ")
      .find(row => row.startsWith(name + "="))
      ?.split("=")[1];
  }
  const csrfCookie = readCookie("XSRF-TOKEN");

  forms.forEach(f => {
    f.addEventListener("submit", async (e) => {
      e.preventDefault();
      const btn = f.querySelector("button[type='submit']") || f.querySelector("button");
      const fd = new FormData(f);
      const action = f.getAttribute("action");

      try {
        btn && (btn.disabled = true);
        const res = await fetch(action, {
          method: "POST",
          body: fd,
          headers: csrfCookie ? { "X-XSRF-TOKEN": decodeURIComponent(csrfCookie) } : undefined,
          redirect: "manual" // wir wollen keinen Seitenwechsel
        });

        if (res.ok || res.status === 0 || (res.status >= 300 && res.status < 400)) {
          // Erfolg: wir zeigen ein Toast. (Optional: Cart-Badge aktualisieren)
          Toast.show("Zum Warenkorb hinzugefügt 🛒");
          // kleine visuelle Bestätigung am Button
          if (btn) {
            const old = btn.textContent;
            btn.textContent = "✓ Hinzugefügt";
            setTimeout(() => (btn.textContent = old), 1200);
          }
        } else {
          // Fallback auf klassisches Submit, wenn der Server etwas anderes erwartet
          f.submit();
        }
      } catch (err) {
        // Netzwerkproblem → klassischer Submit
        f.submit();
      } finally {
        btn && (btn.disabled = false);
      }
    });
  });
})();