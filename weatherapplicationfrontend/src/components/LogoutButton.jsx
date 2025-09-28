import { useAuth0 } from '@auth0/auth0-react'

const LogoutButton = () => {
  const { logout } = useAuth0()

  return (
    <button
      className="logout-btn"
      onClick={() => logout({
        logoutParams: {
            // Ensure the user returns to the application home after logout
            returnTo: window.location.origin
        }
      })}
    >
      Log Out
    </button>
  )
}

export default LogoutButton
