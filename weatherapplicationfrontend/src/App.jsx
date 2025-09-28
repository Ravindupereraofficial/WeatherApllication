import { useAuth0 } from '@auth0/auth0-react'
import WeatherDashboard from './components/WeatherDashboard'
import LoginButton from './components/LoginButton'
import LogoutButton from './components/LogoutButton'
import Loading from './components/Loading'
import './App.css'

function App() {
  const { isLoading, error, isAuthenticated, user } = useAuth0()

  if (error) {
    return <div className="error">Oops... {error.message}</div>
  }

  if (isLoading) {
    return <Loading />
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <h1 className="app-title">Weather Dashboard</h1>
          <div className="auth-section">
            {isAuthenticated ? (
              <div className="user-info">
                <span>Welcome, {user?.name}</span>
                <LogoutButton />
              </div>
            ) : (
              <LoginButton />
            )}
          </div>
        </div>
      </header>

      <main className="main-content">
        {isAuthenticated ? (
          <WeatherDashboard />
        ) : (
          <div className="welcome-section">
            <h2>Welcome to Weather Dashboard</h2>
            <p>Please log in to view weather information for various cities.</p>
          </div>
        )}
      </main>
    </div>
  )
}

export default App
