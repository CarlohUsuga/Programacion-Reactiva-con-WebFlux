import { useState, useEffect } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { getCategories, getProducts, getProductsByCategory, searchProducts } from '../api/axios'
import { formatPrice } from '../utils/formatPrice'
import './Products.css'

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

export default function Products() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [categories, setCategories]     = useState([])
  const [products, setProducts]         = useState([])
  const [loading, setLoading]           = useState(true)
  const [searchQuery, setSearchQuery]   = useState('')
  const [selectedCategory, setSelectedCategory] = useState(
    searchParams.get('category') || 'all'
  )

  useEffect(() => {
    getCategories()
      .then(res => setCategories(res.data))
      .catch(() => {})
  }, [])

  useEffect(() => {
    const delay = searchQuery ? 300 : 0
    const timer = setTimeout(() => {
      setLoading(true)
      let request
      if (searchQuery) {
        request = searchProducts(searchQuery)
      } else if (selectedCategory === 'all') {
        request = getProducts()
      } else {
        request = getProductsByCategory(selectedCategory)
      }
      request
        .then(res => setProducts(res.data))
        .catch(() => setProducts([]))
        .finally(() => setLoading(false))
    }, delay)
    return () => clearTimeout(timer)
  }, [searchQuery, selectedCategory])

  function handleCategorySelect(catId) {
    setSelectedCategory(catId)
    setSearchQuery('')
    if (catId === 'all') {
      setSearchParams({})
    } else {
      setSearchParams({ category: catId })
    }
  }

  return (
    <div className="products-page">
      <h1>Nuestro Menu</h1>

      <div className="search-bar">
        <span className="search-icon">&#128269;</span>
        <input
          type="text"
          placeholder="Buscar productos..."
          value={searchQuery}
          onChange={e => setSearchQuery(e.target.value)}
        />
        {searchQuery && (
          <button className="search-clear" onClick={() => setSearchQuery('')} aria-label="Limpiar">
            &#x2715;
          </button>
        )}
      </div>

      <div className="category-tabs">
        <button
          className={`tab-btn${selectedCategory === 'all' ? ' active' : ''}`}
          onClick={() => handleCategorySelect('all')}
        >
          Todos
        </button>
        {categories.map(cat => {
          const meta = getCategoryMeta(cat.name)
          return (
            <button
              key={cat.id}
              className={`tab-btn${selectedCategory === String(cat.id) ? ' active' : ''}`}
              onClick={() => handleCategorySelect(String(cat.id))}
            >
              {meta.emoji} {cat.name}
            </button>
          )
        })}
      </div>

      {loading ? (
        <div className="products-grid">
          {[...Array(6)].map((_, i) => (
            <div key={i} className="product-skeleton" />
          ))}
        </div>
      ) : products.length === 0 ? (
        <div className="no-results">
          <span className="no-results-icon">&#128532;</span>
          <p>
            {searchQuery
              ? `No encontramos productos para "${searchQuery}".`
              : 'No hay productos en esta categoria.'}
          </p>
          {searchQuery && (
            <button className="tab-btn" onClick={() => setSearchQuery('')}>
              Ver todos
            </button>
          )}
        </div>
      ) : (
        <div className="products-grid">
          {products.map(product => {
            const meta = getCategoryMeta(product.categoryName)
            return (
              <Link
                key={product.id}
                to={`/products/${product.id}`}
                className="product-card"
              >
                <div
                  className="product-image"
                  style={{ backgroundColor: meta.color + '22' }}
                >
                  {product.imageUrl ? (
                    <img src={product.imageUrl} alt={product.name} />
                  ) : (
                    <span className="product-emoji">{meta.emoji}</span>
                  )}
                </div>
                <div className="product-info">
                  <div className="product-top">
                    <h3 className="product-name">{product.name}</h3>
                    <span
                      className="category-badge"
                      style={{ backgroundColor: meta.color + '22', color: meta.color }}
                    >
                      {product.categoryName}
                    </span>
                  </div>
                  <p className="product-price">{formatPrice(product.price)}</p>
                  <span className={`availability-badge${product.available ? ' available' : ' unavailable'}`}>
                    {product.available ? 'Disponible' : 'No disponible'}
                  </span>
                </div>
              </Link>
            )
          })}
        </div>
      )}
    </div>
  )
}
