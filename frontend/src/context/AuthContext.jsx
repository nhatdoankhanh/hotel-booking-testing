import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const stored = localStorage.getItem('hbs_user')
    if (stored) setUser(JSON.parse(stored))
    setLoading(false)
  }, [])

  const login = (userData) => {
    localStorage.setItem('hbs_user', JSON.stringify(userData))
    localStorage.setItem('hbs_token', userData.access_token)
    setUser(userData)
  }

  const logout = () => {
    localStorage.removeItem('hbs_user')
    localStorage.removeItem('hbs_token')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, isAdmin: user?.role === 'admin' }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
