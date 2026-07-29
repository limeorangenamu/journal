import {createBrowserRouter, Navigate} from 'react-router'
import {lazy, Suspense} from 'react'

import MainLayout from '../layouts/MainLayout'
import Home from '../pages/Home'
import Join from '../pages/Join'
import Login from '../pages/Login'
import NotFound from '../pages/NotFound'
import {authStore} from '../store'
import JournalRegister from '../pages/JournalRegister'

const Community = lazy(() => import('../pages/Community'))
const Journals = lazy(() => import('../pages/Journals'))
const JournalDetail = lazy(() => import('../pages/JournalDetail'))

function ProtectedRoute({children}: {children: React.ReactNode}) {
  const login = authStore(state => state.login)
  if (!login) return <Navigate to="/login" replace />
  return children
}

export const router = createBrowserRouter([
  {
    element: <MainLayout />,
    children: [
      {
        index: true,
        element: <Home />
      },
      {
        path: 'community',
        element: (
          <ProtectedRoute>
            <Suspense
              fallback={<button className="btn btn-circle loading">Loading...</button>}>
              <Community />
            </Suspense>
          </ProtectedRoute>
        )
      },
      {
        path: 'journals',
        element: (
          <ProtectedRoute>
            <Suspense
              fallback={<button className="btn btn-circle loading">Loading...</button>}>
              <Journals />
            </Suspense>
          </ProtectedRoute>
        )
      },
      {
        path: 'journals/:id',
        loader: async ({params}) => {
          return {
            id: params.id
          }
        },
        element: (
          <ProtectedRoute>
            <Suspense
              fallback={<button className="btn btn-circle loading">Loading...</button>}>
              <JournalDetail />
            </Suspense>
          </ProtectedRoute>
        )
      },
      {
        path: '*',
        element: <NotFound />
      },
      {
        path: 'login',
        element: <Login />
      },
      {
        path: 'journal-register',
        element: (
          <ProtectedRoute>
            <JournalRegister />
          </ProtectedRoute>
        )
      },
      {
        path: 'join',
        element: <Join />
      }
    ]
  }
])
