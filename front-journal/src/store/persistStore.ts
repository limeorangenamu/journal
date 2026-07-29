import {create} from 'zustand'
import {persist} from 'zustand/middleware'

type Store = {
  darkMode: boolean
  toggle: () => void
}

export const usePersistStore = create<Store>()(
  persist(
    set => ({
      darkMode: false,
      toggle: () => set(state => ({darkMode: !state.darkMode}))
    }),
    {name: 'theme-storage'} // 초기값
  )
)
