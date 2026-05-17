import { createContext, useState } from "react";

export const CartContext = createContext();

export function CartProvider({ children }) {
  const [cart, setCart] = useState([]);

  const addToCart = (item) => {
    setCart((prevCart) => {
      const existing = prevCart.find((c) => c.id === item.id);

      if (existing) {
        return prevCart.map((c) =>
          c.id === item.id
            ? { ...c, quantity: c.quantity + 1 }
            : c
        );
      } else {
        return [...prevCart, { ...item, quantity: 1 }];
      }
    });
  };

  const removeFromCart = (id) => {
    setCart((prevCart) => prevCart.filter((c) => c.id !== id));
  };

  const clearCart = () => {
    setCart([]);
  };

  // 👉 This is important for backend order API
  const items = cart.map((c) => ({
    foodId: c.id,
    quantity: c.quantity,
  }));

  return (
    <CartContext.Provider
      value={{
        cart,
        addToCart,
        removeFromCart,
        clearCart,
        items, // ✅ now exposed
      }}
    >
      {children}
    </CartContext.Provider>
  );
}