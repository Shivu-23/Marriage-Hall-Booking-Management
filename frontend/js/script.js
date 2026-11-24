const API = 'http://localhost:8080/api/v1/hall-bookings';
let bookings = [];
let modal;

document.addEventListener('DOMContentLoaded', () => {
  modal = new bootstrap.Modal(document.getElementById('bookingModal'));
  document.getElementById('eventDate').min = tomorrowISO();
  document.getElementById('reportStart').value = todayISO();
  document.getElementById('reportEnd').value = todayISO();
  loadBookings();

  document.getElementById('newBookingBtn').addEventListener('click', openNewBooking);
  document.getElementById('refreshBtn').addEventListener('click', loadBookings);
  document.getElementById('allBookingsBtn').addEventListener('click', () => {
    document.getElementById('reportMessage').textContent = 'Showing all bookings.';
    loadBookings();
  });
  document.getElementById('bookingForm').addEventListener('submit', saveBooking);
  document.getElementById('searchInput').addEventListener('input', renderTable);
  document.getElementById('statusFilter').addEventListener('change', renderTable);
  document.getElementById('dateReportBtn').addEventListener('click', dateReport);
  document.getElementById('capacityReportBtn').addEventListener('click', capacityReport);
  document.getElementById('statusReportBtn').addEventListener('click', statusReport);
});

function todayISO() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
}
function tomorrowISO() {
  const d = new Date(); d.setDate(d.getDate()+1);
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
}
function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
}
function showAlert(message, type='success') {
  document.getElementById('alertArea').innerHTML =
    `<div class="alert alert-${type} alert-dismissible fade show" role="alert">${escapeHtml(message)}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>`;
}
async function apiRequest(url, options={}) {
  const response = await fetch(url, { ...options, headers: { 'Content-Type':'application/json', ...(options.headers || {}) }});
  if (response.status === 204) return null;
  const text = await response.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = text; }
  if (!response.ok) {
    let message = data?.message || data?.error || 'Request failed';
    if (data?.messages) message = Object.values(data.messages).join('; ');
    throw new Error(message);
  }
  return data;
}
async function loadBookings() {
  try { bookings = await apiRequest(API); renderTable(); updateStats(); }
  catch (error) { showAlert(error.message + ' — Check that the Spring Boot backend and MySQL are running.', 'danger'); }
}
function updateStats() {
  document.getElementById('totalCount').textContent = bookings.length;
  document.getElementById('confirmedCount').textContent = bookings.filter(b=>b.bookingStatus==='CONFIRMED').length;
  document.getElementById('pendingCount').textContent = bookings.filter(b=>b.bookingStatus==='PENDING').length;
  document.getElementById('cancelledCount').textContent = bookings.filter(b=>b.bookingStatus==='CANCELLED').length;
}
function renderTable() {
  const query = document.getElementById('searchInput').value.toLowerCase().trim();
  const status = document.getElementById('statusFilter').value;
  const rows = bookings.filter(b =>
    (!status || b.bookingStatus === status) &&
    (`${b.customerName} ${b.hallName} ${b.bookingId}`.toLowerCase().includes(query))
  );
  document.getElementById('recordCount').textContent = `${rows.length} record(s)`;
  const tbody = document.getElementById('bookingTable');
  if (!rows.length) {
    tbody.innerHTML = '<tr><td colspan="8" class="text-center text-secondary py-4">No bookings found.</td></tr>';
    return;
  }
  tbody.innerHTML = rows.map(b => `
    <tr>
      <td>#${b.bookingId}</td><td class="fw-semibold">${escapeHtml(b.customerName)}</td>
      <td>${escapeHtml(b.hallName)}</td><td>${escapeHtml(b.eventDate)}</td>
      <td>${b.hallCapacity}</td><td>₹${Number(b.rentAmount).toLocaleString('en-IN')}</td>
      <td><span class="badge-status status-${b.bookingStatus}">${b.bookingStatus}</span></td>
      <td>
        <button class="action-btn text-primary" onclick="editBooking(${b.bookingId})">Edit</button>
        ${b.bookingStatus !== 'CANCELLED' ? `<button class="action-btn text-danger" onclick="cancelBooking(${b.bookingId})">Cancel</button>` : ''}
      </td>
    </tr>`).join('');
}
function openNewBooking() {
  document.getElementById('bookingForm').reset();
  document.getElementById('bookingId').value = '';
  document.getElementById('eventDate').min = tomorrowISO();
  document.getElementById('modalTitle').textContent = 'Create booking';
  document.getElementById('saveBtn').textContent = 'Save booking';
  modal.show();
}
async function editBooking(id) {
  try {
    const b = await apiRequest(`${API}/${id}`);
    document.getElementById('bookingId').value = b.bookingId;
    document.getElementById('customerName').value = b.customerName;
    document.getElementById('hallName').value = b.hallName;
    document.getElementById('eventDate').value = b.eventDate;
    document.getElementById('eventDate').min = tomorrowISO();
    document.getElementById('hallCapacity').value = b.hallCapacity;
    document.getElementById('rentAmount').value = b.rentAmount;
    document.getElementById('bookingStatus').value = b.bookingStatus;
    document.getElementById('modalTitle').textContent = `Edit booking #${id}`;
    document.getElementById('saveBtn').textContent = 'Update booking';
    modal.show();
  } catch (error) { showAlert(error.message, 'danger'); }
}
async function saveBooking(event) {
  event.preventDefault();
  const id = document.getElementById('bookingId').value;
  const payload = {
    customerName: document.getElementById('customerName').value.trim(),
    hallName: document.getElementById('hallName').value.trim(),
    eventDate: document.getElementById('eventDate').value,
    hallCapacity: Number(document.getElementById('hallCapacity').value),
    rentAmount: Number(document.getElementById('rentAmount').value),
    bookingStatus: document.getElementById('bookingStatus').value
  };
  if (payload.hallCapacity <= 50 || payload.rentAmount <= 0 || payload.eventDate <= todayISO()) {
    showAlert('Enter a future event date, capacity above 50 and rent greater than zero.', 'warning'); return;
  }
  try {
    await apiRequest(id ? `${API}/${id}` : API, {
      method: id ? 'PUT' : 'POST', body: JSON.stringify(payload)
    });
    modal.hide(); showAlert(id ? 'Booking updated successfully.' : 'Booking created successfully.');
    document.getElementById('statusFilter').value = '';
    await loadBookings();
  } catch (error) { showAlert(error.message, 'danger'); }
}
async function cancelBooking(id) {
  if (!confirm(`Cancel booking #${id}?`)) return;
  try {
    await apiRequest(`${API}/${id}`, { method:'DELETE' });
    showAlert(`Booking #${id} cancelled.`); await loadBookings();
  } catch (error) { showAlert(error.message, 'danger'); }
}
async function applyReport(url, label) {
  try {
    bookings = await apiRequest(url);
    document.getElementById('statusFilter').value = '';
    document.getElementById('searchInput').value = '';
    renderTable(); updateStats();
    document.getElementById('reportMessage').textContent = `${label}: ${bookings.length} result(s).`;
  } catch (error) { showAlert(error.message, 'danger'); }
}
function dateReport() {
  const start = document.getElementById('reportStart').value;
  const end = document.getElementById('reportEnd').value;
  if (!start || !end) return showAlert('Select both start and end dates.', 'warning');
  applyReport(`${API}/reports/dates?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`, 'Date report');
}
function capacityReport() {
  const min = Number(document.getElementById('minCapacity').value);
  if (!min || min <= 50) return showAlert('Minimum capacity must be greater than 50.', 'warning');
  applyReport(`${API}/reports/capacity?minCapacity=${min}`, 'Capacity report');
}
function statusReport() {
  const status = document.getElementById('reportStatus').value;
  applyReport(`${API}/reports/status?status=${encodeURIComponent(status)}`, 'Status report');
}
