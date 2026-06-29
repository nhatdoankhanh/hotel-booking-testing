import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { hotelService } from '../services/hotelService'
import { bookingService } from '../services/bookingService'
import { userService } from '../services/userService'
import { paymentService } from '../services/paymentService'

export default function AdminDashboardPage() {
  const navigate = useNavigate()
  const [stats, setStats] = useState({ hotels: 0, bookings: 0, users: 0, revenue: 0 })
  const [recentBookings, setRecentBookings] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      hotelService.getAll(),
      bookingService.getAll(),
      userService.getAll(),
      paymentService.getAll(),
    ]).then(([hotels, bookings, users, payments]) => {
      const revenue = payments.data.reduce((sum, p) => sum + (p.transaction?.amount || 0), 0)
      setStats({ hotels: hotels.data.length, bookings: bookings.data.length, users: users.data.length, revenue })
      setRecentBookings(bookings.data.slice(-5).reverse())
    }).finally(() => setLoading(false))
  }, [])

  const cards = [
    { title: 'Total Hotels', value: stats.hotels, icon: '🏨', color: '#1a73e8', bg: '#e8f0fe', path: '/admin/hotels' },
    { title: 'Total Bookings', value: stats.bookings, icon: '📋', color: '#34a853', bg: '#e6f4ea', path: '/admin/bookings' },
    { title: 'Registered Users', value: stats.users, icon: '👥', color: '#ea4335', bg: '#fce8e6', path: '/admin/users' },
    { title: 'Total Revenue', value: `₹${stats.revenue.toFixed(0)}`, icon: '💰', color: '#f29900', bg: '#fef7e0', path: '/admin/payments' },
  ]

  if (loading) return <div className="spinner" />

  return (
    <div className="page-wrapper">
      <h1 style={{ fontSize: '26px', fontWeight: '700', marginBottom: '28px' }}>⚙️ Admin Dashboard</h1>

      <div className="grid-2" style={{ marginBottom: '28px' }}>
        {cards.map(c => (
          <div key={c.title} className="card" style={{ cursor: 'pointer', borderLeft: `4px solid ${c.color}`, background: c.bg }}
            onClick={() => navigate(c.path)}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <p style={{ color: '#666', fontSize: '13px', marginBottom: '8px' }}>{c.title}</p>
                <p style={{ fontSize: '32px', fontWeight: '700', color: c.color }}>{c.value}</p>
              </div>
              <span style={{ fontSize: '40px' }}>{c.icon}</span>
            </div>
          </div>
        ))}
      </div>

      <div className="card">
        <div className="page-header">
          <h2 style={{ fontSize: '18px', fontWeight: '700' }}>Recent Bookings</h2>
          <button className="btn btn-outline btn-sm" onClick={() => navigate('/admin/bookings')}>View All</button>
        </div>
        <div className="table-container">
          <table>
            <thead>
              <tr><th>Booking ID</th><th>User ID</th><th>Hotel ID</th><th>Check-In</th><th>Check-Out</th><th>Amount</th><th>Status</th></tr>
            </thead>
            <tbody>
              {recentBookings.length === 0 ? (
                <tr><td colSpan="7" style={{ textAlign: 'center', color: '#888', padding: '24px' }}>No bookings yet</td></tr>
              ) : recentBookings.map(b => (
                <tr key={b.booking_id}>
                  <td>#{b.booking_id}</td>
                  <td>{b.user_id}</td>
                  <td>{b.hotel_id}</td>
                  <td>{b.booked_from}</td>
                  <td>{b.booked_to}</td>
                  <td>₹{b.amount?.toFixed(2)}</td>
                  <td><span className={`badge ${b.status === 'confirmed' ? 'badge-success' : b.status === 'cancelled' ? 'badge-danger' : 'badge-info'}`}>{b.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
