import { useState, useEffect } from 'react'
import { useParams, useSearchParams, useNavigate } from 'react-router-dom'
import { roomService } from '../services/roomService'
import { hotelService } from '../services/hotelService'
import { bookingService } from '../services/bookingService'

export default function BookingPage() {
  const { roomId } = useParams()
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const hotelId = searchParams.get('hotel')

  const [room, setRoom] = useState(null)
  const [hotel, setHotel] = useState(null)
  const [form, setForm] = useState({ booked_from: '', booked_to: '', no_of_adults: 1, no_of_children: 0 })
  const [summary, setSummary] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    Promise.all([roomService.getById(roomId), hotelService.getById(hotelId)])
      .then(([r, h]) => { setRoom(r.data); setHotel(h.data) })
  }, [roomId, hotelId])

  useEffect(() => {
    if (form.booked_from && form.booked_to && room) {
      const days = Math.max(0, (new Date(form.booked_to) - new Date(form.booked_from)) / 86400000)
      if (days > 0) setSummary({ days, total: days * room.rate_per_day })
      else setSummary(null)
    }
  }, [form.booked_from, form.booked_to, room])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data: booking } = await bookingService.create({
        hotel_id: parseInt(hotelId),
        room_id: parseInt(roomId),
        booked_from: form.booked_from,
        booked_to: form.booked_to,
        no_of_adults: parseInt(form.no_of_adults),
        no_of_children: parseInt(form.no_of_children),
      })
      navigate(`/payment/${booking.booking_id}`)
    } catch (err) {
      setError(err.response?.data?.detail || 'Booking failed. Try again.')
    } finally {
      setLoading(false)
    }
  }

  if (!room || !hotel) return <div className="spinner" />

  const today = new Date().toISOString().split('T')[0]

  return (
    <div className="page-wrapper" style={{ maxWidth: '720px' }}>
      <button onClick={() => navigate(-1)} className="btn btn-outline btn-sm" style={{ marginBottom: '20px' }}>← Back</button>
      <h1 style={{ fontSize: '24px', fontWeight: '700', marginBottom: '24px' }}>📅 Book Your Room</h1>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', flexWrap: 'wrap' }}>
        <div className="card">
          <h3 style={{ fontWeight: '700', marginBottom: '16px', color: '#1a73e8' }}>Room & Hotel Details</h3>
          <p><strong>Hotel:</strong> {hotel.hotel_name}</p>
          <p style={{ marginTop: '8px' }}><strong>City:</strong> {hotel.city}</p>
          <p style={{ marginTop: '8px' }}><strong>Room No:</strong> {room.room_no}</p>
          <p style={{ marginTop: '8px' }}><strong>Room Type:</strong> {room.room_type}</p>
          <p style={{ marginTop: '8px', fontSize: '18px', fontWeight: '700', color: '#1a73e8' }}>₹{room.rate_per_day}/night</p>
        </div>

        {summary && (
          <div className="card" style={{ background: 'linear-gradient(135deg, #e8f0fe, #d2e3fc)' }}>
            <h3 style={{ fontWeight: '700', marginBottom: '16px', color: '#1a73e8' }}>Booking Summary</h3>
            <p><strong>Duration:</strong> {summary.days} night{summary.days !== 1 ? 's' : ''}</p>
            <p style={{ marginTop: '8px' }}><strong>Rate:</strong> ₹{room.rate_per_day}/night</p>
            <hr style={{ margin: '12px 0', borderColor: '#c5d6fb' }} />
            <p style={{ fontSize: '20px', fontWeight: '700', color: '#1a73e8' }}>Total: ₹{summary.total.toFixed(2)}</p>
          </div>
        )}
      </div>

      <div className="card" style={{ marginTop: '20px' }}>
        <h3 style={{ fontWeight: '700', marginBottom: '20px', color: '#1a73e8' }}>Booking Details</h3>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label>Check-In Date *</label>
              <input type="date" value={form.booked_from} min={today} onChange={e => setForm({ ...form, booked_from: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Check-Out Date *</label>
              <input type="date" value={form.booked_to} min={form.booked_from || today} onChange={e => setForm({ ...form, booked_to: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>No. of Adults *</label>
              <input type="number" min="1" max="10" value={form.no_of_adults} onChange={e => setForm({ ...form, no_of_adults: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>No. of Children</label>
              <input type="number" min="0" max="10" value={form.no_of_children} onChange={e => setForm({ ...form, no_of_children: e.target.value })} />
            </div>
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', padding: '14px', fontSize: '16px', marginTop: '8px' }} disabled={loading}>
            {loading ? 'Confirming...' : 'Confirm Booking & Proceed to Payment'}
          </button>
        </form>
      </div>
    </div>
  )
}
