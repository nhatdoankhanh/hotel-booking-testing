import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { bookingService } from '../services/bookingService'

export default function BookingHistoryPage() {
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    bookingService.getMy()
      .then(({ data }) => setBookings(data))
      .finally(() => setLoading(false))
  }, [])

  const handleCancel = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) return
    try {
      await bookingService.cancel(id)
      setBookings(prev => prev.map(b => b.booking_id === id ? { ...b, status: 'cancelled' } : b))
    } catch {}
  }

  const statusBadge = (status) => {
    const map = { confirmed: 'badge-success', cancelled: 'badge-danger', pending: 'badge-warning', completed: 'badge-info' }
    return map[status] || 'badge-info'
  }

  if (loading) return <div className="spinner" />

  return (
    <div className="page-wrapper">
      <div className="page-header">
        <h1>📋 My Bookings</h1>
        <button className="btn btn-primary" onClick={() => navigate('/hotels')}>Book Another Room</button>
      </div>

      {bookings.length === 0 ? (
        <div className="empty-state">
          <h3>No bookings yet</h3>
          <p>Browse hotels and book your first stay!</p>
          <button className="btn btn-primary" style={{ marginTop: '16px' }} onClick={() => navigate('/hotels')}>Browse Hotels</button>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {bookings.map(b => (
            <div key={b.booking_id} className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
                  <span style={{ fontWeight: '700', fontSize: '16px' }}>Booking #{b.booking_id}</span>
                  <span className={`badge ${statusBadge(b.status)}`}>{b.status}</span>
                </div>
                <p style={{ color: '#666', fontSize: '14px' }}>📅 {b.booked_from} → {b.booked_to}</p>
                <p style={{ color: '#666', fontSize: '14px' }}>👥 {b.no_of_adults} Adults, {b.no_of_children} Children</p>
              </div>
              <div style={{ textAlign: 'right' }}>
                <p style={{ fontSize: '20px', fontWeight: '700', color: '#1a73e8', marginBottom: '8px' }}>₹{b.amount?.toFixed(2)}</p>
                <div style={{ display: 'flex', gap: '8px' }}>
                  {b.status === 'confirmed' && (
                    <>
                      <button className="btn btn-primary btn-sm" onClick={() => navigate(`/payment/${b.booking_id}`)}>Pay Now</button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleCancel(b.booking_id)}>Cancel</button>
                    </>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
