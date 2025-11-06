(function () {
  function onlyDigit(s) {
    return (s || "").replace(/\D/g, "").slice(-1);
  }

  function focus(el) {
    if (!el) return;
    // minimal Delay, damit Cursor zuverlässig springt
    setTimeout(() => el.focus(), 0);
  }

  function collectCode(inputs) {
    return Array.from(inputs).map(i => (i.value || "").replace(/\D/g, "")).join("");
  }

  document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("verify-form");
    if (!form) return;

    const inputs = form.querySelectorAll(".code-box");
    const hidden = document.getElementById("code-full");

    // initialer Fokus
    focus(inputs[0]);

    inputs.forEach((input, idx) => {
      // Nur Ziffern zulassen + Auto-Advance
      input.addEventListener("input", (e) => {
        const digit = onlyDigit(e.target.value);
        e.target.value = digit;

        if (digit && idx < inputs.length - 1) {
          focus(inputs[idx + 1]);
        }
        hidden.value = collectCode(inputs);
      });

      // Backspace: wenn leer, springe zurück
      input.addEventListener("keydown", (e) => {
        if (e.key === "Backspace" && !e.target.value && idx > 0) {
          e.preventDefault();
          inputs[idx - 1].value = "";
          focus(inputs[idx - 1]);
          hidden.value = collectCode(inputs);
        }
        // Pfeiltasten-Navigation (nice to have)
        if (e.key === "ArrowLeft" && idx > 0) {
          e.preventDefault();
          focus(inputs[idx - 1]);
        }
        if (e.key === "ArrowRight" && idx < inputs.length - 1) {
          e.preventDefault();
          focus(inputs[idx + 1]);
        }
      });
    });

    // Paste: egal wo eingefügt wird – fülle alle Felder
    document.addEventListener("paste", (e) => {
      const text = (e.clipboardData || window.clipboardData)?.getData("text") || "";
      const digits = (text.match(/\d/g) || []).slice(0, inputs.length);
      if (!digits.length) return;

      e.preventDefault();
      inputs.forEach((input, i) => input.value = digits[i] || "");
      const lastIdx = Math.min(digits.length - 1, inputs.length - 1);
      focus(inputs[Math.max(lastIdx, 0)]);
      hidden.value = collectCode(inputs);
    });

    // Beim Absenden sicherstellen, dass Hidden-Feld korrekt ist
    form.addEventListener("submit", () => {
      hidden.value = collectCode(inputs);
    });
  });
})();