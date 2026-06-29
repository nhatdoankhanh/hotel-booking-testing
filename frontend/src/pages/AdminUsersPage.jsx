import { useState, useEffect } from 'react'
import { userService } from '../services/userService'

export default function AdminUsersPage() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)

  const fetch = () => userService.getAll().then(({ data }) => setUsers(data)).finally(() => setLoading(false))
  useEffect(() => { fetch() }, [])

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this user?')) return
    await userService.delete(id)
    fetch()
  }

  if (loading) return <div className="spinner" />

  return (
    <div className="page-wrapper">
      <div className="page-header">
        <h1>👥 Customer Management</h1>
        <span style={{ fontSize: '14px', color: '#666' }}>{users.length} registered customers</span>
      </div>

      <div className="card">
        <div className="table-container">
          <table>
            <thead>
              <tr><th>ID</th><th>Name</th><th>Email</th><th>Mobile</th><th>Address</th><th>Role</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr><td colSpan="7" style={{ textAlign: 'center', padding: '24px', color: '#888' }}>No users registered</td></tr>
              ) : users.map(u => (
                <tr key={u.user_id}>
                  <td>#{u.user_id}</td>
                  <td><strong>{u.user_name}</strong></td>
                  <td>{u.email}</td>
                  <td>{u.mobile || '—'}</td>
                  <td>{u.address || '—'}</td>
                  <td><span className={`badge ${u.role === 'admin' ? 'badge-warning' : 'badge-info'}`}>{u.role}</span></td>
                  <td>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(u.user_id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
