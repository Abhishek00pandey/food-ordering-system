import { useRef, useState } from "react";
import API from "../../services/api";

const SAMPLE = {
  defaultLocationName: "Hanumakonda",
  restaurants: [
    {
      name: "Dominoz",
      address: "MG Road",
      rating: 4.2,
      menu: [
        {
          name: "Cheese Corn Pizza",
          price: 499,
          description: "Extra cheese and corn pizza with classic toppings",
          image_url:
            "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400",
          category: "Pizza",
          available: true,
        },
      ],
    },
    {
      name: "Pizza Hut",
      locationName: "Warangal",
      menu: [
        {
          name: "Margherita Pizza",
          price: 399,
          description: "Classic tomato sauce with fresh mozzarella and basil",
          image_url:
            "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400",
          category: "Pizza",
          available: true,
        },
      ],
    },
  ],
};

function AdminImport() {
  const [text, setText] = useState("");
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const fileRef = useRef(null);

  const handleFile = (e) => {
    setError("");
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (ev) => setText(String(ev.target?.result ?? ""));
    reader.onerror = () => setError("Could not read file");
    reader.readAsText(file);
  };

  const downloadSample = () => {
    const blob = new Blob([JSON.stringify(SAMPLE, null, 2)], {
      type: "application/json",
    });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "bulk-import-sample.json";
    a.click();
    URL.revokeObjectURL(url);
  };

  const runImport = async () => {
    setError("");
    setResult(null);
    if (!text.trim()) {
      setError("Paste JSON or upload a file first.");
      return;
    }
    let payload;
    try {
      payload = JSON.parse(text);
    } catch (e) {
      setError("Invalid JSON: " + e.message);
      return;
    }
    setBusy(true);
    try {
      const res = await API.post("/admin/bulk-import", payload);
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Import failed");
    } finally {
      setBusy(false);
    }
  };

  const clearAll = () => {
    setText("");
    setResult(null);
    setError("");
    if (fileRef.current) fileRef.current.value = "";
  };

  return (
    <div className="container">
      <h2>Bulk Import</h2>
      <p style={{ color: "#666" }}>
        Paste a JSON payload or upload a <code>.json</code> file. Existing
        restaurants (matched by name) are skipped. Bad rows are reported below;
        the rest of the import continues.
      </p>

      <div className="card">
        <div style={{ display: "flex", gap: "10px", flexWrap: "wrap", marginBottom: "10px" }}>
          <button className="button" onClick={downloadSample}>
            Download sample template
          </button>
          <label className="button" style={{ background: "#1f2937", cursor: "pointer" }}>
            Upload .json file
            <input
              ref={fileRef}
              type="file"
              accept="application/json,.json"
              onChange={handleFile}
              style={{ display: "none" }}
            />
          </label>
          <button
            className="button"
            style={{ background: "#999" }}
            onClick={clearAll}
            disabled={busy}
          >
            Clear
          </button>
        </div>

        <textarea
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder='{"defaultLocationName":"Hanumakonda","restaurants":[ ... ]}'
          style={{
            width: "100%",
            minHeight: "260px",
            padding: "10px",
            fontFamily: "monospace",
            fontSize: "13px",
            border: "1px solid #d8d8d8",
            borderRadius: "8px",
            boxSizing: "border-box",
          }}
        />

        <button
          className="button"
          onClick={runImport}
          disabled={busy}
          style={{ marginTop: "10px" }}
        >
          {busy ? "Importing..." : "Import"}
        </button>
      </div>

      {error && (
        <div
          className="card"
          style={{
            marginTop: "16px",
            background: "#fdecea",
            border: "1px solid #f5c2c0",
            color: "#a31515",
          }}
        >
          {error}
        </div>
      )}

      {result && (
        <div className="card" style={{ marginTop: "16px" }}>
          <h3>Import Summary</h3>
          <div className="grid">
            <Stat label="Restaurants created" value={result.restaurantsCreated} />
            <Stat label="Restaurants skipped" value={result.restaurantsSkipped} />
            <Stat label="Food items created" value={result.foodItemsCreated} />
            <Stat label="Food items failed" value={result.foodItemsFailed} />
          </div>

          {result.errors && result.errors.length > 0 && (
            <>
              <h4 style={{ marginTop: "16px" }}>Errors ({result.errors.length})</h4>
              <ul style={{ paddingLeft: "20px", color: "#a31515" }}>
                {result.errors.map((e, i) => (
                  <li key={i} style={{ marginBottom: "4px" }}>{e}</li>
                ))}
              </ul>
            </>
          )}
        </div>
      )}
    </div>
  );
}

function Stat({ label, value }) {
  return (
    <div
      style={{
        background: "#f7f7f7",
        padding: "10px 14px",
        borderRadius: "8px",
        textAlign: "center",
      }}
    >
      <div style={{ fontSize: "24px", fontWeight: "bold", color: "#222" }}>{value}</div>
      <div style={{ fontSize: "12px", color: "#666" }}>{label}</div>
    </div>
  );
}

export default AdminImport;