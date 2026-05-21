import { useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { resetPassword } from '../api/axios'

export default function ResetPassword() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token') || ''

  const [newPassword, setNewPassword]         = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [loading, setLoading]                 = useState(false)
  const [success, setSuccess]                 = useState(false)
  const [error, setError]                     = useState('')

  if (!token) {
    return (
      <div className="auth-container">
        <h1>Enlace invalido</h1>
        <p className="auth-subtitle" style={{ marginBottom: '2rem' }}>
          El enlace de recuperacion no es valido. Solicita uno nuevo desde la pagina de inicio de sesion.
        </p>
        <div className="auth-links">
          <Link to="/forgot-password">Solicitar nuevo enlace</Link>
          <Link to="/login">Volver al inicio de sesion</Link>
        </div>
      </div>
    )
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (newPassword !== confirmPassword) {
      setError('Las contrasenas no coinciden')
      return
    }

    setLoading(true)
    try {
      await resetPassword({ token, newPassword })
      setSuccess(true)
    } catch (err) {
      const msg = err.response?.data?.message
      setError(msg || 'El enlace es invalido o ya expiro. Solicita uno nuevo.')
    } finally {
      setLoading(false)
    }
  }

  if (success) {
    return (
      <div className="auth-container">
        <h1>Contrasena actualizada</h1>
        <p className="auth-subtitle" style={{ marginBottom: '2rem' }}>
          Tu contrasena fue restablecida correctamente. Ya puedes iniciar sesion con tu nueva contrasena.
        </p>
        <div className="auth-links">
          <Link to="/login">Ir al inicio de sesion</Link>
        </div>
      </div>
    )
  }

  return (
    <div className="auth-container">
      <h1>Nueva contrasena</h1>
      <p className="auth-subtitle">Elige una contrasena segura de al menos 6 caracteres</p>

      {error && <div className="error-msg">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="newPassword">Nueva contrasena</label>
          <input
            id="newPassword"
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            placeholder="Minimo 6 caracteres"
            minLength={6}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="confirmPassword">Confirmar contrasena</label>
          <input
            id="confirmPassword"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Repite tu contrasena"
            minLength={6}
            required
          />
        </div>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Guardando...' : 'Guardar nueva contrasena'}
        </button>
      </form>

      <div className="auth-links">
        <Link to="/login">Volver al inicio de sesion</Link>
      </div>
    </div>
  )
}
