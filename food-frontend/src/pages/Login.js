import { useNavigate, Link } from "react-router-dom";
import { useState } from "react";
import API from "../services/api";

function Login() {
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
      if (role !== "USER") {
        alert("This is the customer login. Use the admin login page for admin accounts.");
        return;
      }
      localStorage.setItem("token", token);
      localStorage.setItem("name", name);
      localStorage.setItem("email", userEmail);
      localStorage.setItem("role", role);
      navigate("/restaurants");
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
    <div className="auth-wrapper">
      <form className="auth-card" onSubmit={handleLogin}>
        <h2>Welcome back</h2>
        <p className="auth-subtitle">Sign in to order delicious food</p>

        <div className="form-group">
          <label className="form-label">Email</label>
          <input
            className="form-input"
            placeholder="you@example.com"
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
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </div>

        <button
          type="submit"
          className="button button-block"
          disabled={submitting}
        >
          {submitting ? "Signing in..." : "Login"}
        </button>

        <p className="auth-footer">
          New here? <Link to="/register">Create an account</Link>
        </p>

        <div className="auth-divider">
          <span>or</span>
        </div>

        <Link to="/admin/login" className="button button-block button-secondary">
          Login as Admin
        </Link>
      </form>
    </div>
  );
}

export default Login;
