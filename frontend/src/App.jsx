import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navbar from './components/common/Navbar'
import { ProtectedRoute, PublicOnlyRoute } from './components/common/ProtectedRoute'

import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import HotelsPage from './pages/HotelsPage'
import HotelDetailPage from './pages/HotelDetailPage'
import BookingPage from './pages/BookingPage'
import BookingHistoryPage from './pages/BookingHistoryPage'
import PaymentPage from './pages/PaymentPage'
import AdminDashboardPage from './pages/AdminDashboardPage'
import AdminHotelsPage from './pages/AdminHotelsPage'
import AdminRoomsPage from './pages/AdminRoomsPage'
import AdminBookingsPage from './pages/AdminBookingsPage'
import AdminUsersPage from './pages/AdminUsersPage'
import AdminPaymentsPage from './pages/AdminPaymentsPage'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          {/* Public */}
          <Route path="/" element={<Navigate to="/hotels" replace />} />
          <Route path="/hotels" element={<HotelsPage />} />
          <Route path="/hotels/:id" element={<HotelDetailPage />} />

          {/* Auth */}
          <Route path="/login" element={<PublicOnlyRoute><LoginPage /></PublicOnlyRoute>} />
          <Route path="/register" element={<PublicOnlyRoute><RegisterPage /></PublicOnlyRoute>} />

          {/* Customer */}
          <Route path="/book/:roomId" element={<ProtectedRoute><BookingPage /></ProtectedRoute>} />
          <Route path="/my-bookings" element={<ProtectedRoute><BookingHistoryPage /></ProtectedRoute>} />
          <Route path="/payment/:bookingId" element={<ProtectedRoute><PaymentPage /></ProtectedRoute>} />

          {/* Admin */}
          <Route path="/admin/dashboard" element={<ProtectedRoute adminOnly><AdminDashboardPage /></ProtectedRoute>} />
          <Route path="/admin/hotels" element={<ProtectedRoute adminOnly><AdminHotelsPage /></ProtectedRoute>} />
          <Route path="/admin/rooms" element={<ProtectedRoute adminOnly><AdminRoomsPage /></ProtectedRoute>} />
          <Route path="/admin/bookings" element={<ProtectedRoute adminOnly><AdminBookingsPage /></ProtectedRoute>} />
          <Route path="/admin/users" element={<ProtectedRoute adminOnly><AdminUsersPage /></ProtectedRoute>} />
          <Route path="/admin/payments" element={<ProtectedRoute adminOnly><AdminPaymentsPage /></ProtectedRoute>} />

          {/* Catch all */}
          <Route path="*" element={<Navigate to="/hotels" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
