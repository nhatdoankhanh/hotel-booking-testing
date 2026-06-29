import { Navigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export function ProtectedRoute({ children, adminOnly = false }) {
  const { user, loading } = useAuth()

  if (loading) return <div className="spinner" />

  if (!user) return <Navigate to="/login" replace />

  if (adminOnly && user.role !== 'admin') return <Navigate to="/" replace />

  return children
}

export function PublicOnlyRoute({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="spinner" />
  if (user) return <Navigate to={user.role === 'admin' ? '/admin/dashboard' : '/hotels'} replace />
  return children
}
