function loadSpecialties(selectId) {
  const select = document.getElementById(selectId);
  if (!select) return;

  fetch("/api/specialties", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load specialties");
      return res.json();
    })
    .then((data) => {
      select.innerHTML = "";
      data.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.textContent = item.name;
        select.appendChild(option);
      });
    })
    .catch((err) => console.error(err.message));
}

function loadDiagnoses(selectId) {
  const select = document.getElementById(selectId);
  if (!select) return;

  fetch("/api/diagnoses", {
    method: "GET",
    headers: authHeader(),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to load diagnoses");
      return res.json();
    })
    .then((data) => {
      select.innerHTML = "";
      data.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.textContent = item.name;
        select.appendChild(option);
      });
    })
    .catch((err) => console.error(err.message));
}
