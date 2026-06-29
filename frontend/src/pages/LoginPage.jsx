import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authService } from '../services/authService'

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [isAdmin, setIsAdmin] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const fn = isAdmin ? authService.adminLogin : authService.login
      const { data } = await fn(form)
      login(data)
      navigate(data.role === 'admin' ? '/admin/dashboard' : '/hotels')
    } catch (err) {
      setError(err.response?.data?.detail || 'Login failed. Check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  const styles = {
    page: { minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #e8f0fe 0%, #d2e3fc 100%)' },
    card: { background: '#fff', borderRadius: '12px', boxShadow: '0 4px 24px rgba(0,0,0,0.12)', padding: '40px', width: '100%', maxWidth: '420px' },
    header: { textAlign: 'center', marginBottom: '28px' },
    emoji: { fontSize: '48px', display: 'block', marginBottom: '12px' },
    title: { fontSize: '24px', fontWeight: '700', color: '#1a73e8', marginBottom: '6px' },
    subtitle: { fontSize: '14px', color: '#666' },
    toggle: { display: 'flex', background: '#f1f3f4', borderRadius: '8px', padding: '4px', marginBottom: '24px' },
    toggleBtn: (active) => ({
      flex: 1, padding: '8px', borderRadius: '6px', border: 'none', cursor: 'pointer', fontSize: '14px', fontWeight: '500',
      background: active ? '#1a73e8' : 'transparent', color: active ? '#fff' : '#666', transition: 'all 0.2s'
    }),
    submitBtn: {
      width: '100%', padding: '12px', background: '#1a73e8', color: '#fff', border: 'none',
      borderRadius: '8px', fontSize: '16px', fontWeight: '600', cursor: 'pointer', marginTop: '8px'
    },
    footer: { textAlign: 'center', marginTop: '20px', fontSize: '14px', color: '#666' },
    link: { color: '#1a73e8', fontWeight: '500' }
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.header}>
          <span style={styles.emoji}>🏨</span>
          <h1 style={styles.title}>Welcome Back</h1>
          <p style={styles.subtitle}>Sign in to your account</p>
        </div>

        <div style={styles.toggle}>
          <button style={styles.toggleBtn(!isAdmin)} onClick={() => setIsAdmin(false)}>Customer</button>
          <button style={styles.toggleBtn(isAdmin)} onClick={() => setIsAdmin(true)}>Admin</button>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email Address</label>
            <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="you@example.com" required />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="••••••••" required />
          </div>
          <button type="submit" style={styles.submitBtn} disabled={loading}>
            {loading ? 'Signing in...' : `Sign In as ${isAdmin ? 'Admin' : 'Customer'}`}
          </button>
        </form>

        {!isAdmin && (
          <p style={styles.footer}>
            Don't have an account? <Link to="/register" style={styles.link}>Register here</Link>
          </p>
        )}
      </div>
    </div>
  )
}
