import { useState, useEffect } from 'react'
import { bookingService } from '../services/bookingService'

export default function AdminBookingsPage() {
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('all')

  const fetch = () => bookingService.getAll().then(({ data }) => setBookings(data)).finally(() => setLoading(false))
  useEffect(() => { fetch() }, [])

  const statusBadge = (s) => ({ confirmed: 'badge-success', cancelled: 'badge-danger', pending: 'badge-warning', completed: 'badge-info' }[s] || 'badge-info')

  const filtered = filter === 'all' ? bookings : bookings.filter(b => b.status === filter)

  if (loading) return <div className="spinner" />

  return (
    <div className="page-wrapper">
      <div className="page-header">
        <h1>📋 All Bookings</h1>
        <div style={{ display: 'flex', gap: '8px' }}>
          {['all', 'confirmed', 'cancelled', 'completed'].map(s => (
            <button key={s} className={`btn btn-sm ${filter === s ? 'btn-primary' : 'btn-outline'}`} onClick={() => setFilter(s)} style={{ textTransform: 'capitalize' }}>{s}</button>
          ))}
        </div>
      </div>

      <div className="card">
        <div style={{ marginBottom: '12px', fontSize: '14px', color: '#666' }}>
          Showing {filtered.length} of {bookings.length} bookings
        </div>
        <div className="table-container">
          <table>
            <thead>
              <tr><th>Booking ID</th><th>User ID</th><th>Hotel ID</th><th>Room ID</th><th>Check-In</th><th>Check-Out</th><th>Guests</th><th>Amount</th><th>Status</th></tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr><td colSpan="9" style={{ textAlign: 'center', padding: '24px', color: '#888' }}>No bookings found</td></tr>
              ) : filtered.map(b => (
                <tr key={b.booking_id}>
                  <td>#{b.booking_id}</td>
                  <td>{b.user_id}</td>
                  <td>{b.hotel_id}</td>
                  <td>{b.room_id}</td>
                  <td>{b.booked_from}</td>
                  <td>{b.booked_to}</td>
                  <td>{b.no_of_adults}A + {b.no_of_children}C</td>
                  <td><strong>₹{b.amount?.toFixed(2)}</strong></td>
                  <td><span className={`badge ${statusBadge(b.status)}`}>{b.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
