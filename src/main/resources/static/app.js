const token = localStorage.getItem('token');
if (!token) {
    window.location.href = '/login.html';
}

const user = JSON.parse(localStorage.getItem('user') || '{}');
const userNameDisplay = document.getElementById('userNameDisplay');
if (userNameDisplay) userNameDisplay.textContent = user.fullName || user.email || '';

document.getElementById('logoutButton').addEventListener('click', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login.html';
});

const state = {
    applications: [],
    components: [],
    feedbacks: [],
    stats: null,
    selectedApplicationId: null
};

const elements = {
    applicationSelect: document.querySelector("#applicationSelect"),
    feedbackList: document.querySelector("#feedbackList"),
    listCount: document.querySelector("#listCount"),
    totalFeedbacks: document.querySelector("#totalFeedbacks"),
    averageRating: document.querySelector("#averageRating"),
    pendingCount: document.querySelector("#pendingCount"),
    rejectedCount: document.querySelector("#rejectedCount"),
    ratingDistribution: document.querySelector("#ratingDistribution"),
    toast: document.querySelector("#toast"),
    refreshButton: document.querySelector("#refreshButton")
};

async function api(path, options = {}) {
    const response = await fetch(`/api${path}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            ...(options.headers || {})
        },
        ...options
    });

    if (response.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login.html';
        return;
    }

    if (!response.ok) {
        let message = "Erreur serveur";
        try {
            const payload = await response.json();
            message = payload.detail || payload.messages?.join(" | ") || payload.message || message;
        } catch {
            message = await response.text();
        }
        throw new Error(message);
    }

    if (response.status === 204) return null;
    return response.json();
}

async function loadApplications() {
    state.applications = await api("/applications");
    if (!state.selectedApplicationId && state.applications.length > 0) {
        state.selectedApplicationId = state.applications[0].id;
    }
    if (!state.applications.some(a => a.id === state.selectedApplicationId)) {
        state.selectedApplicationId = state.applications[0]?.id || null;
    }
    renderApplicationSelect();
}

async function loadSelectedApplicationData() {
    if (!state.selectedApplicationId) {
        state.components = [];
        state.feedbacks = [];
        state.stats = null;
        renderStats();
        renderFeedbacks();
        return;
    }

    const id = state.selectedApplicationId;
    const [feedbacks, stats] = await Promise.all([
        api(`/applications/${id}/feedbacks`),
        api(`/applications/${id}/statistics`)
    ]);

    state.feedbacks = feedbacks;
    state.stats = stats;
    renderStats();
    renderFeedbacks();
}

async function refresh() {
    await loadApplications();
    await loadSelectedApplicationData();
}

function renderApplicationSelect() {
    elements.applicationSelect.innerHTML = state.applications.length === 0
        ? '<option value="">Aucune application</option>'
        : state.applications.map(a => `
            <option value="${a.id}" ${a.id === state.selectedApplicationId ? 'selected' : ''}>
                ${escapeHtml(a.name)}
            </option>`).join('');
}

function renderStats() {
    const stats = state.stats || {
        totalFeedbacks: 0, averageRating: 0, pendingCount: 0,
        rejectedCount: 0, ratingDistribution: { 1:0, 2:0, 3:0, 4:0, 5:0 }
    };

    elements.totalFeedbacks.textContent = `${stats.totalFeedbacks} avis`;
    elements.averageRating.textContent = Number(stats.averageRating || 0).toFixed(1);
    elements.pendingCount.textContent = stats.pendingCount || 0;
    elements.rejectedCount.textContent = stats.rejectedCount || 0;

    const max = Math.max(1, ...Object.values(stats.ratingDistribution || {}));
    elements.ratingDistribution.innerHTML = [5, 4, 3, 2, 1].map(rating => {
        const count = stats.ratingDistribution?.[rating] || 0;
        const width = Math.round((count / max) * 100);
        return `
            <div class="distribution-row">
                <span>${rating}/5</span>
                <div class="bar"><div class="bar-fill" style="width:${width}%"></div></div>
                <strong>${count}</strong>
            </div>`;
    }).join('');
}

function renderFeedbacks() {
    elements.listCount.textContent = state.feedbacks.length;

    if (state.feedbacks.length === 0) {
        elements.feedbackList.innerHTML = `<div class="empty-state">Aucun avis pour cette application.</div>`;
        return;
    }

    elements.feedbackList.innerHTML = state.feedbacks.map(feedback => `
        <article class="feedback-item">
            <div class="feedback-main">
                <div class="feedback-heading">
                    <span class="rating-pill">${feedback.rating}/5</span>
                    <span class="status-pill ${feedback.status}">${labelStatus(feedback.status)}</span>
                    <strong>${escapeHtml(feedback.componentName || "Général")}</strong>
                </div>
                <p class="feedback-comment">${escapeHtml(feedback.comment)}</p>
                <div class="feedback-meta">
                    ${escapeHtml(feedback.authorName || "Anonyme")}
                    ${feedback.authorEmail ? ` · ${escapeHtml(feedback.authorEmail)}` : ""}
                    · ${formatDate(feedback.createdAt)}
                </div>
            </div>
            <div class="status-actions" data-feedback-id="${feedback.id}">
                <button class="status-button approve" type="button" data-status="APPROVED">Valider</button>
                <button class="status-button" type="button" data-status="PENDING">Attente</button>
                <button class="status-button reject" type="button" data-status="REJECTED">Rejeter</button>
            </div>
        </article>`
    ).join('');
}

function labelStatus(status) {
    return { PENDING: "En attente", APPROVED: "Validé", REJECTED: "Rejeté" }[status] || status;
}

function formatDate(value) {
    if (!value) return "";
    return new Intl.DateTimeFormat("fr-FR", { dateStyle: "medium", timeStyle: "short" }).format(new Date(value));
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;").replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;").replaceAll('"', "&quot;").replaceAll("'", "&#039;");
}

function showToast(message) {
    elements.toast.textContent = message;
    elements.toast.classList.add("show");
    clearTimeout(showToast.timer);
    showToast.timer = setTimeout(() => elements.toast.classList.remove("show"), 2600);
}

elements.applicationSelect.addEventListener("change", async event => {
    state.selectedApplicationId = Number(event.target.value);
    await loadSelectedApplicationData();
});

elements.refreshButton.addEventListener("click", async () => {
    try { await refresh(); showToast("Données actualisées"); }
    catch (err) { showToast(err.message); }
});

document.getElementById('applicationForm').addEventListener("submit", async event => {
    event.preventDefault();
    const payload = {
        name: document.querySelector("#newApplicationName").value,
        description: document.querySelector("#newApplicationDescription").value,
        active: true
    };
    try {
        const created = await api("/applications", { method: "POST", body: JSON.stringify(payload) });
        state.selectedApplicationId = created.id;
        event.target.reset();
        await refresh();
        showToast("Application ajoutée");
    } catch (err) { showToast(err.message); }
});

document.getElementById('componentForm').addEventListener("submit", async event => {
    event.preventDefault();
    if (!state.selectedApplicationId) { showToast("Sélectionnez une application"); return; }
    const payload = {
        name: document.querySelector("#newComponentName").value,
        description: document.querySelector("#newComponentDescription").value,
        active: true
    };
    try {
        await api(`/applications/${state.selectedApplicationId}/components`, {
            method: "POST", body: JSON.stringify(payload)
        });
        event.target.reset();
        await loadSelectedApplicationData();
        showToast("Composant ajouté");
    } catch (err) { showToast(err.message); }
});

elements.feedbackList.addEventListener("click", async event => {
    const button = event.target.closest("[data-status]");
    if (!button) return;
    const container = button.closest("[data-feedback-id]");
    const feedbackId = container?.dataset.feedbackId;
    const status = button.dataset.status;
    try {
        await api(`/feedbacks/${feedbackId}/status`, {
            method: "PATCH", body: JSON.stringify({ status })
        });
        await loadSelectedApplicationData();
        showToast("Statut mis à jour");
    } catch (err) { showToast(err.message); }
});

refresh().catch(err => showToast(err.message));
