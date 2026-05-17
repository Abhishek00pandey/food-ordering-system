import { useEffect, useState, useContext } from "react";
import API from "../services/api";
import { CartContext } from "../context/CartContext";
import { useParams, useNavigate } from "react-router-dom";

function Menu() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [foods, setFoods] = useState([]);
  const { addToCart } = useContext(CartContext);

  useEffect(() => {
    API.get(`/foods/${id}`)
      .then((res) => {
        console.log(res.data);
        setFoods(res.data);
      })
      .catch((err) => console.error(err));
  }, [id]);

  return (
    <div className="container">
      <h2>Menu</h2>

      <button className="button" onClick={() => navigate("/cart")}>
        Go to Cart
      </button>

      {foods.length === 0 ? (
        <p>No items found</p>
      ) : (
        <div className="grid"> {/* 🔥 IMPORTANT */}
          {foods.map((f) => (
            <div className="card" key={f.id}>
              <h3>{f.name}</h3>
              <p>₹{f.price}</p>

              <button
                className="button"
                onClick={() => addToCart(f)}
              >
                Add to Cart
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Menu;