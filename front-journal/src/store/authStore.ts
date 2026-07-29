import {create} from 'zustand'
import {createJSONStorage, persist} from 'zustand/middleware'

type User = {
  mid: number
  email: string
  name: string
  token: string
}

type AuthState = {
  user: User | null
  login: boolean
  loginAction: (user: User) => void
  logoutAction: () => void
}

export const authStore = create<AuthState>()(
  persist(
    set => ({
      user: null,
      login: false,
      loginAction(user) {
        set({user, login: true})
      },
      logoutAction() {
        set({user: null, login: false})
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('auth-storage')
      }
    }),
    {name: 'auth-storage', storage: createJSONStorage(() => sessionStorage)}
  )
)
