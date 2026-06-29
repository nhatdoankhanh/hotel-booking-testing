import { useState, useEffect } from 'react'
import { hotelService } from '../services/hotelService'

const EMPTY = { city: '', hotel_name: '', address: '', description: '', avg_rate_per_day: '', email: '', phone1: '', phone2: '', website: '' }

export default function AdminHotelsPage() {
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState('')

  const fetch = () => hotelService.getAll().then(({ data }) => setHotels(data)).finally(() => setLoading(false))
  useEffect(() => { fetch() }, [])

  const openAdd = () => { setEditing(null); setForm(EMPTY); setError(''); setModal(true) }
  const openEdit = (h) => { setEditing(h); setForm({ ...h, avg_rate_per_day: h.avg_rate_per_day?.toString() }); setError(''); setModal(true) }

  const handleSave = async (e) => {
    e.preventDefault()
    setError('')
    const payload = { ...form, avg_rate_per_day: parseFloat(form.avg_rate_per_day) || 0 }
    try {
      if (editing) await hotelService.update(editing.hotel_id, payload)
      else await hotelService.create(payload)
      setModal(false)
      fetch()
    } catch (err) {
      setError(err.response?.data?.detail || 'Operation failed')
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this hotel? This will also remove all its rooms.')) return
    await hotelService.delete(id)
    fetch()
  }

  if (loading) return <div className="spinner" />

  return (
    <div className="page-wrapper">
      <div className="page-header">
        <h1>🏨 Hotel Management</h1>
        <button className="btn btn-primary" onClick={openAdd}>+ Add Hotel</button>
      </div>

      <div className="card">
        <div className="table-container">
          <table>
            <thead>
              <tr><th>ID</th><th>Name</th><th>City</th><th>Avg Rate/Night</th><th>Email</th><th>Phone</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {hotels.length === 0 ? (
                <tr><td colSpan="7" style={{ textAlign: 'center', padding: '24px', color: '#888' }}>No hotels added yet</td></tr>
              ) : hotels.map(h => (
                <tr key={h.hotel_id}>
                  <td>#{h.hotel_id}</td>
                  <td><strong>{h.hotel_name}</strong></td>
                  <td>{h.city}</td>
                  <td>₹{h.avg_rate_per_day}</td>
                  <td>{h.email || '—'}</td>
                  <td>{h.phone1 || '—'}</td>
                  <td>
                    <button className="btn btn-outline btn-sm" onClick={() => openEdit(h)} style={{ marginRight: '8px' }}>Edit</button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(h.hotel_id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editing ? 'Edit Hotel' : 'Add New Hotel'}</h2>
              <button className="modal-close" onClick={() => setModal(false)}>×</button>
            </div>
            {error && <div className="alert alert-error">{error}</div>}
            <form onSubmit={handleSave}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                {[['hotel_name', 'Hotel Name', 'text', true], ['city', 'City', 'text', true], ['avg_rate_per_day', 'Avg Rate/Night (₹)', 'number', true], ['email', 'Email', 'email', false], ['phone1', 'Phone 1', 'tel', false], ['phone2', 'Phone 2', 'tel', false], ['website', 'Website', 'url', false]].map(([key, label, type, required]) => (
                  <div className="form-group" key={key}>
                    <label>{label}{required ? ' *' : ''}</label>
                    <input type={type} value={form[key] || ''} onChange={e => setForm({ ...form, [key]: e.target.value })} required={required} min={type === 'number' ? '0' : undefined} />
                  </div>
                ))}
              </div>
              <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                <label>Address</label>
                <input type="text" value={form.address || ''} onChange={e => setForm({ ...form, address: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea rows="3" value={form.description || ''} onChange={e => setForm({ ...form, description: e.target.value })} style={{ width: '100%', padding: '10px', border: '1px solid #dadce0', borderRadius: '8px' }} />
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-outline" onClick={() => setModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">{editing ? 'Update' : 'Add'} Hotel</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
