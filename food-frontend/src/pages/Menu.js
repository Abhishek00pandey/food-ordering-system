import { useEffect, useState, useContext } from "react";
import API from "../services/api";
import { CartContext } from "../context/CartContext";
import { useParams, useNavigate } from "react-router-dom";

function Menu() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [foods, setFoods] = useState([]);
  const [restaurant, setRestaurant] = useState(null);
  const { addToCart } = useContext(CartContext);

  useEffect(() => {
    API.get(`/restaurants/${id}`)
      .then((res) => setRestaurant(res.data))
      .catch((err) => console.error(err));

    API.get(`/foods/${id}`)
      .then((res) => setFoods(res.data))
      .catch((err) => console.error(err));
  }, [id]);

  return (
    <div className="container">
      {restaurant && (
        <div style={{ marginBottom: "20px" }}>
          <h2>{restaurant.name}</h2>
          <p>{restaurant.address}</p>
          {restaurant.rating != null && <p>⭐ {restaurant.rating}</p>}
        </div>
      )}

      <button className="button" onClick={() => navigate("/cart")}>
        Go to Cart
      </button>

      {foods.length === 0 ? (
        <p>No items found</p>
      ) : (
        <div className="grid">
          {foods.map((f) => {
            const unavailable = f.available === false;
            return (
              <div
                className="card"
                key={f.id}
                style={{ opacity: unavailable ? 0.5 : 1 }}
              >
                {f.imageUrl && (
                  <img
                    src={f.imageUrl}
                    alt={f.name}
                    style={{
                      width: "100%",
                      height: "150px",
                      objectFit: "cover",
                      borderRadius: "6px",
                      marginBottom: "8px",
                    }}
                  />
                )}
                <h3>{f.name}</h3>
                {f.category && (
                  <span
                    style={{
                      display: "inline-block",
                      background: "#eee",
                      padding: "2px 8px",
                      borderRadius: "10px",
                      fontSize: "12px",
                      marginBottom: "6px",
                    }}
                  >
                    {f.category}
                  </span>
                )}
                {f.description && (
                  <p style={{ fontSize: "13px", color: "#555" }}>
                    {f.description}
                  </p>
                )}
                <p>
                  <b>₹{f.price}</b>
                </p>

                <button
                  className="button"
                  disabled={unavailable}
                  onClick={() => addToCart(f)}
                >
                  {unavailable ? "Unavailable" : "Add to Cart"}
                </button>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default Menu;
