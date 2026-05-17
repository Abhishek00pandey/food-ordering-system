import { useEffect, useState } from "react";
import API from "../services/api";

function Orders() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    API.get("/orders/user/1") // we'll fix this later with JWT
      .then((res) => setOrders(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <div className="container">
      <h2>My Orders</h2>

      {orders.length === 0 ? (
        <p>No orders yet</p>
      ) : (
        orders.map((o) => (
          <div className="card" key={o.id}>
            <p><b>Total:</b> ₹{o.totalAmount}</p>
            <p><b>Status:</b> {o.status}</p>
          </div>
        ))
      )}
    </div>
  );
}

export default Orders;