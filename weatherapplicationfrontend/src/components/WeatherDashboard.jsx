import { useState, useEffect, useRef } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import axios from 'axios'
import WeatherCard from './WeatherCard'
import Loading from './Loading'

const WeatherDashboard = () => {
  const { getAccessTokenSilently } = useAuth0()
  const [weatherData, setWeatherData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [disabledUntil, setDisabledUntil] = useState(0)
  const inFlightRef = useRef(false)
  const [tokenPayload, setTokenPayload] = useState(null)
  const [lastResponse, setLastResponse] = useState(null)

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

  useEffect(() => {
    fetchWeatherData()
  }, [])

  const fetchWeatherData = async () => {
    const startTime = Date.now()
    console.log('WeatherDashboard - Starting to fetch weather data...', new Date().toISOString())
    if (inFlightRef.current) {
      console.log('WeatherDashboard - Fetch already in progress, skipping duplicate call')
      return
    }
    if (disabledUntil && Date.now() < disabledUntil) {
      const wait = Math.ceil((disabledUntil - Date.now()) / 1000)
      console.warn(`WeatherDashboard - API temporarily disabled for ${wait}s due to previous server errors`)
      setError(`Service temporarily unavailable. Please try again in ${wait} seconds.`)
      setLoading(false)
      return
    }

    try {
      inFlightRef.current = true
      setLoading(true)
      setError(null)

      console.log('WeatherDashboard - Getting access token...')
      const tokenStart = Date.now()
      const audience = import.meta.env.VITE_AUTH0_AUDIENCE
      if (!audience || audience === 'REPLACE_WITH_YOUR_API_IDENTIFIER') {
        // If audience is not configured correctly the backend will reject access tokens with 401
        console.warn('WeatherDashboard - VITE_AUTH0_AUDIENCE is not set or uses placeholder. Access token request may not include the correct audience and backend will return 401.')
      }
      const token = await getAccessTokenSilently({ audience })
      console.log(`WeatherDashboard - Token obtained in ${Date.now() - tokenStart}ms`)
      console.log('WeatherDashboard - Token length:', token?.length)
      try {
        const parts = token.split('.')
        if (parts.length >= 2) {
          const payload = JSON.parse(decodeURIComponent(atob(parts[1]).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
          }).join('')))
          console.log('WeatherDashboard - Token payload:', payload)
          setTokenPayload(payload)
        }
      } catch (e) {
        console.warn('WeatherDashboard - Failed to decode token payload for inspection', e)
        setTokenPayload(null)
      }

  // Call the aggregated endpoint that returns weather for all tracked cities
  console.log('WeatherDashboard - Making API request to:', `${API_BASE_URL}/api/weather/all`)
      const apiStart = Date.now()
      const response = await axios.get(`${API_BASE_URL}/api/weather/all`, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        validateStatus: (status) => true
      })
      console.log('WeatherDashboard - API HTTP status:', response.status)
      if (response.status === 401) {
        console.warn('WeatherDashboard - Backend returned 401 Unauthorized. This usually means the access token audience or scopes are invalid for the API.')
        setError('Unauthorized: access token invalid for this API. Please check the configured audience.')
      } else if (response.status >= 400) {
        console.error('WeatherDashboard - Backend error response (object):', response.data)
        try {
          console.error('WeatherDashboard - Backend error response (string):', JSON.stringify(response.data))
        } catch (e) {
          console.warn('WeatherDashboard - Could not stringify backend response', e)
        }
        
        const serverMessage = response.data?.message || response.data || `Server returned status ${response.status}`
        setLastResponse(response.data)
        setError(`Server error: ${serverMessage}`)
        if (response.status >= 500) {
          const disableMs = 10000
          setDisabledUntil(Date.now() + disableMs)
          console.warn(`WeatherDashboard - Disabling further requests for ${disableMs / 1000}s due to server error`)
        }
      } else {
        setLastResponse(response.data)
        console.log(`WeatherDashboard - API response received in ${Date.now() - apiStart}ms`)
  const rawList = response.data.List || response.data.list || []
        
        const normalized = rawList.map(item => ({
          cityCode: item.cityCode || item.CityCode || item.cityCode || item.cityCode?.toString(),
          cityName: item.cityName || item.CityName || item.cityName,
          status: item.status || item.Status || item.status,
          temp: item.temp || item.Temp || item.temp
        }))
        setWeatherData(normalized)
      }
      console.log(`WeatherDashboard - Total fetch time: ${Date.now() - startTime}ms`)
    } catch (err) {
      console.error('WeatherDashboard - Error fetching weather data:', err)
      console.log(`WeatherDashboard - Error occurred after ${Date.now() - startTime}ms`)
      setError('Failed to fetch weather data. Please try again.')
    } finally {
      inFlightRef.current = false
      setLoading(false)
      console.log('WeatherDashboard - Loading state set to false')
    }
  }

  const handleRefresh = () => {
    fetchWeatherData()
  }

  if (loading) {
    console.log('WeatherDashboard - Showing loading screen...')
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
