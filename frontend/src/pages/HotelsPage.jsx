import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { hotelService } from '../services/hotelService'

export default function HotelsPage() {
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const navigate = useNavigate()

  const fetchHotels = async (city = '') => {
    setLoading(true)
    try {
      const { data } = await hotelService.getAll(city)
      setHotels(data)
    } catch {
      setHotels([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchHotels() }, [])

  const handleSearch = (e) => {
    e.preventDefault()
    fetchHotels(search)
  }

  const cardStyle = {
    background: '#fff', borderRadius: '12px', boxShadow: '0 2px 12px rgba(0,0,0,0.1)',
    overflow: 'hidden', cursor: 'pointer', transition: 'transform 0.2s, box-shadow 0.2s'
  }

  return (
    <div className="page-wrapper">
      <div className="page-header">
        <h1>🏨 Browse Hotels</h1>
      </div>

      <form onSubmit={handleSearch} style={{ display: 'flex', gap: '12px', marginBottom: '28px' }}>
        <input
          type="text"
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Search by city (e.g. Mumbai, Delhi...)"
          style={{ flex: 1, padding: '12px 16px', border: '1px solid #dadce0', borderRadius: '8px', fontSize: '15px' }}
        />
        <button type="submit" className="btn btn-primary">Search</button>
        {search && <button type="button" className="btn btn-outline" onClick={() => { setSearch(''); fetchHotels() }}>Clear</button>}
      </form>

      {loading ? (
        <div className="spinner" />
      ) : hotels.length === 0 ? (
        <div className="empty-state"><h3>No hotels found</h3><p>Try searching a different city</p></div>
      ) : (
        <div className="grid-3">
          {hotels.map(hotel => (
            <div key={hotel.hotel_id} style={cardStyle}
              onClick={() => navigate(`/hotels/${hotel.hotel_id}`)}
              onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-4px)'; e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.15)' }}
              onMouseLeave={e => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 2px 12px rgba(0,0,0,0.1)' }}
            >
              <div style={{ background: 'linear-gradient(135deg, #1a73e8, #0d47a1)', height: '140px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '48px' }}>
                🏨
              </div>
              <div style={{ padding: '16px' }}>
                <h3 style={{ fontSize: '17px', fontWeight: '700', marginBottom: '6px' }}>{hotel.hotel_name}</h3>
                <p style={{ color: '#666', fontSize: '13px', marginBottom: '8px' }}>📍 {hotel.city}</p>
                {hotel.description && (
                  <p style={{ color: '#888', fontSize: '13px', marginBottom: '12px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {hotel.description}
                  </p>
                )}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ color: '#1a73e8', fontWeight: '700', fontSize: '16px' }}>
                    ₹{hotel.avg_rate_per_day?.toFixed(0)}<span style={{ fontSize: '12px', fontWeight: '400', color: '#888' }}>/night</span>
                  </span>
                  <span style={{ background: '#e8f0fe', color: '#1a73e8', padding: '4px 10px', borderRadius: '20px', fontSize: '12px' }}>
                    View Rooms →
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
