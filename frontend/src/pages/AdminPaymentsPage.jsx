import { useState, useEffect } from 'react'
import { paymentService } from '../services/paymentService'

export default function AdminPaymentsPage() {
  const [payments, setPayments] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    paymentService.getAll().then(({ data }) => setPayments(data)).finally(() => setLoading(false))
  }, [])

  const totalRevenue = payments.reduce((sum, p) => sum + (p.transaction?.amount || 0), 0)

  if (loading) return <div className="spinner" />

  return (
    <div className="page-wrapper">
      <div className="page-header">
        <h1>💰 Payment Records</h1>
        <div className="card" style={{ padding: '12px 24px', background: '#e6f4ea' }}>
          <span style={{ fontSize: '13px', color: '#555' }}>Total Revenue</span>
          <div style={{ fontSize: '22px', fontWeight: '700', color: '#34a853' }}>₹{totalRevenue.toFixed(2)}</div>
        </div>
      </div>

      <div className="card">
        <div className="table-container">
          <table>
            <thead>
              <tr><th>Payment ID</th><th>Booking ID</th><th>Transaction ID</th><th>Amount</th><th>Method</th><th>Date</th><th>Status</th></tr>
            </thead>
            <tbody>
              {payments.length === 0 ? (
                <tr><td colSpan="7" style={{ textAlign: 'center', padding: '24px', color: '#888' }}>No payments yet</td></tr>
              ) : payments.map(p => (
                <tr key={p.payment_id}>
                  <td>#{p.payment_id}</td>
                  <td>#{p.booking_id}</td>
                  <td>#{p.transaction_id}</td>
                  <td><strong>₹{p.transaction?.amount?.toFixed(2)}</strong></td>
                  <td style={{ textTransform: 'capitalize' }}>{p.transaction?.payment_method}</td>
                  <td>{p.payment_date ? new Date(p.payment_date).toLocaleDateString('en-IN') : '—'}</td>
                  <td><span className={`badge ${p.status === 'completed' ? 'badge-success' : 'badge-warning'}`}>{p.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
