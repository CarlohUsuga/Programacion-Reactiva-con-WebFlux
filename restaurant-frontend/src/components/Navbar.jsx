import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { FiMenu, FiX, FiUser, FiLogOut } from 'react-icons/fi'
import { useAuth } from '../context/AuthContext'
import toast from 'react-hot-toast'
import './Navbar.css'

export default function Navbar() {
  const { isAuthenticated, user, logout } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)

  const close = () => setMenuOpen(false)

  const handleLogout = () => {
    logout()
    toast.success('Sesión cerrada')
    navigate('/')
    close()
  }

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="navbar-brand" onClick={close}>
          No se qúe nombre ponerle
        </Link>

        <button
          className="hamburger"
          onClick={() => setMenuOpen((o) => !o)}
          aria-label="Toggle menu"
        >
          {menuOpen ? <FiX size={24} /> : <FiMenu size={24} />}
        </button>

        <div className={`navbar-menu${menuOpen ? ' open' : ''}`}>
          <Link to="/" className="nav-link" onClick={close}>Inicio</Link>
          <Link to="/products" className="nav-link" onClick={close}>Menú</Link>

          {isAuthenticated ? (
            <>
              <span className="nav-user">
                <FiUser size={15} />
                Hola, {user?.name}
              </span>
              <button className="btn-logout" onClick={handleLogout}>
                <FiLogOut size={15} />
                Cerrar sesión
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn-nav-login" onClick={close}>
                Iniciar sesión
              </Link>
              <Link to="/register" className="btn-nav-register" onClick={close}>
                Registrarse
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}
