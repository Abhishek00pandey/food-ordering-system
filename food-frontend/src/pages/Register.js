import { useNavigate, Link } from "react-router-dom";
import { useState } from "react";
import API from "../services/api";

function Register() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleRegister = async (e) => {
    e?.preventDefault();
    if (!name || !email || !password) {
      alert("Please fill in all fields");
      return;
    }
    setSubmitting(true);
    try {
      const res = await API.post("/auth/register", { name, email, password });
      const { token, name: userName, email: userEmail, role } = res.data;
      localStorage.setItem("token", token);
      localStorage.setItem("name", userName);
      localStorage.setItem("email", userEmail);
      localStorage.setItem("role", role);
      navigate("/restaurants");
    } catch (err) {
      console.error("Register error:", err);
      const message =
        err.response?.data?.message ||
        (err.message === "Network Error"
          ? "Cannot reach server — is the backend running?"
          : "Registration failed");
      alert(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <form className="auth-card" onSubmit={handleRegister}>
        <h2>Create your account</h2>
        <p className="auth-subtitle">Order from your favourite restaurants</p>

        <div className="form-group">
          <label className="form-label">Full name</label>
          <input
            className="form-input"
            placeholder="Jane Doe"
            value={name}
            onChange={(e) => setName(e.target.value)}
            autoComplete="name"
          />
        </div>

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
            placeholder="Choose a strong password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="new-password"
          />
        </div>

        <button
          type="submit"
          className="button button-block"
          disabled={submitting}
        >
          {submitting ? "Creating account..." : "Register"}
        </button>

        <p className="auth-footer">
          Already have an account? <Link to="/">Login</Link>
        </p>
      </form>
    </div>
  );
}

export default Register;
