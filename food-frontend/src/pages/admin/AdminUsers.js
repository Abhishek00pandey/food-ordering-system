import { useEffect, useState } from "react";
import API from "../../services/api";

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const myEmail = localStorage.getItem("email");

  const load = () => {
    API.get("/admin/users")
      .then((res) => setUsers(res.data))
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    load();
  }, []);

  const changeRole = async (id, newRole, userLabel) => {
    const action = newRole === "ADMIN" ? "promote to ADMIN" : "demote to USER";
    if (!window.confirm(`${action} ${userLabel}?`)) return;
    try {
      await API.put(`/admin/users/${id}/role`, { role: newRole });
      load();
    } catch (err) {
      alert(err.response?.data?.message || "Role change failed");
    }
  };

  return (
    <div className="container">
      <h2>Users</h2>
      <p style={{ color: "#666", fontSize: "14px" }}>
        Promote a registered user to ADMIN, or demote an existing admin (you cannot demote yourself or the last admin).
      </p>

      {users.length === 0 ? (
        <p>No users found</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse", marginTop: "16px" }}>
          <thead>
            <tr style={{ background: "#eee" }}>
              <th style={{ padding: "10px", textAlign: "left" }}>ID</th>
              <th style={{ padding: "10px", textAlign: "left" }}>Name</th>
              <th style={{ padding: "10px", textAlign: "left" }}>Email</th>
              <th style={{ padding: "10px", textAlign: "left" }}>Role</th>
              <th style={{ padding: "10px", textAlign: "left" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => {
              const isSelf = u.email === myEmail;
              return (
                <tr key={u.id} style={{ borderBottom: "1px solid #ddd" }}>
                  <td style={{ padding: "10px" }}>{u.id}</td>
                  <td style={{ padding: "10px" }}>{u.name}</td>
                  <td style={{ padding: "10px" }}>{u.email}</td>
                  <td style={{ padding: "10px" }}>
                    <span
                      style={{
                        background: u.role === "ADMIN" ? "#27ae60" : "#777",
                        color: "white",
                        padding: "2px 8px",
                        borderRadius: "10px",
                        fontSize: "12px",
                      }}
                    >
                      {u.role}
                    </span>
                  </td>
                  <td style={{ padding: "10px" }}>
                    {isSelf ? (
                      <span style={{ color: "#999", fontSize: "13px" }}>(you)</span>
                    ) : u.role === "USER" ? (
                      <button
                        className="button"
                        onClick={() => changeRole(u.id, "ADMIN", u.email)}
                      >
                        Make Admin
                      </button>
                    ) : (
                      <button
                        className="button"
                        style={{ background: "#999" }}
                        onClick={() => changeRole(u.id, "USER", u.email)}
                      >
                        Demote
                      </button>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default AdminUsers;
