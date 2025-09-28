import { useState, useEffect } from 'react'

const Loading = () => {
  const [seconds, setSeconds] = useState(0)

  useEffect(() => {
    const interval = setInterval(() => {
      setSeconds(prev => prev + 1)
    }, 1000)
    // Simple timer to show approximate wait time while loading external data
    return () => clearInterval(interval)
  }, [])

  return (
    <div className="loading-container">
      <div className="loading-spinner"></div>
      <p>Loading... ({seconds}s)</p>
    </div>
  )
}

export default Loading
