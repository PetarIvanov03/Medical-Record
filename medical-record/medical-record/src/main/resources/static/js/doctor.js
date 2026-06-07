function populateExaminationDropdown() {
  fetch("/api/examinations/my-history", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load examinations");
      return res.json();
    })
    .then((data) => {
      const select = document.getElementById("examinationId");
      if (!select) return;
      select.innerHTML = "";
      const blank = document.createElement("option");
      blank.value = "";
      blank.textContent = "-- Select Examination --";
      select.appendChild(blank);
      data.forEach((exam) => {
        const option = document.createElement("option");
        option.value = exam.id;
        option.textContent = `#${exam.id} — ${exam.examDate} — ${exam.patientName} — ${exam.diagnosisName}`;
        select.appendChild(option);
      });
    })
    .catch((err) => alert(err.message));
}

function populatePatientDropdown(patients) {
  const select = document.getElementById("patientId");
  if (!select) return;
  select.innerHTML = "";
  const blank = document.createElement("option");
  blank.value = "";
  blank.textContent = "-- Select Patient --";
  select.appendChild(blank);
  patients.forEach((patient) => {
    const option = document.createElement("option");
    option.value = patient.id;
    option.textContent = `#${patient.id} - ${patient.name} (EGN: ${patient.egn})`;
    select.appendChild(option);
  });
}

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
      populatePatientDropdown(data);
    })
    .catch((err) => alert(err.message));
}

document.addEventListener("DOMContentLoaded", () => {
  loadMyExaminations();
  loadAllPatients();
  loadMySpecialtyDiagnoses("diagnosisId");
  populateExaminationDropdown();
});
