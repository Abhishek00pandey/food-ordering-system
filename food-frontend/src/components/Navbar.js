import { useContext } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { CartContext } from "../context/CartContext";

function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const { itemCount } = useContext(CartContext);
  const token = localStorage.getItem("token");
  const name = localStorage.getItem("name");
  const role = localStorage.getItem("role");
  const isAdmin = role === "ADMIN";

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("name");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    navigate(isAdmin ? "/admin/login" : "/");
  };

  // Hide navbar on auth pages
  if (
    location.pathname === "/" ||
    location.pathname === "/register" ||
    location.pathname === "/admin/login"
  ) {
    return null;
  }

  return (
    <div className="navbar">
      {isAdmin ? (
        <>
          <Link to="/admin">Admin Dashboard</Link>
          <Link to="/admin/restaurants">Restaurants</Link>
          <Link to="/admin/orders">Orders</Link>
          <Link to="/admin/users">Users</Link>
        </>
      ) : (
        <>
          <Link to="/restaurants">Restaurants</Link>
          <Link to="/cart">
            Cart{itemCount > 0 && ` (${itemCount})`}
          </Link>
          <Link to="/orders">Orders</Link>
        </>
      )}

      {token ? (
        <span style={{ marginLeft: "auto" }}>
          <span style={{ marginRight: "10px" }}>Hi, {name}</span>
          <button className="button" onClick={handleLogout}>Logout</button>
        </span>
      ) : (
        <Link to="/" style={{ marginLeft: "auto" }}>Login</Link>
      )}
    </div>
  );
}

export default Navbar;
