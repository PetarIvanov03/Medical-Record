function loadAllPatients() {
  fetch("/api/patients", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load patients");
      return res.json();
    })
    .then((data) => {
      const tbody = document.getElementById("patientsTableBody");
      tbody.innerHTML = "";
      data.forEach((patient) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${patient.name}</td>
          <td>${patient.egn}</td>
          <td>${patient.gpName}</td>
        `;
        tbody.appendChild(tr);
      });
    })
    .catch((err) => alert(err.message));
}

document.addEventListener("DOMContentLoaded", () => {
  loadMyExaminations();
  loadAllPatients();
});
