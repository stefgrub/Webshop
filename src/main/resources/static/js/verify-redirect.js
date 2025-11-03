document.addEventListener("DOMContentLoaded", () => {
  const successBox = document.querySelector(".alert-success[data-redirect='login']");
  if (!successBox) return;

  // Nach 3 Sekunden weiterleiten:
  setTimeout(() => {
    window.location.href = "/login?verified";
  }, 3000);
});