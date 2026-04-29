const state = {
    applications: [],
    components: [],
    feedbacks: [],
    stats: null,
    selectedApplicationId: null
};

const elements = {
    applicationSelect: document.querySelector("#applicationSelect"),
    componentSelect: document.querySelector("#componentSelect"),
    selectedApplicationName: document.querySelector("#selectedApplicationName"),
    feedbackForm: document.querySelector("#feedbackForm"),
    applicationForm: document.querySelector("#applicationForm"),
    componentForm: document.querySelector("#componentForm"),
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
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {
        let message = "Erreur serveur";
        try {
            const payload = await response.json();
            message = payload.messages?.join(" | ") || payload.message || message;
        } catch (error) {
            message = await response.text();
        }
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

async function loadApplications() {
    state.applications = await api("/applications");
    if (!state.selectedApplicationId && state.applications.length > 0) {
        state.selectedApplicationId = state.applications[0].id;
    }

    if (!state.applications.some(application => application.id === state.selectedApplicationId)) {
        state.selectedApplicationId = state.applications[0]?.id || null;
    }

    renderApplications();
}

async function loadSelectedApplicationData() {
    if (!state.selectedApplicationId) {
        state.components = [];
        state.feedbacks = [];
        state.stats = null;
        renderAll();
        return;
    }

    const applicationId = state.selectedApplicationId;
    const [components, feedbacks, stats] = await Promise.all([
        api(`/applications/${applicationId}/components`),
        api(`/applications/${applicationId}/feedbacks`),
        api(`/applications/${applicationId}/statistics`)
    ]);

    state.components = components;
    state.feedbacks = feedbacks;
    state.stats = stats;
    renderAll();
}

async function refresh() {
    await loadApplications();
    await loadSelectedApplicationData();
}

function renderAll() {
    renderApplications();
    renderComponents();
    renderStats();
    renderFeedbacks();
}

function renderApplications() {
    elements.applicationSelect.innerHTML = state.applications.map(application => `
        <option value="${application.id}" ${application.id === state.selectedApplicationId ? "selected" : ""}>
            ${escapeHtml(application.name)}
        </option>
    `).join("");

    const selectedApplication = getSelectedApplication();
    elements.selectedApplicationName.textContent = selectedApplication?.name || "Application";
}

function renderComponents() {
    elements.componentSelect.innerHTML = `
        <option value="">General</option>
        ${state.components.map(component => `
            <option value="${component.id}">${escapeHtml(component.name)}</option>
        `).join("")}
    `;
}

function renderStats() {
    const stats = state.stats || {
        totalFeedbacks: 0,
        averageRating: 0,
        pendingCount: 0,
        rejectedCount: 0,
        ratingDistribution: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 }
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
                <div class="bar"><div class="bar-fill" style="width: ${width}%"></div></div>
                <strong>${count}</strong>
            </div>
        `;
    }).join("");
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
                    <strong>${escapeHtml(feedback.componentName || "General")}</strong>
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
        </article>
    `).join("");
}

function getSelectedApplication() {
    return state.applications.find(application => application.id === state.selectedApplicationId);
}

function labelStatus(status) {
    return {
        PENDING: "En attente",
        APPROVED: "Valide",
        REJECTED: "Rejete"
    }[status] || status;
}

function formatDate(value) {
    if (!value) {
        return "";
    }
    return new Intl.DateTimeFormat("fr-FR", {
        dateStyle: "medium",
        timeStyle: "short"
    }).format(new Date(value));
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function showToast(message) {
    elements.toast.textContent = message;
    elements.toast.classList.add("show");
    window.clearTimeout(showToast.timer);
    showToast.timer = window.setTimeout(() => elements.toast.classList.remove("show"), 2600);
}

elements.applicationSelect.addEventListener("change", async event => {
    state.selectedApplicationId = Number(event.target.value);
    await loadSelectedApplicationData();
});

elements.refreshButton.addEventListener("click", async () => {
    try {
        await refresh();
        showToast("Donnees actualisees");
    } catch (error) {
        showToast(error.message);
    }
});

elements.feedbackForm.addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;

    if (!state.selectedApplicationId) {
        showToast("Ajoutez d'abord une application");
        return;
    }

    const formData = new FormData(form);
    const payload = {
        applicationClientId: state.selectedApplicationId,
        componentId: formData.get("componentId") ? Number(formData.get("componentId")) : null,
        rating: Number(formData.get("rating")),
        comment: formData.get("comment"),
        authorName: formData.get("authorName"),
        authorEmail: formData.get("authorEmail")
    };

    try {
        await api("/feedbacks", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        form.reset();
        document.querySelector("#rating5").checked = true;
        await loadSelectedApplicationData();
        showToast("Avis enregistre");
    } catch (error) {
        showToast(error.message);
    }
});

elements.applicationForm.addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;

    const payload = {
        name: document.querySelector("#newApplicationName").value,
        description: document.querySelector("#newApplicationDescription").value,
        active: true
    };

    try {
        const created = await api("/applications", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        state.selectedApplicationId = created.id;
        form.reset();
        await refresh();
        showToast("Application ajoutee");
    } catch (error) {
        showToast(error.message);
    }
});

elements.componentForm.addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;

    if (!state.selectedApplicationId) {
        showToast("Selectionnez une application");
        return;
    }

    const payload = {
        name: document.querySelector("#newComponentName").value,
        description: document.querySelector("#newComponentDescription").value,
        active: true
    };

    try {
        await api(`/applications/${state.selectedApplicationId}/components`, {
            method: "POST",
            body: JSON.stringify(payload)
        });
        form.reset();
        await loadSelectedApplicationData();
        showToast("Composant ajoute");
    } catch (error) {
        showToast(error.message);
    }
});

elements.feedbackList.addEventListener("click", async event => {
    const button = event.target.closest("[data-status]");
    if (!button) {
        return;
    }

    const container = button.closest("[data-feedback-id]");
    const feedbackId = container?.dataset.feedbackId;
    const status = button.dataset.status;

    try {
        await api(`/feedbacks/${feedbackId}/status`, {
            method: "PATCH",
            body: JSON.stringify({ status })
        });
        await loadSelectedApplicationData();
        showToast("Statut mis a jour");
    } catch (error) {
        showToast(error.message);
    }
});

refresh().catch(error => showToast(error.message));
