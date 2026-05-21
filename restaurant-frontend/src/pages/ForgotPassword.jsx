import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ForgotPassword() {
  const { forgotPassword } = useAuth()
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)
  const [sent, setSent] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await forgotPassword(email)
    } catch {
      // Always show confirmation regardless (security best practice)
    } finally {
      setSent(true)
      setLoading(false)
    }
  }

  if (sent) {
    return (
      <div className="auth-container">
        <h1>Revisa tu correo</h1>
        <p className="auth-subtitle" style={{ marginBottom: '2rem' }}>
          Si el correo existe, recibirás instrucciones para restablecer tu contraseña.
        </p>
        <div className="auth-links">
          <Link to="/login">← Volver al inicio de sesión</Link>
        </div>
      </div>
    )
  }

  return (
    <div className="auth-container">
      <h1>¿Olvidaste tu contraseña?</h1>
      <p className="auth-subtitle">Te enviaremos instrucciones por correo</p>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="email">Correo electrónico</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="tu@correo.com"
            required
          />
        </div>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Enviando...' : 'Enviar instrucciones'}
        </button>
      </form>

      <div className="auth-links">
        <Link to="/login">← Volver al inicio de sesión</Link>
      </div>
    </div>
  )
}
