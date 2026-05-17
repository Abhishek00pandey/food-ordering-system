import { useContext } from "react";
import { CartContext } from "../context/CartContext";
import API from "../services/api";

function Cart() {
  const { cart, items, clearCart } = useContext(CartContext);

  const placeOrder = async () => {
    try {
      await API.post("/orders", { items }); // ✅ interceptor handles token

      alert("Order placed successfully");
      clearCart();
    } catch (err) {
      console.error(err);
      alert("Order failed");
    }
  };

  return (
    <div className="container">
      <h2>Cart</h2>

      {cart.length === 0 ? (
        <p>Cart is empty</p>
      ) : (
        cart.map((c) => (
          <div className="card" key={c.id}>
            <h3>{c.name}</h3>
            <p>Quantity: {c.quantity}</p>
          </div>
        ))
      )}

      {cart.length > 0 && (
        <button className="button" onClick={placeOrder}>
          Place Order
        </button>
      )}
    </div>
  );
}

export default Cart;