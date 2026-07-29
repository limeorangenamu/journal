import {create} from 'zustand'

type User = {
  id: number
  name: string
}

type AuthStore = {
  user: User | null
  login: (user: User) => void
  logout: () => void
}

export const useAuthStore = create<AuthStore>(set => ({
  user: null,
  login: user => set({user}),
  logout: () => set({user: null})
}))
