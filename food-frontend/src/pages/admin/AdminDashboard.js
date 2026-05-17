import { Link } from "react-router-dom";

function AdminDashboard() {
  return (
    <div className="container">
      <h2>Admin Dashboard</h2>
      <div className="grid">
        <div className="card">
          <h3>Restaurants</h3>
          <p>Add, edit, or delete restaurants.</p>
          <Link className="button" to="/admin/restaurants">Manage</Link>
        </div>
        <div className="card">
          <h3>Orders</h3>
          <p>View all orders and update status.</p>
          <Link className="button" to="/admin/orders">Manage</Link>
        </div>
        <div className="card">
          <h3>Users</h3>
          <p>View registered users.</p>
          <Link className="button" to="/admin/users">View</Link>
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;
