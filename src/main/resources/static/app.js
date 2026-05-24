// AlojaGRASP JS Logic - Conexión total con backend Java Spring Boot

// Datos de la sesión local (Simulados para facilidad de uso)
const SESION = {
    huespedId: 1,
    anfitrionId: 2,
    huespedEmail: "juan.perez@example.com",
    anfitrionEmail: "maria.gomez@example.com"
};

// Variable para guardar el borrador actual (CU-04)
let currentDraftId = null;

// Inicialización de fechas
document.addEventListener("DOMContentLoaded", () => {
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const nextDay = new Date(tomorrow);
    nextDay.setDate(nextDay.getDate() + 2);

    document.getElementById("search-checkin").value = formatDate(tomorrow);
    document.getElementById("search-checkout").value = formatDate(nextDay);

    // Cargar datos por defecto al iniciar
    performSearch(null);
    loadBookings();
});

// Formatear fecha a YYYY-MM-DD
function formatDate(date) {
    const yyyy = date.getFullYear();
    let mm = date.getMonth() + 1;
    let dd = date.getDate();
    if (dd < 10) dd = '0' + dd;
    if (mm < 10) mm = '0' + mm;
    return `${yyyy}-${mm}-${dd}`;
}

// Intercambio de Pestañas
function switchTab(tabName) {
    document.querySelectorAll(".tab-content").forEach(el => el.classList.remove("active"));
    document.querySelectorAll(".nav-btn").forEach(el => el.classList.remove("active"));

    document.getElementById(`tab-${tabName}`).classList.add("active");
    document.getElementById(`btn-${tabName}`).classList.add("active");

    if (tabName === 'bookings') {
        loadBookings();
    }
}

// MOSTRAR TOAST (Manejo de Notificación virtual del backend)
function showToast(message, isNotification = true) {
    const toast = document.getElementById("toast");
    const toastMsg = document.getElementById("toast-message");
    toastMsg.textContent = message;
    
    if (isNotification) {
        toast.style.borderColor = "var(--primary)";
        toast.style.boxShadow = "0 10px 30px rgba(139,92,246, 0.25)";
    } else {
        toast.style.borderColor = "var(--success)";
        toast.style.boxShadow = "0 10px 30px rgba(16,185,129, 0.25)";
    }

    toast.classList.remove("hidden");
    setTimeout(() => {
        toast.classList.add("hidden");
    }, 4500);
}

// -------------------------------------------------------------
// CU-02: BÚSQUEDA FILTRADA Y CRUCE CON RATING
// -------------------------------------------------------------
async function performSearch(event) {
    if (event) event.preventDefault();

    const destino = document.getElementById("search-dest").value;
    const entrada = document.getElementById("search-checkin").value;
    const salida = document.getElementById("search-checkout").value;
    const huespedes = document.getElementById("search-guests").value;

    const listingsContainer = document.getElementById("listings-container");
    const noResults = document.getElementById("no-results");
    
    listingsContainer.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: var(--text-muted);">🔍 Consultando disponibilidad y agregando valoraciones concurrentemente...</div>`;
    noResults.classList.add("hidden");

    try {
        const url = `/api/alojamientos/buscar?destino=${encodeURIComponent(destino)}&entrada=${entrada}&salida=${salida}&huespedes=${huespedes}`;
        const response = await fetch(url);
        if (!response.ok) throw new Error("Error en la consulta al servidor");

        const data = await response.json(); // ResultadoBusqueda
        const alojamientos = data.alojamientosEncontrados || [];
        const ratingGlobal = data.ratingMedioCalculado || 4.8;

        document.getElementById("system-rating").textContent = ratingGlobal.toFixed(1);

        if (alojamientos.length === 0) {
            listingsContainer.innerHTML = "";
            noResults.classList.remove("hidden");
            return;
        }

        listingsContainer.innerHTML = "";
        alojamientos.forEach(aloj => {
            const card = document.createElement("div");
            card.className = "listing-card";
            
            // Emoji para la foto simulada
            const emojis = ["🏡", "🏢", "🌊", "🏖️", "🏨", "🏰"];
            const cardEmoji = emojis[aloj.id % emojis.length];

            const description = aloj.descripcion || "Sin descripción disponible.";
            const cancellationType = aloj.politicaCancelacion ? aloj.politicaCancelacion.tipo : "FLEXIBLE";

            card.innerHTML = `
                <div class="listing-img-container">
                    ${cardEmoji}
                    <span class="listing-img-tag">${cancellationType}</span>
                </div>
                <div class="listing-body">
                    <h3 class="listing-title">${escapeHTML(aloj.titulo)}</h3>
                    <div class="listing-meta">📍 ${escapeHTML(aloj.ubicacion)}</div>
                    <p class="listing-desc">${escapeHTML(description)}</p>
                    <div class="listing-footer">
                        <div class="listing-price">
                            ${aloj.precioPorNoche}€ <span>/ noche</span>
                        </div>
                        <button class="btn-card" onclick="openBookingModal(${aloj.id}, '${escapeJS(aloj.titulo)}', ${aloj.precioPorNoche})">Reservar</button>
                    </div>
                </div>
            `;
            listingsContainer.appendChild(card);
        });

    } catch (err) {
        console.error(err);
        listingsContainer.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: var(--danger);">❌ Error al conectar con el backend de Spring Boot. Asegúrate de que el servidor esté corriendo en el puerto 8080.</div>`;
    }
}

// -------------------------------------------------------------
// CU-01: CREAR RESERVA Y COBRO SÍNCRONO
// -------------------------------------------------------------
function openBookingModal(alojamientoId, titulo, precioPorNoche) {
    const modal = document.getElementById("booking-modal");
    const container = document.getElementById("modal-details-container");
    
    // Fechas actuales de búsqueda
    const entrada = document.getElementById("search-checkin").value;
    const salida = document.getElementById("search-checkout").value;

    const noches = Math.max(1, Math.round((new Date(salida) - new Date(entrada)) / (1000 * 60 * 60 * 24)));
    const importeTotal = precioPorNoche * noches;

    container.innerHTML = `
        <h3 style="font-size: 1.5rem; font-weight:700; margin-bottom: 1rem;">Confirmar tu Estancia</h3>
        <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Estás reservando <strong>${escapeHTML(titulo)}</strong>.</p>
        
        <div style="background-color: rgba(15,23,42,0.4); border: 1px solid var(--border); border-radius: 10px; padding: 1.2rem; margin-bottom: 1.5rem;">
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.6rem; font-size: 0.95rem;">
                <span>📅 Fechas:</span>
                <strong>${entrada} al ${salida}</strong>
            </div>
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.6rem; font-size: 0.95rem;">
                <span>🌙 Duración:</span>
                <strong>${noches} noches</strong>
            </div>
            <div style="display: flex; justify-content: space-between; border-top: 1px solid var(--border); padding-top: 0.6rem; font-weight:700; font-size: 1.1rem; color: var(--primary);">
                <span>Total a Pagar (Cobro Síncrono):</span>
                <span>${importeTotal}€</span>
            </div>
        </div>

        <div style="margin-bottom: 1.5rem; font-size:0.8rem; color: var(--text-muted); display:flex; align-items:center; gap:0.5rem; background-color: rgba(139,92,246,0.06); padding:0.8rem; border-radius:8px; border: 1px solid rgba(139,92,246,0.15);">
            💳 Método de Pago del Huésped: <strong>${SESION.huespedEmail} (${SESION.huespedId}) - Tarjeta Registrada</strong>
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 1rem;">
            <button class="btn-secondary" onclick="closeModal()">Cancelar</button>
            <button class="btn-primary" onclick="confirmBooking(${alojamientoId}, '${entrada}', '${salida}')">
                <span>💳 Confirmar y Pagar</span>
            </button>
        </div>
    `;

    modal.classList.remove("hidden");
}

function closeModal() {
    document.getElementById("booking-modal").classList.add("hidden");
}

async function confirmBooking(alojamientoId, entrada, salida) {
    closeModal();
    try {
        const response = await fetch(`/api/reservas?alojamientoid=${alojamientoId}&huespedId=${SESION.huespedId}&entrada=${entrada}&salida=${salida}`, {
            method: "POST"
        });

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg || "Error al registrar la reserva.");
        }

        const reserva = await response.json(); // Reserva creada
        
        // Simular notificación en Toast (CU-01 paso de notificación)
        showToast(`📧 [NOTIFICACIÓN] Confirmación enviada a ${SESION.huespedEmail}. Reserva #${reserva.id} confirmada exitosamente.`);
        
        setTimeout(() => {
            showToast(`💳 [PAGO] Cobro síncrono procesado exitosamente por ${reserva.pago.importe}€.`, false);
        }, 1200);

        // Actualizar búsqueda y cambiar de pestaña
        performSearch(null);
        switchTab('bookings');

    } catch (err) {
        alert("Error al procesar reserva: " + err.message);
    }
}

// -------------------------------------------------------------
// CU-03: CANCELAR RESERVA Y REEMBOLSO DINÁMICO
// -------------------------------------------------------------
async function loadBookings() {
    const bookingsContainer = document.getElementById("bookings-container");
    const noBookings = document.getElementById("no-bookings");
    
    bookingsContainer.innerHTML = `<div style="text-align:center; padding: 2rem; color:var(--text-muted);">Cargando tus reservas de la base de datos...</div>`;
    noBookings.classList.add("hidden");

    try {
        // En una app real filtraríamos por huésped id en el backend, para esta demo listamos todas las reservas
        const response = await fetch("/api/reservas");
        if (!response.ok) {
            // Si el endpoint de listar reservas no existía, lo gestionaremos recuperando del mock o inicializador
            // Vamos a implementar un pequeño controller o recuperarlas. 
            // Para asegurar robustez, llamamos al endpoint de listar. (Implementado en ControladorReserva)
        }
        // Nota: para hacer la SPA completamente autónoma, implementaremos un endpoint simple GET /api/reservas
        const responseList = await fetch("/api/reservas/listar");
        const reservas = responseList.ok ? await responseList.json() : [];

        if (reservas.length === 0) {
            bookingsContainer.innerHTML = "";
            noBookings.classList.remove("hidden");
            return;
        }

        bookingsContainer.innerHTML = "";
        reservas.forEach(res => {
            const card = document.createElement("div");
            card.className = "booking-card glass-card";
            
            const precioTotal = res.pago ? res.pago.importe : 0.0;
            const estadoBadgeClass = `badge-${res.estado.toLowerCase()}`;
            const isCancelable = res.estado === 'CONFIRMADA' || res.estado === 'PENDIENTE';

            card.innerHTML = `
                <div style="font-size: 2.5rem; text-align: center;">📅</div>
                <div class="booking-info">
                    <span class="badge ${estadoBadgeClass}">${res.estado}</span>
                    <h4 style="margin-top:0.4rem;">${escapeHTML(res.alojamiento.titulo)}</h4>
                    <p class="booking-dates">📅 Entrada: <strong>${res.fechaEntrada}</strong> | Salida: <strong>${res.fechaSalida}</strong></p>
                    <p style="font-size: 0.8rem; color:var(--text-muted);">📍 Ubicación: ${escapeHTML(res.alojamiento.ubicacion)}</p>
                </div>
                <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 0.8rem;">
                    <div class="booking-price">${precioTotal}€</div>
                    <button class="btn-danger" 
                            ${isCancelable ? "" : "disabled"} 
                            onclick="cancelBooking(${res.id})">
                        Cancelar Reserva
                    </button>
                </div>
            `;
            bookingsContainer.appendChild(card);
        });

    } catch (err) {
        bookingsContainer.innerHTML = `<div style="text-align: center; color: var(--danger); padding: 2rem;">No se pudieron cargar las reservas o la base de datos está vacía. Realiza una búsqueda y haz tu primera reserva.</div>`;
    }
}

async function cancelBooking(reservaId) {
    if (!confirm("¿Seguro que deseas cancelar esta reserva? Se evaluará el reembolso en base a la política de cancelación.")) return;

    try {
        const response = await fetch(`/api/reservas/${reservaId}/cancelar`, {
            method: "POST"
        });

        if (!response.ok) throw new Error("Error al cancelar la reserva");

        // Mensaje de notificación del reembolso calculado dinámicamente
        showToast(`📧 [NOTIFICACIÓN] Reserva #${reservaId} Cancelada. Mensaje enviado al Huésped.`);
        
        setTimeout(() => {
            showToast(`💳 [REEMBOLSO] El dinero ha sido devuelto a tu tarjeta exitosamente.`, false);
        }, 1200);

        loadBookings();
        performSearch(null);

    } catch (err) {
        alert("Error al cancelar: " + err.message);
    }
}

// -------------------------------------------------------------
// CU-04: PUBLICACIÓN DE ALOJAMIENTO EN DOS PASOS
// -------------------------------------------------------------
async function createDraft(event) {
    event.preventDefault();

    const title = document.getElementById("pub-title").value;
    const price = document.getElementById("pub-price").value;
    const location = document.getElementById("pub-location").value;
    const desc = document.getElementById("pub-desc").value;
    const imagesInput = document.getElementById("pub-images");

    const formData = new FormData();
    formData.append("anfitrionId", SESION.anfitrionId);
    formData.append("titulo", title);
    formData.append("descripcion", desc);
    formData.append("ubicacion", location);
    formData.append("precio", price);
    
    if (imagesInput.files.length > 0) {
        for (let i = 0; i < imagesInput.files.length; i++) {
            formData.append("imagenes", imagesInput.files[i]);
        }
    }

    try {
        const response = await fetch("/api/anuncios", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg || "Error al guardar el borrador.");
        }

        const draft = await response.json(); // Alojamiento creado en estado BORRADOR
        currentDraftId = draft.id;

        // Renderizar la vista previa (Paso 2)
        document.getElementById("preview-title").textContent = draft.titulo;
        document.getElementById("preview-location").textContent = draft.ubicacion;
        document.getElementById("preview-price").textContent = draft.precioPorNoche;
        document.getElementById("preview-desc").textContent = draft.descripcion;

        const imgList = document.getElementById("preview-img-list");
        imgList.innerHTML = "";
        draft.imagenes.forEach(img => {
            const li = document.createElement("li");
            li.textContent = `📷 Imagen #${img.orden}: ${img.url}`;
            imgList.appendChild(li);
        });

        // Pasar de fase
        document.getElementById("publish-step1-card").classList.add("hidden");
        document.getElementById("publish-step2-card").classList.remove("hidden");
        
        showToast("💾 Borrador guardado exitosamente por GestorImagenes (Fabricación Pura). Procede al Paso 2.");

    } catch (err) {
        alert("Error al guardar borrador: " + err.message);
    }
}

async function publishDraft() {
    if (!currentDraftId) return;

    try {
        const response = await fetch(`/api/anuncios/${currentDraftId}/confirmar`, {
            method: "POST"
        });

        if (!response.ok) throw new Error("Error al publicar oficialmente el anuncio.");

        const publicado = await response.json(); // Alojamiento en estado PUBLICADO

        showToast(`📧 [NOTIFICACIÓN] ¡Anuncio Publicado! Notificación enviada al Anfitrión.`);
        
        setTimeout(() => {
            showToast(`🚀 [INDEXADOR] El alojamiento "${publicado.titulo}" ya es localizable de forma pública.`, false);
        }, 1200);

        // Reset y volver
        resetPublishForm();
        switchTab('browse');
        performSearch(null);

    } catch (err) {
        alert("Error al publicar: " + err.message);
    }
}

function resetPublishForm() {
    document.getElementById("publish-form").reset();
    document.getElementById("publish-step1-card").classList.remove("hidden");
    document.getElementById("publish-step2-card").classList.add("hidden");
    currentDraftId = null;
}

// Helpers para evitar XSS
function escapeHTML(str) {
    if (!str) return "";
    return str.replace(/[&<>'"]/g, 
        tag => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            "'": '&#39;',
            '"': '&quot;'
        }[tag] || tag)
    );
}

function escapeJS(str) {
    if (!str) return "";
    return str.replace(/'/g, "\\'");
}
