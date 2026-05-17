import { useEffect, useState } from "react";
import API from "../../services/api";

const STATUSES = [
  "PLACED",
  "CONFIRMED",
  "PREPARING",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
  "CANCELLED",
];

const STATUS_COLORS = {
  PLACED: "#3498db",
  CONFIRMED: "#2980b9",
  PREPARING: "#f39c12",
  OUT_FOR_DELIVERY: "#9b59b6",
  DELIVERED: "#27ae60",
  CANCELLED: "#7f8c8d",
};

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [filter, setFilter] = useState("");

  const load = () => {
    const url = filter ? `/admin/orders?status=${filter}` : "/admin/orders";
    API.get(url)
      .then((res) => setOrders(res.data))
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filter]);

  const changeStatus = async (id, status) => {
    try {
      await API.put(`/orders/${id}/status`, { status });
      load();
    } catch (err) {
      alert(err.response?.data?.message || "Status update failed");
    }
  };

  return (
    <div className="container">
      <h2>All Orders</h2>

      <div style={{ marginBottom: "16px" }}>
        <label style={{ marginRight: "8px" }}>Filter:</label>
        <select
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          style={{ padding: "6px" }}
        >
          <option value="">All</option>
          {STATUSES.map((s) => (
            <option key={s} value={s}>
              {s}
            </option>
          ))}
        </select>
      </div>

      {orders.length === 0 ? (
        <p>No orders found</p>
      ) : (
        orders.map((o) => (
          <div className="card" key={o.id}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <h3 style={{ margin: 0 }}>Order #{o.id}</h3>
              <span
                style={{
                  background: STATUS_COLORS[o.status] || "#777",
                  color: "white",
                  padding: "4px 10px",
                  borderRadius: "12px",
                  fontSize: "12px",
                  fontWeight: "bold",
                }}
              >
                {o.status}
              </span>
            </div>

            <p style={{ color: "#777", margin: "4px 0" }}>
              {o.userName} ({o.userEmail}) ·{" "}
              {o.createdAt && new Date(o.createdAt).toLocaleString()}
            </p>

            {o.items && o.items.length > 0 && (
              <ul style={{ paddingLeft: "20px", margin: "8px 0" }}>
                {o.items.map((it) => (
                  <li key={it.id}>
                    {it.foodName} × {it.quantity} — ₹{it.lineTotal?.toFixed(2)}
                  </li>
                ))}
              </ul>
            )}

            {o.deliveryAddress && (
              <p style={{ fontSize: "13px", color: "#555" }}>
                Deliver to: {o.deliveryAddress}
                {o.phone && ` · ${o.phone}`}
              </p>
            )}

            <p>
              <b>Total: ₹{o.totalAmount?.toFixed(2)}</b>
            </p>

            <label style={{ marginRight: "8px" }}>Change status:</label>
            <select
              value={o.status}
              onChange={(e) => changeStatus(o.id, e.target.value)}
              style={{ padding: "6px" }}
            >
              {STATUSES.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </div>
        ))
      )}
    </div>
  );
}

export default AdminOrders;
