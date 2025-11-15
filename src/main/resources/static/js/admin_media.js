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

    // Delete-Bestätigung
    document.body.addEventListener('submit', function (event) {
        const form = event.target;
        if (!form.matches('.js-delete-form')) return;

        const ok = window.confirm('Datei wirklich löschen?');
        if (!ok) {
            event.preventDefault();
        }
    });

});