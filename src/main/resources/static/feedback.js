const appSelect = document.getElementById('applicationSelect');
const compSelect = document.getElementById('componentSelect');
const form = document.getElementById('feedbackForm');
const toast = document.getElementById('toast');

function showToast(msg) {
    toast.textContent = msg;
    toast.classList.add('show');
    clearTimeout(showToast._t);
    showToast._t = setTimeout(() => toast.classList.remove('show'), 2600);
}

async function loadApplications() {
    try {
        const res = await fetch('/api/applications/public');
        if (!res.ok) throw new Error();
        const apps = await res.json();
        if (apps.length === 0) {
            appSelect.innerHTML = '<option value="">Aucune application disponible</option>';
            return;
        }
        appSelect.innerHTML = apps.map(a =>
            `<option value="${a.id}">${a.name.replace(/</g,'&lt;')}</option>`
        ).join('');
        loadComponents(apps[0].id);
    } catch {
        appSelect.innerHTML = '<option value="">Erreur de chargement</option>';
    }
}

async function loadComponents(appId) {
    compSelect.innerHTML = '<option value="">Général</option>';
    if (!appId) return;
    try {
        const res = await fetch(`/api/applications/${appId}/components`);
        if (!res.ok) return;
        const comps = await res.json();
        comps.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.textContent = c.name;
            compSelect.appendChild(opt);
        });
    } catch {}
}

appSelect.addEventListener('change', () => loadComponents(appSelect.value));

form.addEventListener('submit', async event => {
    event.preventDefault();
    const btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.textContent = 'Envoi...';

    const payload = {
        applicationClientId: Number(appSelect.value),
        componentId: compSelect.value ? Number(compSelect.value) : null,
        rating: Number(form.querySelector('[name=rating]:checked').value),
        comment: form.comment.value.trim(),
        authorName: form.authorName.value.trim() || null,
        authorEmail: form.authorEmail.value.trim() || null
    };

    try {
        const res = await fetch('/api/feedbacks', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) {
            const data = await res.json().catch(() => ({}));
            throw new Error(data.detail || data.message || 'Erreur lors de l\'envoi');
        }
        form.style.display = 'none';
        document.getElementById('successState').classList.add('show');
    } catch (err) {
        showToast(err.message);
        btn.disabled = false;
        btn.textContent = 'Envoyer mon avis';
    }
});

function resetForm() {
    form.reset();
    document.getElementById('rating5').checked = true;
    form.style.display = '';
    document.getElementById('successState').classList.remove('show');
}

loadApplications();
