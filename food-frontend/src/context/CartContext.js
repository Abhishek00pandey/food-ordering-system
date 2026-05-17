import { createContext, useEffect, useState } from "react";

export const CartContext = createContext();

const STORAGE_KEY = "cart";

function loadCart() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function CartProvider({ children }) {
  const [cart, setCart] = useState(loadCart);

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(cart));
  }, [cart]);

  const addToCart = (item) => {
    setCart((prev) => {
      const existing = prev.find((c) => c.id === item.id);
      if (existing) {
        return prev.map((c) =>
          c.id === item.id ? { ...c, quantity: c.quantity + 1 } : c
        );
      }
      return [...prev, { ...item, quantity: 1 }];
    });
  };

  const removeFromCart = (id) => {
    setCart((prev) => prev.filter((c) => c.id !== id));
  };

  const increaseQty = (id) => {
    setCart((prev) =>
      prev.map((c) =>
        c.id === id ? { ...c, quantity: c.quantity + 1 } : c
      )
    );
  };

  const decreaseQty = (id) => {
    setCart((prev) =>
      prev
        .map((c) =>
          c.id === id ? { ...c, quantity: c.quantity - 1 } : c
        )
        .filter((c) => c.quantity > 0)
    );
  };

  const clearCart = () => setCart([]);

  const items = cart.map((c) => ({
    foodId: c.id,
    quantity: c.quantity,
  }));

  const total = cart.reduce(
    (sum, c) => sum + (c.price || 0) * c.quantity,
    0
  );

  const itemCount = cart.reduce((sum, c) => sum + c.quantity, 0);

  return (
    <CartContext.Provider
      value={{
        cart,
        addToCart,
        removeFromCart,
        increaseQty,
        decreaseQty,
        clearCart,
        items,
        total,
        itemCount,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}
