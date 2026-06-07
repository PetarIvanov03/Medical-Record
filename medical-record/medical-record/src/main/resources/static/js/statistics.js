function parseJsonSafe(res) {
  return res.text().then((text) => {
    if (!text || !text.trim()) return null;
    return JSON.parse(text);
  });
}

function loadMostCommonDiagnosis(targetId) {
  fetch("/api/statistics/most-common-diagnosis", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load most common diagnosis");
      return parseJsonSafe(res);
    })
    .then((data) => {
      const el = document.getElementById(targetId);
      if (!el) return;
      if (data) {
        el.innerHTML = `
          <strong>${data.name}</strong><br>
          <small class="text-muted">Code: ${data.code}</small><br>
          <span class="badge bg-primary fs-6">Count: ${data.count}</span>
        `;
      } else {
        el.textContent = "No data available";
      }
    })
    .catch((err) => {
      const el = document.getElementById(targetId);
      if (el) el.textContent = "Error: " + err.message;
    });
}

function loadGpPatientCounts(targetId) {
  fetch("/api/statistics/gp-patient-counts", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load GP patient counts");
      return parseJsonSafe(res);
    })
    .then((data) => {
      const el = document.getElementById(targetId);
      if (!el) return;
      if (!data || data.length === 0) {
        el.innerHTML = "<p class=\"text-muted\">No data available</p>";
        return;
      }
      let html = `
        <table class="table table-sm table-bordered mb-0">
          <thead class="table-light">
            <tr>
              <th>GP Name</th>
              <th>Patient Count</th>
            </tr>
          </thead>
          <tbody>
      `;
      data.forEach((row) => {
        html += `<tr><td>${row.label}</td><td>${row.count}</td></tr>`;
      });
      html += "</tbody></table>";
      el.innerHTML = html;
    })
    .catch((err) => {
      const el = document.getElementById(targetId);
      if (el) el.textContent = "Error: " + err.message;
    });
}

function loadDoctorVisitCounts(targetId) {
  fetch("/api/statistics/doctor-visit-counts", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load doctor visit counts");
      return parseJsonSafe(res);
    })
    .then((data) => {
      const el = document.getElementById(targetId);
      if (!el) return;
      if (!data || data.length === 0) {
        el.innerHTML = "<p class=\"text-muted\">No data available</p>";
        return;
      }
      let html = `
        <table class="table table-sm table-bordered mb-0">
          <thead class="table-light">
            <tr>
              <th>Doctor</th>
              <th>Visit Count</th>
            </tr>
          </thead>
          <tbody>
      `;
      data.forEach((row) => {
        html += `<tr><td>${row.label}</td><td>${row.count}</td></tr>`;
      });
      html += "</tbody></table>";
      el.innerHTML = html;
    })
    .catch((err) => {
      const el = document.getElementById(targetId);
      if (el) el.textContent = "Error: " + err.message;
    });
}

function loadTotalRevenue(targetId) {
  fetch("/api/statistics/total-revenue", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load total revenue");
      return parseJsonSafe(res);
    })
    .then((data) => {
      const el = document.getElementById(targetId);
      if (!el) return;
      if (data != null) {
        el.innerHTML = `<span class="fs-3 fw-bold text-success">${data}</span> <span class="text-muted">BGN</span>`;
      } else {
        el.textContent = "No data available";
      }
    })
    .catch((err) => {
      const el = document.getElementById(targetId);
      if (el) el.textContent = "Error: " + err.message;
    });
}

function loadRevenueByDoctor(targetId) {
  fetch("/api/statistics/revenue-by-doctor", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load revenue by doctor");
      return parseJsonSafe(res);
    })
    .then((data) => {
      const el = document.getElementById(targetId);
      if (!el) return;
      if (!data || data.length === 0) {
        el.innerHTML = "<p class=\"text-muted\">No data available</p>";
        return;
      }
      let html = `
        <table class="table table-sm table-bordered mb-0">
          <thead class="table-light">
            <tr>
              <th>Doctor</th>
              <th>Revenue (BGN)</th>
            </tr>
          </thead>
          <tbody>
      `;
      data.forEach((row) => {
        html += `<tr><td>${row.label}</td><td>${row.revenue}</td></tr>`;
      });
      html += "</tbody></table>";
      el.innerHTML = html;
    })
    .catch((err) => {
      const el = document.getElementById(targetId);
      if (el) el.textContent = "Error: " + err.message;
    });
}

function loadPeakSickLeaveMonth(targetId) {
  fetch("/api/statistics/peak-sick-leave-month", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load peak sick leave month");
      return parseJsonSafe(res);
    })
    .then((data) => {
      const el = document.getElementById(targetId);
      if (!el) return;
      if (data != null) {
        const monthNames = ["January","February","March","April","May","June",
                            "July","August","September","October","November","December"];
        const monthName = monthNames[data - 1] || `Month ${data}`;
        el.innerHTML = `<span class="badge bg-danger fs-6">${monthName}</span>`;
      } else {
        el.textContent = "No data available";
      }
    })
    .catch((err) => {
      const el = document.getElementById(targetId);
      if (el) el.textContent = "Error: " + err.message;
    });
}

function loadDoctorsWithMostSickLeaves(targetId) {
  fetch("/api/statistics/doctors-with-most-sick-leaves", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load doctors with most sick leaves");
      return parseJsonSafe(res);
    })
    .then((data) => {
      const el = document.getElementById(targetId);
      if (!el) return;
      if (!data || data.length === 0) {
        el.innerHTML = "<p class=\"text-muted\">No data available</p>";
        return;
      }
      let html = `
        <table class="table table-sm table-bordered mb-0">
          <thead class="table-light">
            <tr>
              <th>Doctor</th>
              <th>Sick Leave Count</th>
            </tr>
          </thead>
          <tbody>
      `;
      data.forEach((row) => {
        html += `<tr><td>${row.label}</td><td>${row.count}</td></tr>`;
      });
      html += "</tbody></table>";
      el.innerHTML = html;
    })
    .catch((err) => {
      const el = document.getElementById(targetId);
      if (el) el.textContent = "Error: " + err.message;
    });
}

function loadAllStatistics() {
  loadMostCommonDiagnosis("stat-most-common-diagnosis");
  loadGpPatientCounts("stat-gp-patient-counts");
  loadDoctorVisitCounts("stat-doctor-visit-counts");
  loadTotalRevenue("stat-total-revenue");
  loadRevenueByDoctor("stat-revenue-by-doctor");
  loadPeakSickLeaveMonth("stat-peak-sick-leave-month");
  loadDoctorsWithMostSickLeaves("stat-doctors-with-most-sick-leaves");
}
