// WeatherCard displays normalized weather fields with sensible fallbacks for missing data
const WeatherCard = ({ weather }) => {
  const name = weather.cityName || weather.CityName || 'Unknown'
  const status = weather.status || weather.Status || 'Unknown'
  const tempRaw = weather.temp ?? weather.Temp ?? ''
  const cityCode = weather.cityCode || weather.CityCode || '—'

  const getTemperature = (temp) => {
    const n = parseFloat(temp)
    if (Number.isFinite(n)) return n.toFixed(1)
    return '—'
  }

  const getWeatherIcon = (statusText) => {
    const statusLower = (statusText || '').toLowerCase()
    if (statusLower.includes('clear')) return '☀️'
    if (statusLower.includes('cloud')) return '☁️'
    if (statusLower.includes('rain')) return '🌧️'
    if (statusLower.includes('snow')) return '❄️'
    if (statusLower.includes('thunderstorm')) return '⛈️'
    if (statusLower.includes('mist') || statusLower.includes('fog')) return '🌫️'
    return '🌤️'
  }

  return (
    <div className="weather-card">
      <div className="weather-card-header">
        <h3 className="city-name">{name}</h3>
        <div className="weather-icon">{getWeatherIcon(status)}</div>
      </div>

      <div className="weather-details">
        <div className="temperature">
          <span className="temp-value">{getTemperature(tempRaw)}</span>
          <span className="temp-unit">°C</span>
        </div>

        <div className="weather-status">{status}</div>

        <div className="city-code">City Code: {cityCode}</div>
      </div>
    </div>
  )
}

export default WeatherCard
