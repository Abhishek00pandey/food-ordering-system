import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";
import LocationSearchBar from "../components/LocationSearchBar";
import { LocationContext, ALL_LOCATIONS } from "../context/LocationContext";

function Restaurants() {
  const navigate = useNavigate();
  const [restaurants, setRestaurants] = useState([]);
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const { selectedLocationId, selectedLocation } = useContext(LocationContext);

  useEffect(() => {
    const id = setTimeout(() => setDebouncedSearch(search.trim()), 250);
    return () => clearTimeout(id);
  }, [search]);

  useEffect(() => {
    const params = {};
    if (selectedLocationId && selectedLocationId !== ALL_LOCATIONS) {
      params.locationId = selectedLocationId;
    }
    if (debouncedSearch) {
      params.search = debouncedSearch;
    }
    API.get("/restaurants", { params })
      .then((res) => setRestaurants(res.data))
      .catch((err) => console.error("Error fetching restaurants:", err));
  }, [selectedLocationId, debouncedSearch]);

  const emptyLabel = debouncedSearch
    ? `No results for "${debouncedSearch}"`
    : selectedLocationId === ALL_LOCATIONS
    ? "No restaurants found"
    : `No restaurants in ${selectedLocation?.name || "this location"}`;

  return (
    <div className="container">
      <LocationSearchBar searchValue={search} onSearchChange={setSearch} />

      <h2 style={{ marginTop: "20px" }}>Restaurants</h2>

      {restaurants.length === 0 ? (
        <p>{emptyLabel}</p>
      ) : (
        <div className="grid">
          {restaurants.map((r) => (
            <div className="card" key={r.id}>
              <h3>{r.name}</h3>
              <p>{r.address}</p>
              {r.location && (
                <p style={{ color: "#666", fontSize: "13px", margin: "4px 0" }}>
                  📍 {r.location.name}
                </p>
              )}
              {r.rating != null && <p>⭐ {r.rating}</p>}

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