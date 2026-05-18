import { useContext, useEffect, useState } from "react";
import API from "../../services/api";
import { LocationContext } from "../../context/LocationContext";

const emptyForm = { name: "", label: "", sortOrder: "" };
const RECOMMENDED_MAX = 5;

function AdminLocations() {
  const { refreshLocations } = useContext(LocationContext);
  const [locations, setLocations] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const load = () => {
    API.get("/locations")
      .then((res) => setLocations(res.data || []))
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
    if (!form.label.trim()) {
      alert("Label is required");
      return;
    }
    const payload = {
      name: form.name.trim(),
      label: form.label.trim(),
      sortOrder: form.sortOrder ? parseInt(form.sortOrder, 10) : null,
    };
    try {
      if (editingId) {
        await API.put(`/locations/${editingId}`, payload);
      } else {
        await API.post("/locations", payload);
      }
      setForm(emptyForm);
      setEditingId(null);
      load();
      refreshLocations();
    } catch (err) {
      alert(err.response?.data?.message || "Save failed");
    }
  };

  const editRow = (l) => {
    setEditingId(l.id);
    setForm({
      name: l.name || "",
      label: l.label || "",
      sortOrder: l.sortOrder ?? "",
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const deleteRow = async (id) => {
    if (!window.confirm("Delete this location? Restaurants assigned to it will lose their location.")) return;
    try {
      await API.delete(`/locations/${id}`);
      load();
      refreshLocations();
    } catch (err) {
      alert(err.response?.data?.message || "Delete failed");
    }
  };

  return (
    <div className="container">
      <h2>Manage Locations</h2>

      {locations.length > RECOMMENDED_MAX && (
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
          You have {locations.length} locations. We recommend keeping it at {RECOMMENDED_MAX} or fewer
          so users can pick quickly.
        </div>
      )}

      <div className="card">
        <h3>{editingId ? "Edit Location" : "Add Location"}</h3>
        <input
          placeholder="Name (e.g. Hanumakonda)"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <input
          placeholder="Label (e.g. Ywca, Ashoka Rd, Hanumakonda)"
          value={form.label}
          onChange={(e) => setForm({ ...form, label: e.target.value })}
          style={{ width: "100%", padding: "8px", marginBottom: "8px" }}
        />
        <input
          placeholder="Sort order (optional, lower shows first)"
          type="number"
          value={form.sortOrder}
          onChange={(e) => setForm({ ...form, sortOrder: e.target.value })}
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
        {locations.map((l) => (
          <div className="card" key={l.id}>
            <h3>{l.name}</h3>
            <p>{l.label}</p>
            {l.sortOrder != null && (
              <p style={{ color: "#666", fontSize: "13px" }}>Sort: {l.sortOrder}</p>
            )}
            <div style={{ display: "flex", gap: "8px", flexWrap: "wrap" }}>
              <button className="button" onClick={() => editRow(l)}>Edit</button>
              <button
                className="button"
                style={{ background: "#c0392b" }}
                onClick={() => deleteRow(l.id)}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
        {locations.length === 0 && <p>No locations yet. Add one above.</p>}
      </div>
    </div>
  );
}

export default AdminLocations;
