import Header from './Header'
import Footer from './Footer'
import {Outlet} from 'react-router'

export default function MainLayout() {
  return (
    <div className="d-flex flex-column h-100">
      <main className="flex-shrink-0">
        <Header />
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}
