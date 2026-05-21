import './Footer.css'

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-inner">
        <p>🍔 no se &copy; {new Date().getFullYear()}</p>
        <p className="footer-sub">Sabor artesanal, calidad garantizada</p>
      </div>
    </footer>
  )
}
