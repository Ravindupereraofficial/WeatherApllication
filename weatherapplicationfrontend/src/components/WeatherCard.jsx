const WeatherCard = ({ weather }) => {
  const getTemperatureInCelsius = (temp) => {
    const celsius = parseFloat(temp) - 273.15
    return celsius.toFixed(1)
  }

  const getWeatherIcon = (status) => {
    const statusLower = status.toLowerCase()
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
        <h3 className="city-name">{weather.CityName}</h3>
        <div className="weather-icon">
          {getWeatherIcon(weather.Status)}
        </div>
      </div>

      <div className="weather-details">
        <div className="temperature">
          <span className="temp-value">{getTemperatureInCelsius(weather.Temp)}</span>
          <span className="temp-unit">°C</span>
        </div>

        <div className="weather-status">
          {weather.Status}
        </div>

        <div className="city-code">
          City Code: {weather.CityCode}
        </div>
      </div>
    </div>
  )
}

export default WeatherCard
