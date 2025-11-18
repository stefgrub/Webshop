document.addEventListener('DOMContentLoaded', function () {

    // URL kopieren
    document.body.addEventListener('click', function (event) {
        const btn = event.target.closest('.js-copy-url');
        if (!btn) return;

        const url = btn.getAttribute('data-url');
        if (!url) return;

        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(url).catch(function () {
                fallbackCopy(url);
            });
        } else {
            fallbackCopy(url);
        }
    });

    function fallbackCopy(text) {
        const tmp = document.createElement('input');
        tmp.type = 'text';
        tmp.value = text;
        document.body.appendChild(tmp);
        tmp.select();
        try {
            document.execCommand('copy');
        } catch (e) {
            console.warn('Kopieren nicht möglich', e);
        }
        document.body.removeChild(tmp);
    }

    // Einzel-Löschen: Bestätigung
    document.body.addEventListener('submitdel', function (event) {
        const form = event.target;
        if (!form.matches('.js-delete-form')) return;

        const ok = window.confirm('Datei wirklich löschen?');
        if (!ok) {
            event.preventDefault();
        }
    });

    // Bulk-Delete: Bestätigung
    document.body.addEventListener('submit', function (event) {
        const form = event.target;
        if (!form.matches('#bulkDeleteForm')) return;

        const anyChecked = !!form.querySelector('.js-select-item:checked');
        if (!anyChecked) {
            alert('Bitte wähle mindestens eine Datei aus.');
            event.preventDefault();
            return;
        }

        const ok = window.confirm('Ausgewählte Dateien wirklich löschen?');
        if (!ok) {
            event.preventDefault();
        }
    });

    // Select All
    document.body.addEventListener('change', function (event) {
        const master = event.target.closest('.js-select-all');
        if (!master) return;

        const checked = master.checked;
        document.querySelectorAll('.js-select-item').forEach(function (cb) {
            cb.checked = checked;
        });
    });

});