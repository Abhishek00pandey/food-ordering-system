import { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import API from "../../services/api";
import LocationSearchBar from "../../components/LocationSearchBar";
import { LocationContext, ALL_LOCATIONS } from "../../context/LocationContext";

const emptyForm = { name: "", address: "", rating: "", locationId: "" };

function AdminRestaurants() {
  const { locations, selectedLocationId } = useContext(LocationContext);
  const [restaurants, setRestaurants] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");

  useEffect(() => {
    const id = setTimeout(() => setDebouncedSearch(search.trim()), 250);
    return () => clearTimeout(id);
  }, [search]);

  const load = () => {
    const params = {};
    if (selectedLocationId && selectedLocationId !== ALL_LOCATIONS) {
      params.locationId = selectedLocationId;
    }
    if (debouncedSearch) {
      params.search = debouncedSearch;
    }
    API.get("/restaurants", { params })
      .then((res) => setRestaurants(res.data))
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedLocationId, debouncedSearch]);

  const submit = async () => {
    if (!form.name.trim()) {
      alert("Name is required");
      return;
    }
    if (!form.locationId) {
      alert("Location is required");
      return;
    }
    const payload = {
      name: form.name,
      address: form.address,
      rating: form.rating ? parseFloat(form.rating) : null,
      location: { id: parseInt(form.locationId, 10) },
    };
    try {
      if (editingId) {
        await API.put(`/restaurants/${editingId}`, payload);
      } else {
        await API.post("/restaurants", payload);
      }
      setForm(emptyForm);
      setEditingId(null);
      load();
    } catch (err) {
      alert(err.response?.data?.message || "Save failed");
    }
  };

  const editRow = (r) => {
    setEditingId(r.id);
    setForm({
      name: r.name || "",
      address: r.address || "",
      rating: r.rating ?? "",
      locationId: r.location?.id ? String(r.location.id) : "",
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const deleteRow = async (id) => {
    if (!window.confirm("Delete this restaurant?")) return;
    try {
      await API.delete(`/restaurants/${id}`);
      load();
    } catch (err) {
      alert(err.response?.data?.message || "Delete failed");
    }
  };

  const unassignedCount = restaurants.filter((r) => !r.location).length;

  return (
    <div className="container">
      <LocationSearchBar searchValue={search} onSearchChange={setSearch} />

      <h2 style={{ marginTop: "20px" }}>Manage Restaurants</h2>

      {unassignedCount > 0 && selectedLocationId === ALL_LOCATIONS && (
        <div
          style={{
            background: "#fff3cd",
            border: "1px solid #ffe69c",
            color: "#856404",
            padding: "10px 14px",
            borderRadius: "8px",
            marginBottom: "14px",
          }}
        >
          {unassignedCount} restaurant(s) have no location assigned. Edit them to set a location.
        </div>
      )}

      <div className="card">
        <h3>{editingId ? "Edit Restaurant" : "Add Restaurant"}</h3>
        <input
          placeholder="Name"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <input
          placeholder="Address"
          value={form.address}
          onChange={(e) => setForm({ ...form, address: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <input
          placeholder="Rating (0-5)"
          type="number"
          step="0.1"
          value={form.rating}
          onChange={(e) => setForm({ ...form, rating: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <select
          value={form.locationId}
          onChange={(e) => setForm({ ...form, locationId: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        >
          <option value="">-- Select location --</option>
          {locations.map((l) => (
            <option key={l.id} value={l.id}>
              {l.name} ({l.label})
            </option>
          ))}
        </select>
        <button className="button" onClick={submit}>
          {editingId ? "Update" : "Add"}
        </button>
        {editingId && (
          <button
            className="button"
            style={{ background: "#999", marginLeft: "8px" }}
            onClick={cancelEdit}
          >
            Cancel
          </button>
        )}
      </div>

      <div className="grid" style={{ marginTop: "20px" }}>
        {restaurants.map((r) => (
          <div className="card" key={r.id}>
            <h3>{r.name}</h3>
            <p>{r.address}</p>
            <p style={{ color: "#666", fontSize: "13px", margin: "4px 0" }}>
              📍 {r.location ? r.location.name : "No location"}
            </p>
            {r.rating != null && <p>⭐ {r.rating}</p>}
            <div style={{ display: "flex", gap: "8px", flexWrap: "wrap" }}>
              <button className="button" onClick={() => editRow(r)}>Edit</button>
              <Link className="button" to={`/admin/menu/${r.id}`}>Menu</Link>
              <button
                className="button"
                style={{ background: "#c0392b" }}
                onClick={() => deleteRow(r.id)}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default AdminRestaurants;
