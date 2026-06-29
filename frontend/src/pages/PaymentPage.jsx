import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { bookingService } from '../services/bookingService'
import { hotelService } from '../services/hotelService'
import { paymentService } from '../services/paymentService'

export default function PaymentPage() {
  const { bookingId } = useParams()
  const navigate = useNavigate()
  const [booking, setBooking] = useState(null)
  const [hotel, setHotel] = useState(null)
  const [paymentMethod, setPaymentMethod] = useState('online')
  const [paid, setPaid] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    bookingService.getById(bookingId).then(({ data }) => {
      setBooking(data)
      return hotelService.getById(data.hotel_id)
    }).then(({ data }) => setHotel(data)).catch(() => {})
  }, [bookingId])

  const handlePay = async () => {
    setError('')
    setLoading(true)
    try {
      await paymentService.makePayment(parseInt(bookingId), paymentMethod)
      setPaid(true)
    } catch (err) {
      setError(err.response?.data?.detail || 'Payment failed. Try again.')
    } finally {
      setLoading(false)
    }
  }

  if (!booking) return <div className="spinner" />

  if (paid) return (
    <div className="page-wrapper" style={{ maxWidth: '500px', textAlign: 'center' }}>
      <div className="card" style={{ padding: '48px 32px' }}>
        <div style={{ fontSize: '72px', marginBottom: '20px' }}>✅</div>
        <h2 style={{ fontSize: '24px', fontWeight: '700', color: '#34a853', marginBottom: '12px' }}>Payment Successful!</h2>
        <p style={{ color: '#555', marginBottom: '24px' }}>Your booking has been confirmed. Enjoy your stay!</p>
        <p><strong>Booking ID:</strong> #{booking.booking_id}</p>
        <p style={{ marginTop: '8px' }}><strong>Amount Paid:</strong> ₹{booking.amount?.toFixed(2)}</p>
        <button className="btn btn-primary" style={{ marginTop: '24px' }} onClick={() => navigate('/my-bookings')}>
          View My Bookings
        </button>
      </div>
    </div>
  )

  return (
    <div className="page-wrapper" style={{ maxWidth: '520px' }}>
      <h1 style={{ fontSize: '24px', fontWeight: '700', marginBottom: '24px' }}>💳 Payment</h1>

      <div className="card" style={{ marginBottom: '20px' }}>
        <h3 style={{ fontWeight: '700', marginBottom: '16px' }}>Booking Summary</h3>
        <table style={{ width: '100%' }}>
          <tbody>
            <tr><td style={{ padding: '6px 0', color: '#666' }}>Hotel</td><td style={{ fontWeight: '600' }}>{hotel?.hotel_name}</td></tr>
            <tr><td style={{ padding: '6px 0', color: '#666' }}>Check-In</td><td style={{ fontWeight: '600' }}>{booking.booked_from}</td></tr>
            <tr><td style={{ padding: '6px 0', color: '#666' }}>Check-Out</td><td style={{ fontWeight: '600' }}>{booking.booked_to}</td></tr>
            <tr><td style={{ padding: '6px 0', color: '#666' }}>Guests</td><td style={{ fontWeight: '600' }}>{booking.no_of_adults} Adults, {booking.no_of_children} Children</td></tr>
            <tr><td style={{ padding: '12px 0 6px', borderTop: '1px solid #eee', fontWeight: '700' }}>Total Amount</td><td style={{ padding: '12px 0 6px', borderTop: '1px solid #eee', fontWeight: '700', color: '#1a73e8', fontSize: '18px' }}>₹{booking.amount?.toFixed(2)}</td></tr>
          </tbody>
        </table>
      </div>

      <div className="card">
        <h3 style={{ fontWeight: '700', marginBottom: '16px' }}>Payment Method</h3>
        {error && <div className="alert alert-error">{error}</div>}
        {['online', 'card', 'upi', 'cash'].map(method => (
          <label key={method} style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 0', cursor: 'pointer', borderBottom: '1px solid #f0f0f0' }}>
            <input type="radio" name="pm" value={method} checked={paymentMethod === method} onChange={() => setPaymentMethod(method)} />
            <span style={{ textTransform: 'capitalize', fontWeight: '500' }}>
              {method === 'online' ? '🌐 Online Banking' : method === 'card' ? '💳 Credit/Debit Card' : method === 'upi' ? '📱 UPI' : '💵 Cash at Property'}
            </span>
          </label>
        ))}
        <button className="btn btn-success" style={{ width: '100%', padding: '14px', fontSize: '16px', marginTop: '20px' }} onClick={handlePay} disabled={loading}>
          {loading ? 'Processing...' : `Pay ₹${booking.amount?.toFixed(2)}`}
        </button>
      </div>
    </div>
  )
}
