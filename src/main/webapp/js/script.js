/**
 * Vehicle Rental Management System - Master Client Script
 * Demonstrates extensive DOM manipulation, event handling, dynamic calculations,
 * and seamless interaction with Java Servlets.
 */

// Global State
let currentUser = null;
let allVehicles = [];

document.addEventListener('DOMContentLoaded', () => {
    initAuthHeader();
    initUrlParamsFeedback();
});

// ==========================================================================
// 1. Authentication State & Navbar DOM Manipulation
// ==========================================================================
function initAuthHeader() {
    fetch('api/current-user')
        .then(res => res.json())
        .then(data => {
            currentUser = data.loggedIn ? data : null;
            updateNavDOM(currentUser);
            
            // Trigger page-specific initializers that depend on auth
            if (typeof onAuthReady === 'function') {
                onAuthReady(currentUser);
            }
        })
        .catch(err => {
            console.warn('Auth state check error:', err);
            updateNavDOM(null);
        });
}

function updateNavDOM(user) {
    const navAuth = document.getElementById('navAuth');
    const navLinks = document.getElementById('navLinks');
    if (!navAuth) return;

    navAuth.innerHTML = '';

    if (user && user.loggedIn) {
        // Create User Badge Element
        const userBadge = document.createElement('div');
        userBadge.className = 'user-badge';

        const userNameSpan = document.createElement('span');
        userNameSpan.textContent = user.name || user.username;

        const roleTag = document.createElement('span');
        roleTag.className = 'role-tag';
        roleTag.textContent = user.role;

        userBadge.appendChild(userNameSpan);
        userBadge.appendChild(roleTag);

        // Logout Button
        const logoutBtn = document.createElement('a');
        logoutBtn.href = 'logout';
        logoutBtn.className = 'btn btn-secondary btn-sm';
        logoutBtn.textContent = 'Logout';

        navAuth.appendChild(userBadge);
        navAuth.appendChild(logoutBtn);

        // Add Dashboard link based on role if navLinks present
        if (navLinks && !document.getElementById('navDynamicLink')) {
            const li = document.createElement('li');
            li.id = 'navDynamicLink';
            const a = document.createElement('a');
            if (user.role === 'ADMIN') {
                a.href = 'admin-dashboard.html';
                a.textContent = 'Admin Panel';
            } else {
                a.href = 'dashboard.html';
                a.textContent = 'Dashboard';
            }
            li.appendChild(a);
            navLinks.appendChild(li);
        }
    } else {
        // Guest: Show Login and Register buttons
        const loginLink = document.createElement('a');
        loginLink.href = 'login.html';
        loginLink.className = 'btn btn-secondary btn-sm';
        loginLink.textContent = 'Login';

        const registerLink = document.createElement('a');
        registerLink.href = 'register.html';
        registerLink.className = 'btn btn-primary btn-sm';
        registerLink.textContent = 'Register';

        navAuth.appendChild(loginLink);
        navAuth.appendChild(registerLink);
    }
}

// ==========================================================================
// 2. URL Parameters Feedback (Success/Error Alerts via DOM)
// ==========================================================================
function initUrlParamsFeedback() {
    const params = new URLSearchParams(window.location.search);
    const feedbackBox = document.getElementById('feedbackMessage');
    
    if (!feedbackBox) return;

    if (params.has('error')) {
        showAlert(feedbackBox, decodeURIComponent(params.get('error')), 'danger');
    } else if (params.has('success') || params.has('registered') || params.has('added') || params.has('updated') || params.has('deleted') || params.has('cancelled')) {
        let msg = 'Operation completed successfully!';
        if (params.has('registered')) msg = 'Registration successful! Please login with your credentials.';
        if (params.has('added')) msg = 'Vehicle added successfully!';
        if (params.has('updated')) msg = 'Vehicle updated successfully!';
        if (params.has('deleted')) msg = 'Vehicle deleted successfully!';
        if (params.has('cancelled')) msg = 'Booking cancelled successfully.';
        if (params.has('bookingId')) msg = `Booking confirmed! Your Booking Reference ID is #${params.get('bookingId')}`;

        showAlert(feedbackBox, msg, 'success');
    } else if (params.has('loggedOut')) {
        showAlert(feedbackBox, 'You have been logged out safely.', 'info');
    }
}

function showAlert(container, message, type = 'info') {
    if (!container) return;
    container.innerHTML = '';
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    
    const icon = document.createElement('span');
    icon.innerHTML = type === 'success' ? '✓ ' : (type === 'danger' ? '⚠ ' : 'ℹ ');
    icon.style.fontWeight = 'bold';
    
    const textNode = document.createTextNode(message);
    
    alertDiv.appendChild(icon);
    alertDiv.appendChild(textNode);
    container.appendChild(alertDiv);
    container.style.display = 'block';
}

// ==========================================================================
// 3. Dynamic Vehicle Catalogue & JavaScript Filtering (DOM)
// ==========================================================================
function loadVehiclesCatalogue() {
    const grid = document.getElementById('vehiclesGrid');
    if (!grid) return;

    grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 2rem;">Loading vehicle fleet...</div>';

    fetch('vehicles?format=json')
        .then(res => res.json())
        .then(data => {
            allVehicles = data;
            renderVehicleCards(allVehicles);
            setupFilterListeners();
        })
        .catch(err => {
            console.error('Error fetching vehicles:', err);
            grid.innerHTML = '<div class="alert alert-danger" style="grid-column: 1/-1;">Failed to load vehicles from XML.</div>';
        });
}

function renderVehicleCards(vehicles) {
    const grid = document.getElementById('vehiclesGrid');
    if (!grid) return;

    grid.innerHTML = '';

    if (!vehicles || vehicles.length === 0) {
        const noData = document.createElement('div');
        noData.style.gridColumn = '1 / -1';
        noData.className = 'alert alert-info';
        noData.textContent = 'No vehicles match your current search and filter criteria.';
        grid.appendChild(noData);
        return;
    }

    vehicles.forEach(vehicle => {
        const card = document.createElement('div');
        card.className = 'vehicle-card';
        card.setAttribute('data-id', vehicle.id);
        card.setAttribute('data-type', vehicle.type);
        card.setAttribute('data-fuel', vehicle.fuel);
        card.setAttribute('data-price', vehicle.pricePerDay);
        card.setAttribute('data-status', vehicle.status);

        // Visual header
        const visual = document.createElement('div');
        visual.className = 'vehicle-visual';

        // Tags
        const tags = document.createElement('div');
        tags.className = 'vehicle-tags';

        const typeTag = document.createElement('span');
        typeTag.className = 'tag tag-type';
        typeTag.textContent = vehicle.type;

        const statusBadge = document.createElement('span');
        const statusClass = vehicle.status ? vehicle.status.toLowerCase() : 'available';
        statusBadge.className = `badge-status ${statusClass}`;
        statusBadge.textContent = vehicle.status;

        tags.appendChild(typeTag);
        tags.appendChild(statusBadge);

        // Vehicle Icon (SVG)
        const svgIcon = createVehicleSvgIcon(vehicle.type);

        visual.appendChild(tags);
        visual.appendChild(svgIcon);

        // Card Body
        const body = document.createElement('div');
        body.className = 'vehicle-body';

        const title = document.createElement('h3');
        title.className = 'vehicle-title';
        title.textContent = `${vehicle.brand} ${vehicle.name}`;

        const subtitle = document.createElement('p');
        subtitle.className = 'vehicle-subtitle';
        subtitle.textContent = `Model ${vehicle.model} • Reg: ${vehicle.registrationNumber}`;

        const specs = document.createElement('div');
        specs.className = 'vehicle-specs';

        const fuelSpec = document.createElement('div');
        fuelSpec.className = 'spec-item';
        fuelSpec.innerHTML = `<strong>Fuel:</strong> ${vehicle.fuel}`;

        const typeSpec = document.createElement('div');
        typeSpec.className = 'spec-item';
        typeSpec.innerHTML = `<strong>Category:</strong> ${vehicle.type}`;

        specs.appendChild(fuelSpec);
        specs.appendChild(typeSpec);

        // Card Footer
        const footer = document.createElement('div');
        footer.className = 'vehicle-footer';

        const priceTag = document.createElement('div');
        priceTag.className = 'price-tag';

        const priceVal = document.createElement('span');
        priceVal.className = 'price-val';
        priceVal.textContent = `₹${vehicle.pricePerDay}`;

        const priceUnit = document.createElement('span');
        priceUnit.className = 'price-unit';
        priceUnit.textContent = '/ per day';

        priceTag.appendChild(priceVal);
        priceTag.appendChild(priceUnit);

        const actions = document.createElement('div');
        actions.className = 'vehicle-actions';

        const detailsBtn = document.createElement('a');
        detailsBtn.href = `vehicle-details.html?id=${encodeURIComponent(vehicle.id)}`;
        detailsBtn.className = 'btn btn-secondary btn-sm';
        detailsBtn.textContent = 'Details';

        actions.appendChild(detailsBtn);

        if (vehicle.status === 'Available') {
            const rentBtn = document.createElement('a');
            rentBtn.href = `booking.html?vehicleId=${encodeURIComponent(vehicle.id)}`;
            rentBtn.className = 'btn btn-primary btn-sm';
            rentBtn.textContent = 'Rent Now';
            actions.appendChild(rentBtn);
        }

        footer.appendChild(priceTag);
        footer.appendChild(actions);

        body.appendChild(title);
        body.appendChild(subtitle);
        body.appendChild(specs);
        body.appendChild(footer);

        card.appendChild(visual);
        card.appendChild(body);

        grid.appendChild(card);
    });
}

function setupFilterListeners() {
    const searchInput = document.getElementById('searchInput');
    const typeFilter = document.getElementById('typeFilter');
    const fuelFilter = document.getElementById('fuelFilter');
    const statusFilter = document.getElementById('statusFilter');
    const priceRange = document.getElementById('priceRange');
    const priceDisplay = document.getElementById('priceDisplay');
    const resetBtn = document.getElementById('resetFiltersBtn');

    function applyFilters() {
        const query = (searchInput ? searchInput.value : '').toLowerCase().trim();
        const selectedType = typeFilter ? typeFilter.value : 'All';
        const selectedFuel = fuelFilter ? fuelFilter.value : 'All';
        const selectedStatus = statusFilter ? statusFilter.value : 'All';
        const maxPrice = priceRange ? parseFloat(priceRange.value) : 10000;

        if (priceDisplay && priceRange) {
            priceDisplay.textContent = `₹${priceRange.value}/day`;
        }

        const filtered = allVehicles.filter(v => {
            const matchesQuery = !query || 
                v.name.toLowerCase().includes(query) || 
                v.brand.toLowerCase().includes(query) ||
                v.model.toLowerCase().includes(query);

            const matchesType = (selectedType === 'All' || v.type === selectedType);
            const matchesFuel = (selectedFuel === 'All' || v.fuel === selectedFuel);
            const matchesStatus = (selectedStatus === 'All' || v.status === selectedStatus);
            const matchesPrice = (v.pricePerDay <= maxPrice);

            return matchesQuery && matchesType && matchesFuel && matchesStatus && matchesPrice;
        });

        renderVehicleCards(filtered);
    }

    if (searchInput) searchInput.addEventListener('input', applyFilters);
    if (typeFilter) typeFilter.addEventListener('change', applyFilters);
    if (fuelFilter) fuelFilter.addEventListener('change', applyFilters);
    if (statusFilter) statusFilter.addEventListener('change', applyFilters);
    if (priceRange) priceRange.addEventListener('input', applyFilters);

    if (resetBtn) {
        resetBtn.addEventListener('click', () => {
            if (searchInput) searchInput.value = '';
            if (typeFilter) typeFilter.value = 'All';
            if (fuelFilter) fuelFilter.value = 'All';
            if (statusFilter) statusFilter.value = 'All';
            if (priceRange) {
                priceRange.value = priceRange.max || 5000;
                if (priceDisplay) priceDisplay.textContent = `₹${priceRange.value}/day`;
            }
            renderVehicleCards(allVehicles);
        });
    }
}

// ==========================================================================
// 4. Dynamic Rental Cost Calculator (DOM Manipulation)
// ==========================================================================
function initBookingCalculator(vehiclePrice) {
    const pickupInput = document.getElementById('pickupDate');
    const returnInput = document.getElementById('returnDate');
    const rentalDaysSpan = document.getElementById('calcRentalDays');
    const totalAmountSpan = document.getElementById('calcTotalAmount');
    const dateErrorSpan = document.getElementById('dateErrorMsg');
    const submitBtn = document.getElementById('confirmBookingBtn');

    // Default dates: tomorrow and 3 days later
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const minDateStr = tomorrow.toISOString().split('T')[0];
    if (pickupInput) {
        pickupInput.min = minDateStr;
        if (!pickupInput.value) pickupInput.value = minDateStr;
    }

    const defaultReturn = new Date(tomorrow);
    defaultReturn.setDate(defaultReturn.getDate() + 2);
    const returnDateStr = defaultReturn.toISOString().split('T')[0];
    if (returnInput) {
        returnInput.min = minDateStr;
        if (!returnInput.value) returnInput.value = returnDateStr;
    }

    function calculate() {
        if (!pickupInput || !returnInput) return;

        const pDate = new Date(pickupInput.value);
        const rDate = new Date(returnInput.value);

        if (isNaN(pDate.getTime()) || isNaN(rDate.getTime())) {
            return;
        }

        // Validate return >= pickup
        const diffMs = rDate - pDate;
        if (diffMs < 0) {
            if (dateErrorSpan) {
                dateErrorSpan.textContent = 'Return date cannot be earlier than pickup date.';
                dateErrorSpan.style.display = 'block';
            }
            if (submitBtn) submitBtn.disabled = true;
            return;
        } else {
            if (dateErrorSpan) {
                dateErrorSpan.style.display = 'none';
            }
            if (submitBtn) submitBtn.disabled = false;
        }

        let days = Math.round(diffMs / (1000 * 60 * 60 * 24));
        if (days === 0) days = 1; // Same-day rental minimum 1 day

        const total = days * vehiclePrice;

        if (rentalDaysSpan) {
            rentalDaysSpan.textContent = `${days} day${days > 1 ? 's' : ''}`;
        }
        if (totalAmountSpan) {
            totalAmountSpan.textContent = `₹${total}`;
        }
    }

    if (pickupInput) pickupInput.addEventListener('change', calculate);
    if (returnInput) returnInput.addEventListener('change', calculate);

    // Initial calculation on load
    calculate();
}

// ==========================================================================
// 5. Confirmation Modals (Delete & Cancel Handlers via DOM)
// ==========================================================================
function confirmAction(title, message, confirmBtnLabel, onConfirm) {
    let overlay = document.getElementById('customConfirmModal');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'customConfirmModal';
        overlay.className = 'modal-overlay';
        overlay.innerHTML = `
            <div class="modal-card">
                <div class="modal-header">
                    <h3 class="modal-title" id="modalTitle">Confirm Action</h3>
                </div>
                <div class="modal-body" id="modalMessage">Are you sure?</div>
                <div class="modal-footer">
                    <button class="btn btn-secondary btn-sm" id="modalCancelBtn">Cancel</button>
                    <button class="btn btn-danger btn-sm" id="modalConfirmBtn">Confirm</button>
                </div>
            </div>
        `;
        document.body.appendChild(overlay);

        document.getElementById('modalCancelBtn').addEventListener('click', () => {
            overlay.style.display = 'none';
        });
    }

    document.getElementById('modalTitle').textContent = title;
    document.getElementById('modalMessage').textContent = message;
    
    const confirmBtn = document.getElementById('modalConfirmBtn');
    confirmBtn.textContent = confirmBtnLabel || 'Confirm';

    // Remove old listeners by replacing clone
    const newConfirmBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

    newConfirmBtn.addEventListener('click', () => {
        overlay.style.display = 'none';
        if (typeof onConfirm === 'function') onConfirm();
    });

    overlay.style.display = 'flex';
}

function confirmDeleteVehicle(vehicleId, vehicleName) {
    confirmAction(
        'Delete Vehicle',
        `Are you sure you want to permanently delete "${vehicleName}" (${vehicleId}) from the fleet?`,
        'Delete Vehicle',
        () => {
            window.location.href = `admin/delete-vehicle?id=${encodeURIComponent(vehicleId)}`;
        }
    );
}

function confirmCancelBooking(bookingId) {
    confirmAction(
        'Cancel Booking',
        `Are you sure you want to cancel booking reference #${bookingId}? The vehicle will be returned to Available status.`,
        'Cancel Booking',
        () => {
            window.location.href = `cancel-booking?bookingId=${encodeURIComponent(bookingId)}`;
        }
    );
}

// ==========================================================================
// 6. SVG Icon Generator Helper
// ==========================================================================
function createVehicleSvgIcon(type) {
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('viewBox', '0 0 24 24');
    svg.setAttribute('fill', 'currentColor');

    if (type === 'Bike') {
        svg.innerHTML = `
            <path d="M5 20.5a3.5 3.5 0 1 1 3.5-3.5A3.5 3.5 0 0 1 5 20.5zm0-5a1.5 1.5 0 1 0 1.5 1.5A1.5 1.5 0 0 0 5 15.5zm14 5a3.5 3.5 0 1 1 3.5-3.5 3.5 3.5 0 0 1-3.5 3.5zm0-5a1.5 1.5 0 1 0 1.5 1.5 1.5 1.5 0 0 0-1.5-1.5zm-5.78-2l-2.18-4h-2.1l1.58 3h-2.9L6.5 9.4 8 9h3.6a1 1 0 0 1 .9.56l2.12 3.94h2.52l-1.32-3h-1.9V9h2.56a1 1 0 0 1 .92.61L18.84 13.5z"/>
        `;
    } else {
        svg.innerHTML = `
            <path d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z"/>
        `;
    }
    return svg;
}
