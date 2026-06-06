function login() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Login failed");
      return res.json();
    })
    .then((data) => {
      localStorage.setItem("jwt", data.token);
      localStorage.setItem("role", data.role);
      if (data.role === "ADMIN") {
        window.location.href = "dashboard-admin.html";
      } else if (data.role === "DOCTOR") {
        window.location.href = "dashboard-doctor.html";
      } else {
        window.location.href = "dashboard-patient.html";
      }
    })
    .catch((err) => alert(err.message));
}

function register() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;
  const name = document.getElementById("name").value;
  const egn = document.getElementById("egn").value;

  fetch("/api/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password, name, egn }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Registration failed");
      window.location.href = "index.html";
    })
    .catch((err) => alert(err.message));
}

function logout() {
  localStorage.removeItem("jwt");
  localStorage.removeItem("role");
  window.location.href = "index.html";
}

function authHeader() {
  return {
    Authorization: "Bearer " + localStorage.getItem("jwt"),
    "Content-Type": "application/json",
  };
}
