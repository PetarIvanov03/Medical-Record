function loadMyProfile() {
  fetch("/api/patients/my-profile", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load profile");
      return res.json();
    })
    .then((data) => {
      document.getElementById("profile-name").textContent = data.name || "";
      document.getElementById("profile-egn").textContent = data.ejn || data.ezn || data.egn || "";
      document.getElementById("profile-isInsured").textContent = data.isInsured ? "Yes" : "No";
      document.getElementById("profile-gpName").textContent = data.gpName || "Not assigned";
    })
    .catch((err) => {
      console.error(err);
      alert(err.message);
    });
}

function loadMyHistory() {
  fetch("/api/patients/my-history", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load history");
      return res.json();
    })
    .then((data) => {
      const tbody = document.getElementById("history-tbody");
      tbody.innerHTML = "";
      if (!data.examinations || data.examinations.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No examinations found.</td></tr>';
        return;
      }
      data.examinations.forEach((item) => {
        const tr = document.createElement("tr");
        tr.innerHTML =
          "<td>" + (item.examDate || "") + "</td>" +
          "<td>" + (item.doctorName || "") + "</td>" +
          "<td>" + (item.diagnosisName || "") + "</td>" +
          "<td>" + (item.price != null ? item.price : "") + "</td>" +
          "<td>" + (item.paidByNzok ? "Yes" : "No") + "</td>";
        tbody.appendChild(tr);
      });
    })
    .catch((err) => {
      console.error(err);
      alert(err.message);
    });
}

function loadDoctorsDropdown() {
  fetch("/api/doctors", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load doctors");
      return res.json();
    })
    .then((data) => {
      const select = document.getElementById("gp-select");
      select.innerHTML = '<option value="">-- Select a doctor --</option>';
      const gps = (data || []).filter((doctor) => doctor.isGp);
      gps.forEach((doctor) => {
        const option = document.createElement("option");
        option.value = doctor.id;
        option.textContent = doctor.name || "Doctor";
        select.appendChild(option);
      });
    })
    .catch((err) => {
      console.error(err);
      alert(err.message);
    });
}

function changeGp() {
  const gpId = document.getElementById("gp-select").value;
  if (!gpId) {
    alert("Please select a doctor.");
    return;
  }
  fetch("/api/patients/my-profile/change-gp", {
    method: "PUT",
    headers: authHeader(),
    body: JSON.stringify({ gpId: Number(gpId) }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to change GP");
    })
    .then(() => {
      alert("General practitioner updated successfully.");
      loadMyProfile();
    })
    .catch((err) => {
      console.error(err);
      alert(err.message);
    });
}

window.addEventListener("DOMContentLoaded", () => {
  loadMyProfile();
  loadMyHistory();
  loadDoctorsDropdown();
});
