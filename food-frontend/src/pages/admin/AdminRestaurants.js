import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import API from "../../services/api";

const emptyForm = { name: "", address: "", rating: "" };

function AdminRestaurants() {
  const [restaurants, setRestaurants] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const load = () => {
    API.get("/restaurants")
      .then((res) => setRestaurants(res.data))
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    load();
  }, []);

  const submit = async () => {
    if (!form.name.trim()) {
      alert("Name is required");
      return;
    }
    const payload = {
      name: form.name,
      address: form.address,
      rating: form.rating ? parseFloat(form.rating) : null,
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

  return (
    <div className="container">
      <h2>Manage Restaurants</h2>

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
