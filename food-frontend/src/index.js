import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { CartProvider } from "./context/CartContext";
import { LocationProvider } from "./context/LocationContext";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <LocationProvider>
      <CartProvider>
        <App />
      </CartProvider>
    </LocationProvider>
  </React.StrictMode>
);

reportWebVitals();
