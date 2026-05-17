import { useEffect, useState } from "react";
import API from "../services/api";

const STATUS_COLORS = {
  PLACED: "#3498db",
  CONFIRMED: "#2980b9",
  PREPARING: "#f39c12",
  OUT_FOR_DELIVERY: "#9b59b6",
  DELIVERED: "#27ae60",
  CANCELLED: "#7f8c8d",
};

function StatusBadge({ status }) {
  return (
    <span
      style={{
        background: STATUS_COLORS[status] || "#777",
        color: "white",
        padding: "4px 10px",
        borderRadius: "12px",
        fontSize: "12px",
        fontWeight: "bold",
      }}
    >
      {status}
    </span>
  );
}

function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    API.get("/orders/my")
      .then((res) => setOrders(res.data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const cancelOrder = async (id) => {
    if (!window.confirm("Cancel this order?")) return;
    try {
      await API.put(`/orders/${id}/cancel`);
      load();
    } catch (err) {
      alert(err.response?.data?.message || "Could not cancel");
    }
  };

  if (loading) return <div className="container"><p>Loading...</p></div>;

  return (
    <div className="container">
      <h2>My Orders</h2>

      {orders.length === 0 ? (
        <p>No orders yet</p>
      ) : (
        orders.map((o) => (
          <div className="card" key={o.id}>
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                marginBottom: "10px",
              }}
            >
              <h3 style={{ margin: 0 }}>Order #{o.id}</h3>
              <StatusBadge status={o.status} />
            </div>

            {o.createdAt && (
              <p style={{ color: "#777", margin: "4px 0" }}>
                {new Date(o.createdAt).toLocaleString()}
              </p>
            )}

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
              <p style={{ margin: "4px 0", fontSize: "13px", color: "#555" }}>
                Deliver to: {o.deliveryAddress}
                {o.phone && ` · ${o.phone}`}
              </p>
            )}

            <p style={{ margin: "8px 0" }}>
              <b>Total: ₹{o.totalAmount?.toFixed(2)}</b>
            </p>

            {o.status === "PLACED" && (
              <button
                className="button"
                style={{ background: "#c0392b" }}
                onClick={() => cancelOrder(o.id)}
              >
                Cancel Order
              </button>
            )}
          </div>
        ))
      )}
    </div>
  );
}

export default Orders;
