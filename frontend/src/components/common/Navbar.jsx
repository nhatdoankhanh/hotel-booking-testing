import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const styles = {
    nav: {
      background: 'linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%)',
      padding: '0 24px',
      height: '64px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
      position: 'sticky',
      top: 0,
      zIndex: 100,
    },
    logo: {
      color: '#fff',
      fontSize: '20px',
      fontWeight: '700',
      display: 'flex',
      alignItems: 'center',
      gap: '8px',
    },
    links: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px',
    },
    link: {
      color: 'rgba(255,255,255,0.9)',
      padding: '8px 14px',
      borderRadius: '6px',
      fontSize: '14px',
      fontWeight: '500',
      transition: 'background 0.2s',
    },
    activeLink: {
      background: 'rgba(255,255,255,0.15)',
    },
    logoutBtn: {
      background: 'rgba(255,255,255,0.15)',
      color: '#fff',
      padding: '8px 16px',
      borderRadius: '6px',
      fontSize: '14px',
      fontWeight: '500',
      border: '1px solid rgba(255,255,255,0.3)',
      cursor: 'pointer',
    },
    userName: {
      color: 'rgba(255,255,255,0.8)',
      fontSize: '13px',
      padding: '0 10px',
    }
  }

  return (
    <nav style={styles.nav}>
      <Link to="/" style={styles.logo}>
        🏨 Hotel Booking System
      </Link>
      <div style={styles.links}>
        {!user && (
          <>
            <Link to="/hotels" style={styles.link}>Hotels</Link>
            <Link to="/login" style={styles.link}>Login</Link>
            <Link to="/register" style={{ ...styles.link, background: 'rgba(255,255,255,0.2)', border: '1px solid rgba(255,255,255,0.4)' }}>Register</Link>
          </>
        )}
        {user && !isAdmin && (
          <>
            <Link to="/hotels" style={styles.link}>Hotels</Link>
            <Link to="/my-bookings" style={styles.link}>My Bookings</Link>
            <span style={styles.userName}>👤 {user.user_name}</span>
            <button onClick={handleLogout} style={styles.logoutBtn}>Logout</button>
          </>
        )}
        {user && isAdmin && (
          <>
            <Link to="/admin/dashboard" style={styles.link}>Dashboard</Link>
            <Link to="/admin/hotels" style={styles.link}>Hotels</Link>
            <Link to="/admin/rooms" style={styles.link}>Rooms</Link>
            <Link to="/admin/bookings" style={styles.link}>Bookings</Link>
            <Link to="/admin/users" style={styles.link}>Users</Link>
            <Link to="/admin/payments" style={styles.link}>Payments</Link>
            <span style={styles.userName}>⚙️ {user.user_name}</span>
            <button onClick={handleLogout} style={styles.logoutBtn}>Logout</button>
          </>
        )}
      </div>
    </nav>
  )
}
