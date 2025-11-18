(function () {
  const STORAGE_KEY = "webshop_last_checkout_address";

  function $(id) {
    return document.getElementById(id);
  }

  function loadAddress() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) return;
      const data = JSON.parse(raw);

      if (data.fullName && $("fullName")) $("fullName").value = data.fullName;
      if (data.street && $("street")) $("street").value = data.street;
      if (data.postalCode && $("postalCode")) $("postalCode").value = data.postalCode;
      if (data.city && $("city")) $("city").value = data.city;
      if (data.country && $("country")) $("country").value = data.country;
    } catch (e) {
      console.warn("Konnte gespeicherte Adresse nicht laden:", e);
    }
  }

  function saveAddressIfWanted() {
    const remember = $("rememberAddress");
    if (!remember || !remember.checked) {
      localStorage.removeItem(STORAGE_KEY);
      return;
    }

    const data = {
      fullName: $("fullName")?.value ?? "",
      street: $("street")?.value ?? "",
      postalCode: $("postalCode")?.value ?? "",
      city: $("city")?.value ?? "",
      country: $("country")?.value ?? ""
    };

    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    } catch (e) {
      console.warn("Konnte Adresse nicht speichern:", e);
    }
  }

  function enhanceValidation() {
    const form = document.querySelector("form.checkout-form");
    if (!form) return;

    form.addEventListener("submit", function (e) {
      if (!form.checkValidity()) {
        e.preventDefault();
        form.classList.add("was-validated");
        const firstInvalid = form.querySelector(":invalid");
        if (firstInvalid && typeof firstInvalid.focus === "function") {
          firstInvalid.focus();
        }
      } else {
        saveAddressIfWanted();
      }
    });

    form.querySelectorAll("input").forEach(function (input) {
      input.addEventListener("blur", function () {
        if (input.checkValidity()) {
          input.classList.remove("is-invalid");
          input.classList.add("is-valid");
        } else if (input.value !== "") {
          input.classList.add("is-invalid");
          input.classList.remove("is-valid");
        }
      });
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    loadAddress();
    enhanceValidation();
  });
})();