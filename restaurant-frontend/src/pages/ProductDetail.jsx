import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getProductById } from '../api/axios'
import { formatPrice } from '../utils/formatPrice'
import './ProductDetail.css'

const CATEGORY_META = {
  hamburguesas: { emoji: '\uD83C\uDF54', color: '#e67e22' },
  bebidas:      { emoji: '\uD83E\uDD64', color: '#2980b9' },
  postres:      { emoji: '\uD83C\uDF70', color: '#8e44ad' },
  acompan:      { emoji: '\uD83C\uDF5F', color: '#27ae60' },
}

function normalize(str) {
  return (str || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
}

function getCategoryMeta(name) {
  const n = normalize(name)
  if (n.includes('hamburgues')) return CATEGORY_META.hamburguesas
  if (n.includes('bebida'))     return CATEGORY_META.bebidas
  if (n.includes('postre'))     return CATEGORY_META.postres
  if (n.includes('acompa'))     return CATEGORY_META.acompan
  return { emoji: '\uD83C\uDF7D\uFE0F', color: '#7f5539' }
}

export default function ProductDetail() {
  const { id } = useParams()
  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)

  useEffect(() => {
    setLoading(true)
    setNotFound(false)
    getProductById(id)
      .then(res => setProduct(res.data))
      .catch(err => {
        if (err.response?.status === 404) setNotFound(true)
      })
      .finally(() => setLoading(false))
  }, [id])

  if (loading) {
    return (
      <div className="detail-page">
        <div className="detail-skeleton">
          <div className="skeleton-image" />
          <div className="skeleton-info">
            <div className="skeleton-line wide" />
            <div className="skeleton-line medium" />
            <div className="skeleton-line short" />
            <div className="skeleton-line medium" />
          </div>
        </div>
      </div>
    )
  }

  if (notFound || !product) {
    return (
      <div className="detail-page">
        <div className="detail-error">
          <span className="detail-error-icon">{'\uD83D\uDE35'}</span>
          <h2>Producto no encontrado</h2>
          <p>El producto que buscas no existe o ya no esta disponible.</p>
          <Link to="/products" className="btn-back-primary">Ver menu</Link>
        </div>
      </div>
    )
  }

  const meta = getCategoryMeta(product.categoryName)

  return (
    <div className="detail-page">
      <Link to="/products" className="btn-back">
        &larr; Volver al menu
      </Link>

      <div className="detail-card">
        <div className="detail-image" style={{ backgroundColor: meta.color + '22' }}>
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} />
          ) : (
            <span className="detail-emoji">{meta.emoji}</span>
          )}
        </div>

        <div className="detail-info">
          <span
            className="detail-category-badge"
            style={{ backgroundColor: meta.color + '22', color: meta.color }}
          >
            {meta.emoji} {product.categoryName}
          </span>

          <h1 className="detail-name">{product.name}</h1>

          {product.description && (
            <p className="detail-description">{product.description}</p>
          )}

          <p className="detail-price">{formatPrice(product.price)}</p>

          <span className={`detail-availability${product.available ? ' available' : ' unavailable'}`}>
            {product.available ? '\u2713 Disponible' : '\u2715 No disponible'}
          </span>
        </div>
      </div>
    </div>
  )
}
