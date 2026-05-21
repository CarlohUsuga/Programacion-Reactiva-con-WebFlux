import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getCategories } from '../api/axios'
import './Home.css'

const CATEGORY_META = {
  hamburguesas: { emoji: '\uD83C\uDF54' },
  bebidas:      { emoji: '\uD83E\uDD64' },
  postres:      { emoji: '\uD83C\uDF70' },
  acompan:      { emoji: '\uD83C\uDF5F' },
}

function normalize(str) {
  return (str || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
}

function getCategoryEmoji(name) {
  const n = normalize(name)
  if (n.includes('hamburgues')) return CATEGORY_META.hamburguesas.emoji
  if (n.includes('bebida'))     return CATEGORY_META.bebidas.emoji
  if (n.includes('postre'))     return CATEGORY_META.postres.emoji
  if (n.includes('acompa'))     return CATEGORY_META.acompan.emoji
  return '\uD83C\uDF7D\uFE0F'
}

export default function Home() {
  const { isAuthenticated, user } = useAuth()
  const [categories, setCategories] = useState([])
  const [loadingCats, setLoadingCats] = useState(true)

  useEffect(() => {
    getCategories()
      .then(res => setCategories(res.data))
      .catch(() => {})
      .finally(() => setLoadingCats(false))
  }, [])

  return (
    <div className="home">
      <section className="hero">
        <div className="hero-content">
          <h1>Bienvenido </h1>
          {isAuthenticated ? (
            <p>
              Hola, <strong>{user.name}</strong>. Explora nuestro menu y disfruta!
            </p>
          ) : (
            <p>
              Descubre nuestro menu artesanal, ingredientes frescos y sabores unicos.
            </p>
          )}
          <div className="hero-actions">
            <Link to="/products" className="btn-hero-primary">Ver Menu</Link>
            {!isAuthenticated && (
              <Link to="/register" className="btn-hero-secondary">Crear cuenta</Link>
            )}
          </div>
        </div>
      </section>

      <section className="features">
        {loadingCats ? (
          [...Array(4)].map((_, i) => (
            <div key={i} className="feature-card feature-skeleton" />
          ))
        ) : categories.length > 0 ? (
          categories.map(cat => (
            <Link
              key={cat.id}
              to={`/products?category=${cat.id}`}
              className="feature-card feature-card-link"
            >
              <span className="feature-icon">{getCategoryEmoji(cat.name)}</span>
              <h3>{cat.name}</h3>
              {cat.description && <p>{cat.description}</p>}
            </Link>
          ))
        ) : (
          <>
            <div className="feature-card">
              <span className="feature-icon">{'\uD83C\uDF54'}</span>
              <h3>Hamburguesas Artesanales</h3>
              <p>Elaboradas con ingredientes frescos y carnes premium</p>
            </div>
            <div className="feature-card">
              <span className="feature-icon">{'\uD83E\uDD64'}</span>
              <h3>Bebidas Refrescantes</h3>
              <p>Limonadas naturales, malteadas y mas</p>
            </div>
            <div className="feature-card">
              <span className="feature-icon">{'\uD83C\uDF70'}</span>
              <h3>Postres Caseros</h3>
              <p>Dulces tentaciones hechas con amor</p>
            </div>
            <div className="feature-card">
              <span className="feature-icon">{'\uD83C\uDF5F'}</span>
              <h3>Acompañamientos</h3>
              <p>El complemento perfecto para tu combo</p>
            </div>
          </>
        )}
      </section>
    </div>
  )
}
