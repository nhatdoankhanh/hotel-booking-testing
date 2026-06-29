import { useState, useEffect } from 'react'
import { hotelService } from '../services/hotelService'
import { roomService } from '../services/roomService'

const EMPTY = { hotel_id: '', room_no: '', room_type: 'Standard', rate_per_day: '', isavailable: true, photo: '' }
const ROOM_TYPES = ['Standard', 'Deluxe', 'Suite', 'Executive', 'Family', 'Presidential']

export default function AdminRoomsPage() {
  const [rooms, setRooms] = useState([])
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState('')
  const [filterHotel, setFilterHotel] = useState('')

  useEffect(() => {
    Promise.all([roomService.getAll(), hotelService.getAll()])
      .then(([r, h]) => { setRooms(r.data); setHotels(h.data) })
      .finally(() => setLoading(false))
  }, [])

  const fetch = () => {
    roomService.getAll(filterHotel || undefined).then(({ data }) => setRooms(data))
  }

  useEffect(() => { if (!loading) fetch() }, [filterHotel])

  const openAdd = () => { setEditing(null); setForm(EMPTY); setError(''); setModal(true) }
  const openEdit = (r) => { setEditing(r); setForm({ ...r, rate_per_day: r.rate_per_day?.toString(), hotel_id: r.hotel_id?.toString() }); setError(''); setModal(true) }

  const handleSave = async (e) => {
    e.preventDefault()
    setError('')
    const payload = { ...form, hotel_id: parseInt(form.hotel_id), rate_per_day: parseFloat(form.rate_per_day) }
    try {
      if (editing) await roomService.update(editing.room_id, payload)
      else await roomService.create(payload)
      setModal(false)
      fetch()
    } catch (err) {
      setError(err.response?.data?.detail || 'Operation failed')
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this room?')) return
    await roomService.delete(id)
    fetch()
  }

  const hotelName = (id) => hotels.find(h => h.hotel_id === id)?.hotel_name || `Hotel #${id}`

  if (loading) return <div className="spinner" />

  return (
    <div className="page-wrapper">
      <div className="page-header">
        <h1>🛏️ Room Management</h1>
        <button className="btn btn-primary" onClick={openAdd}>+ Add Room</button>
      </div>

      <div style={{ marginBottom: '16px', display: 'flex', gap: '12px', alignItems: 'center' }}>
        <label style={{ fontWeight: '600', fontSize: '14px' }}>Filter by Hotel:</label>
        <select value={filterHotel} onChange={e => setFilterHotel(e.target.value)} style={{ padding: '8px 12px', border: '1px solid #dadce0', borderRadius: '8px', fontSize: '14px' }}>
          <option value="">All Hotels</option>
          {hotels.map(h => <option key={h.hotel_id} value={h.hotel_id}>{h.hotel_name}</option>)}
        </select>
      </div>

      <div className="card">
        <div className="table-container">
          <table>
            <thead>
              <tr><th>ID</th><th>Hotel</th><th>Room No</th><th>Type</th><th>Rate/Night</th><th>Available</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {rooms.length === 0 ? (
                <tr><td colSpan="7" style={{ textAlign: 'center', padding: '24px', color: '#888' }}>No rooms found</td></tr>
              ) : rooms.map(r => (
                <tr key={r.room_id}>
                  <td>#{r.room_id}</td>
                  <td>{hotelName(r.hotel_id)}</td>
                  <td><strong>{r.room_no}</strong></td>
                  <td>{r.room_type}</td>
                  <td>₹{r.rate_per_day}</td>
                  <td><span className={`badge ${r.isavailable ? 'badge-success' : 'badge-danger'}`}>{r.isavailable ? 'Yes' : 'No'}</span></td>
                  <td>
                    <button className="btn btn-outline btn-sm" onClick={() => openEdit(r)} style={{ marginRight: '8px' }}>Edit</button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(r.room_id)}>Delete</button>
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
              <h2>{editing ? 'Edit Room' : 'Add New Room'}</h2>
              <button className="modal-close" onClick={() => setModal(false)}>×</button>
            </div>
            {error && <div className="alert alert-error">{error}</div>}
            <form onSubmit={handleSave}>
              <div className="form-group">
                <label>Hotel *</label>
                <select value={form.hotel_id} onChange={e => setForm({ ...form, hotel_id: e.target.value })} required>
                  <option value="">Select Hotel</option>
                  {hotels.map(h => <option key={h.hotel_id} value={h.hotel_id}>{h.hotel_name} – {h.city}</option>)}
                </select>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div className="form-group">
                  <label>Room Number *</label>
                  <input type="text" value={form.room_no} onChange={e => setForm({ ...form, room_no: e.target.value })} placeholder="101" required />
                </div>
                <div className="form-group">
                  <label>Room Type *</label>
                  <select value={form.room_type} onChange={e => setForm({ ...form, room_type: e.target.value })}>
                    {ROOM_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label>Rate per Night (₹) *</label>
                  <input type="number" min="0" value={form.rate_per_day} onChange={e => setForm({ ...form, rate_per_day: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label>Available</label>
                  <select value={form.isavailable.toString()} onChange={e => setForm({ ...form, isavailable: e.target.value === 'true' })}>
                    <option value="true">Yes</option>
                    <option value="false">No</option>
                  </select>
                </div>
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '8px' }}>
                <button type="button" className="btn btn-outline" onClick={() => setModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">{editing ? 'Update' : 'Add'} Room</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
