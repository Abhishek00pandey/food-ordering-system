import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";

function Restaurants() {
  const navigate = useNavigate();
  const [restaurants, setRestaurants] = useState([]);

  useEffect(() => {
    API.get("/restaurants")
      .then((res) => {
        console.log(res.data);
        setRestaurants(res.data);
      })
      .catch((err) => {
        console.error("Error fetching restaurants:", err);
      });
  }, []);

  return (
    <div className="container">
      <h2>Restaurants</h2>

      {restaurants.length === 0 ? (
        <p>No restaurants found</p>
      ) : (
        <div className="grid"> {/* 🔥 IMPORTANT */}
          {restaurants.map((r) => (
            <div className="card" key={r.id}>
              <h3>{r.name}</h3>
              <p>{r.address}</p>

              <button
                className="button"
                onClick={() => navigate(`/menu/${r.id}`)}
              >
                View Menu
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Restaurants;