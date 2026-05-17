import { useNavigate, Link } from "react-router-dom";
import { useState } from "react";
import API from "../services/api";

function AdminLogin() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleLogin = async (e) => {
    e?.preventDefault();
    setSubmitting(true);
    try {
      const res = await API.post("/auth/login", { email, password });
      const { token, name, email: userEmail, role } = res.data;
      if (role !== "ADMIN") {
        alert("This account is not an admin. Use the customer login page.");
        return;
      }
      localStorage.setItem("token", token);
      localStorage.setItem("name", name);
      localStorage.setItem("email", userEmail);
      localStorage.setItem("role", role);
      navigate("/admin");
    } catch (err) {
      console.error("Login error:", err);
      const message =
        err.response?.data?.message ||
        (err.message === "Network Error"
          ? "Cannot reach server — is the backend running?"
          : "Login failed");
      alert(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-wrapper auth-wrapper-admin">
      <form className="auth-card auth-card-admin" onSubmit={handleLogin}>
        <div className="admin-badge">ADMIN</div>
        <h2>Admin Console</h2>
        <p className="auth-subtitle">Restricted to administrators</p>

        <div className="form-group">
          <label className="form-label">Email</label>
          <input
            className="form-input"
            placeholder="admin@food.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
          />
        </div>

        <div className="form-group">
          <label className="form-label">Password</label>
          <input
            className="form-input"
            type="password"
            placeholder="Admin password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </div>

        <button
          type="submit"
          className="button button-block button-dark"
          disabled={submitting}
        >
          {submitting ? "Signing in..." : "Sign in to Admin"}
        </button>

        <p className="auth-footer">
          Not an admin? <Link to="/">Customer login</Link>
        </p>
      </form>
    </div>
  );
}

export default AdminLogin;
