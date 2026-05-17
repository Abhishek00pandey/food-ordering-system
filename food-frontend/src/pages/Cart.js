import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CartContext } from "../context/CartContext";
import API from "../services/api";

function Cart() {
  const {
    cart,
    items,
    total,
    increaseQty,
    decreaseQty,
    removeFromCart,
    clearCart,
  } = useContext(CartContext);
  const navigate = useNavigate();

  const [deliveryAddress, setDeliveryAddress] = useState("");
  const [phone, setPhone] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const placeOrder = async () => {
    if (cart.length === 0) return;
    if (!deliveryAddress.trim() || !phone.trim()) {
      alert("Please enter delivery address and phone number");
      return;
    }
    setSubmitting(true);
    try {
      await API.post("/orders", { items, deliveryAddress, phone });
      alert("Order placed successfully");
      clearCart();
      navigate("/orders");
    } catch (err) {
      const message = err.response?.data?.message || "Order failed";
      alert(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container">
      <h2>Your Cart</h2>

      {cart.length === 0 ? (
        <div className="card" style={{ textAlign: "center" }}>
          <p>Your cart is empty</p>
          <button
            className="button"
            onClick={() => navigate("/restaurants")}
          >
            Browse Restaurants
          </button>
        </div>
      ) : (
        <>
          {cart.map((c) => {
            const lineTotal = (c.price || 0) * c.quantity;
            return (
              <div
                className="card"
                key={c.id}
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  gap: "12px",
                }}
              >
                <div style={{ flex: 1 }}>
                  <h3 style={{ margin: 0 }}>{c.name}</h3>
                  <p style={{ margin: "4px 0", color: "#555" }}>
                    ₹{c.price} each
                  </p>
                </div>

                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                  <button className="button" onClick={() => decreaseQty(c.id)}>−</button>
                  <span style={{ minWidth: "24px", textAlign: "center" }}>{c.quantity}</span>
                  <button className="button" onClick={() => increaseQty(c.id)}>+</button>
                </div>

                <div style={{ minWidth: "80px", textAlign: "right" }}>
                  <b>₹{lineTotal.toFixed(2)}</b>
                </div>

                <button
                  className="button"
                  style={{ background: "#c0392b" }}
                  onClick={() => removeFromCart(c.id)}
                >
                  Remove
                </button>
              </div>
            );
          })}

          <div
            className="card"
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginTop: "20px",
            }}
          >
            <h3 style={{ margin: 0 }}>Grand Total</h3>
            <h3 style={{ margin: 0 }}>₹{total.toFixed(2)}</h3>
          </div>

          <div className="card" style={{ marginTop: "20px" }}>
            <h3 style={{ marginTop: 0 }}>Delivery Details</h3>
            <input
              placeholder="Delivery address"
              value={deliveryAddress}
              onChange={(e) => setDeliveryAddress(e.target.value)}
              style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
            />
            <input
              placeholder="Phone number"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              style={{ width: "100%", padding: "10px" }}
            />
          </div>

          <div style={{ marginTop: "16px", display: "flex", gap: "10px" }}>
            <button
              className="button"
              onClick={placeOrder}
              disabled={submitting}
            >
              {submitting ? "Placing..." : "Place Order"}
            </button>
            <button
              className="button"
              style={{ background: "#999" }}
              onClick={clearCart}
            >
              Clear Cart
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default Cart;
