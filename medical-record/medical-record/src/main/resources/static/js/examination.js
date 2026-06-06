function loadMyExaminations() {
  fetch("/api/examinations/my-history", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load examinations");
      return res.json();
    })
    .then((data) => {
      const tbody = document.getElementById("examinationsTableBody");
      tbody.innerHTML = "";
      data.forEach((exam) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${exam.examDate}</td>
          <td>${exam.patientName}</td>
          <td>${exam.diagnosisName}</td>
          <td>${exam.price}</td>
          <td>${exam.paidByNzok ? 'Yes' : 'No'}</td>
        `;
        tbody.appendChild(tr);
      });
    })
    .catch((err) => alert(err.message));
}

function createExamination(data) {
  fetch("/api/examinations", {
    method: "POST",
    headers: authHeader(),
    body: JSON.stringify(data),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to create examination");
      return res.json();
    })
    .then(() => {
      alert("Examination created successfully");
      loadMyExaminations();
    })
    .catch((err) => alert(err.message));
}

function createSickLeave(data) {
  fetch("/api/sick-leaves", {
    method: "POST",
    headers: authHeader(),
    body: JSON.stringify(data),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to create sick leave");
      return res.json();
    })
    .then(() => {
      alert("Sick leave created successfully");
    })
    .catch((err) => alert(err.message));
}
