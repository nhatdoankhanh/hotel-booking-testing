import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { hotelService } from '../services/hotelService'
import { roomService } from '../services/roomService'
import { useAuth } from '../context/AuthContext'

export default function HotelDetailPage() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [hotel, setHotel] = useState(null)
  const [rooms, setRooms] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetch = async () => {
      try {
        const [hotelRes, roomRes] = await Promise.all([
          hotelService.getById(id),
          roomService.getAll(id),
        ])
        setHotel(hotelRes.data)
        setRooms(roomRes.data)
      } finally {
        setLoading(false)
      }
    }
    fetch()
  }, [id])

  if (loading) return <div className="spinner" />
  if (!hotel) return <div className="page-wrapper"><div className="empty-state"><h3>Hotel not found</h3></div></div>

  return (
    <div className="page-wrapper">
      <button onClick={() => navigate(-1)} className="btn btn-outline btn-sm" style={{ marginBottom: '20px' }}>← Back</button>

      <div className="card" style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap' }}>
          <div style={{ background: 'linear-gradient(135deg, #1a73e8, #0d47a1)', borderRadius: '8px', width: '200px', height: '160px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '64px', flexShrink: 0 }}>
            🏨
          </div>
          <div style={{ flex: 1 }}>
            <h1 style={{ fontSize: '28px', fontWeight: '700', marginBottom: '8px' }}>{hotel.hotel_name}</h1>
            <p style={{ color: '#666', marginBottom: '6px' }}>📍 {hotel.city} — {hotel.address}</p>
            {hotel.description && <p style={{ color: '#555', marginBottom: '12px', lineHeight: '1.6' }}>{hotel.description}</p>}
            <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', fontSize: '14px', color: '#666' }}>
              {hotel.phone1 && <span>📞 {hotel.phone1}</span>}
              {hotel.email && <span>✉️ {hotel.email}</span>}
              {hotel.website && <span>🌐 {hotel.website}</span>}
            </div>
            <p style={{ marginTop: '12px', fontSize: '20px', fontWeight: '700', color: '#1a73e8' }}>
              Avg. ₹{hotel.avg_rate_per_day?.toFixed(0)}/night
            </p>
          </div>
        </div>
      </div>

      <h2 style={{ fontSize: '20px', fontWeight: '700', marginBottom: '16px' }}>Available Rooms</h2>
      {rooms.length === 0 ? (
        <div className="empty-state"><h3>No rooms listed yet</h3></div>
      ) : (
        <div className="grid-3">
          {rooms.map(room => (
            <div key={room.room_id} className="card" style={{ position: 'relative' }}>
              <div style={{ position: 'absolute', top: '12px', right: '12px' }}>
                <span className={`badge ${room.isavailable ? 'badge-success' : 'badge-danger'}`}>
                  {room.isavailable ? 'Available' : 'Booked'}
                </span>
              </div>
              <div style={{ fontSize: '36px', marginBottom: '12px' }}>🛏️</div>
              <h3 style={{ fontWeight: '700', marginBottom: '6px' }}>Room {room.room_no}</h3>
              <p style={{ color: '#666', fontSize: '14px', marginBottom: '8px' }}>Type: {room.room_type}</p>
              <p style={{ fontSize: '18px', fontWeight: '700', color: '#1a73e8', marginBottom: '16px' }}>
                ₹{room.rate_per_day}<span style={{ fontSize: '13px', fontWeight: '400', color: '#888' }}>/night</span>
              </p>
              {room.isavailable && user && user.role !== 'admin' && (
                <button
                  className="btn btn-primary"
                  style={{ width: '100%' }}
                  onClick={() => navigate(`/book/${room.room_id}?hotel=${hotel.hotel_id}&rate=${room.rate_per_day}`)}
                >
                  Book Now
                </button>
              )}
              {!user && room.isavailable && (
                <button className="btn btn-outline" style={{ width: '100%' }} onClick={() => navigate('/login')}>
                  Login to Book
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
