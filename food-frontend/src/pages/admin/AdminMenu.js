import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import API from "../../services/api";

const emptyForm = {
  name: "",
  price: "",
  description: "",
  imageUrl: "",
  category: "",
  available: true,
};

function AdminMenu() {
  const { restaurantId } = useParams();
  const navigate = useNavigate();
  const [restaurant, setRestaurant] = useState(null);
  const [foods, setFoods] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const load = () => {
    API.get(`/restaurants/${restaurantId}`)
      .then((res) => setRestaurant(res.data))
      .catch((err) => console.error(err));
    API.get(`/foods/${restaurantId}`)
      .then((res) => setFoods(res.data))
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [restaurantId]);

  const submit = async () => {
    if (!form.name.trim() || !form.price) {
      alert("Name and price are required");
      return;
    }
    const payload = {
      name: form.name,
      price: parseFloat(form.price),
      description: form.description || null,
      imageUrl: form.imageUrl || null,
      category: form.category || null,
      available: form.available,
    };
    try {
      if (editingId) {
        await API.put(`/foods/${editingId}`, payload);
      } else {
        await API.post(`/foods/${restaurantId}`, payload);
      }
      setForm(emptyForm);
      setEditingId(null);
      load();
    } catch (err) {
      alert(err.response?.data?.message || "Save failed");
    }
  };

  const editRow = (f) => {
    setEditingId(f.id);
    setForm({
      name: f.name || "",
      price: f.price ?? "",
      description: f.description || "",
      imageUrl: f.imageUrl || "",
      category: f.category || "",
      available: f.available !== false,
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const deleteRow = async (id) => {
    if (!window.confirm("Delete this food item?")) return;
    try {
      await API.delete(`/foods/${id}`);
      load();
    } catch (err) {
      alert(err.response?.data?.message || "Delete failed");
    }
  };

  return (
    <div className="container">
      <button
        className="button"
        style={{ background: "#999", marginBottom: "10px" }}
        onClick={() => navigate("/admin/restaurants")}
      >
        ← Back
      </button>
      <h2>Menu — {restaurant?.name || `Restaurant #${restaurantId}`}</h2>

      <div className="card">
        <h3>{editingId ? "Edit Food Item" : "Add Food Item"}</h3>
        <input
          placeholder="Name"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <input
          placeholder="Price"
          type="number"
          step="0.01"
          value={form.price}
          onChange={(e) => setForm({ ...form, price: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <input
          placeholder="Description"
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <input
          placeholder="Image URL"
          value={form.imageUrl}
          onChange={(e) => setForm({ ...form, imageUrl: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <input
          placeholder="Category"
          value={form.category}
          onChange={(e) => setForm({ ...form, category: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <label style={{ display: "block", marginBottom: "8px" }}>
          <input
            type="checkbox"
            checked={form.available}
            onChange={(e) => setForm({ ...form, available: e.target.checked })}
          />{" "}
          Available
        </label>
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
        {foods.map((f) => (
          <div className="card" key={f.id} style={{ opacity: f.available === false ? 0.6 : 1 }}>
            {f.imageUrl && (
              <img
                src={f.imageUrl}
                alt={f.name}
                style={{ width: "100%", height: "120px", objectFit: "cover", borderRadius: "6px" }}
              />
            )}
            <h3>{f.name}</h3>
            <p>₹{f.price}</p>
            {f.category && <p style={{ fontSize: "12px", color: "#666" }}>{f.category}</p>}
            {f.description && <p style={{ fontSize: "13px" }}>{f.description}</p>}
            <p style={{ fontSize: "12px" }}>
              Status: {f.available === false ? "Unavailable" : "Available"}
            </p>
            <div style={{ display: "flex", gap: "8px" }}>
              <button className="button" onClick={() => editRow(f)}>Edit</button>
              <button
                className="button"
                style={{ background: "#c0392b" }}
                onClick={() => deleteRow(f.id)}
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

export default AdminMenu;
