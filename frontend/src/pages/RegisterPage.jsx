import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authService } from '../services/authService'

export default function RegisterPage() {
  const [form, setForm] = useState({ user_name: '', email: '', password: '', mobile: '', address: '' })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await authService.register({ ...form, role: 'customer' })
      setSuccess('Registration successful! Redirecting to login...')
      setTimeout(() => navigate('/login'), 1500)
    } catch (err) {
      setError(err.response?.data?.detail || 'Registration failed. Try again.')
    } finally {
      setLoading(false)
    }
  }

  const styles = {
    page: { minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #e8f0fe 0%, #d2e3fc 100%)', padding: '20px' },
    card: { background: '#fff', borderRadius: '12px', boxShadow: '0 4px 24px rgba(0,0,0,0.12)', padding: '40px', width: '100%', maxWidth: '460px' },
    header: { textAlign: 'center', marginBottom: '28px' },
    emoji: { fontSize: '48px', display: 'block', marginBottom: '12px' },
    title: { fontSize: '24px', fontWeight: '700', color: '#1a73e8', marginBottom: '6px' },
    row: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' },
    submitBtn: { width: '100%', padding: '12px', background: '#34a853', color: '#fff', border: 'none', borderRadius: '8px', fontSize: '16px', fontWeight: '600', cursor: 'pointer', marginTop: '8px' },
    footer: { textAlign: 'center', marginTop: '20px', fontSize: '14px', color: '#666' },
    link: { color: '#1a73e8', fontWeight: '500' }
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.header}>
          <span style={styles.emoji}>✍️</span>
          <h1 style={styles.title}>Create Account</h1>
          <p style={{ fontSize: '14px', color: '#666' }}>Join us to book your dream hotel</p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Full Name *</label>
            <input type="text" value={form.user_name} onChange={e => setForm({ ...form, user_name: e.target.value })} placeholder="John Doe" required />
          </div>
          <div className="form-group">
            <label>Email Address *</label>
            <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="john@example.com" required />
          </div>
          <div className="form-group">
            <label>Password *</label>
            <input type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="Min 8 characters" required />
          </div>
          <div style={styles.row}>
            <div className="form-group">
              <label>Mobile</label>
              <input type="tel" value={form.mobile} onChange={e => setForm({ ...form, mobile: e.target.value })} placeholder="+91 99999 00000" />
            </div>
            <div className="form-group">
              <label>Address</label>
              <input type="text" value={form.address} onChange={e => setForm({ ...form, address: e.target.value })} placeholder="City, State" />
            </div>
          </div>
          <button type="submit" style={styles.submitBtn} disabled={loading}>
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <p style={styles.footer}>
          Already have an account? <Link to="/login" style={styles.link}>Sign in</Link>
        </p>
      </div>
    </div>
  )
}
