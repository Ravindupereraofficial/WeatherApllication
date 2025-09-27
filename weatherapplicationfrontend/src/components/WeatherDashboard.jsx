import { useState, useEffect } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import axios from 'axios'
import WeatherCard from './WeatherCard'
import Loading from './Loading'

const WeatherDashboard = () => {
  const { getAccessTokenSilently } = useAuth0()
  const [weatherData, setWeatherData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

  useEffect(() => {
    fetchWeatherData()
  }, [])

  const fetchWeatherData = async () => {
    try {
      setLoading(true)
      setError(null)

      const token = await getAccessTokenSilently()

      const response = await axios.get(`${API_BASE_URL}/api/weather/all`, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      setWeatherData(response.data.List || [])
    } catch (err) {
      console.error('Error fetching weather data:', err)
      setError('Failed to fetch weather data. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleRefresh = () => {
    fetchWeatherData()
  }

  if (loading) {
    return <Loading />
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
        <button className="retry-btn" onClick={handleRefresh}>
          Try Again
        </button>
      </div>
    )
  }

  return (
    <div className="weather-dashboard">
      <div className="dashboard-header">
        <h2>Weather Information</h2>
        <button className="refresh-btn" onClick={handleRefresh}>
          Refresh Data
        </button>
      </div>

      {weatherData.length === 0 ? (
        <div className="no-data">
          <p>No weather data available.</p>
        </div>
      ) : (
        <div className="weather-grid">
          {weatherData.map((weather, index) => (
            <WeatherCard key={weather.CityCode || index} weather={weather} />
          ))}
        </div>
      )}
    </div>
  )
}

export default WeatherDashboard
