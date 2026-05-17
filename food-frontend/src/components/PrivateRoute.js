import { Navigate } from "react-router-dom";

function PrivateRoute({ children, adminOnly = false }) {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (!token) {
    return <Navigate to={adminOnly ? "/admin/login" : "/"} replace />;
  }
  if (adminOnly && role !== "ADMIN") {
    return <Navigate to="/restaurants" replace />;
  }
  return children;
}

export default PrivateRoute;
