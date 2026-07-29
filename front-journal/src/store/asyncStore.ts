import {create} from 'zustand'

type User = {
  id: number
  name: string
}

type UserStore = {
  users: User[]
  loading: boolean

  fetchUsers: () => Promise<void>
}

export const useAsyncStore = create<UserStore>(set => ({
  users: [],
  loading: false,
  fetchUsers: async () => {
    set({users: [], loading: true})
    const response = await fetch('https://jsonplaceholder.typicode.com/users')
    const users = await response.json()
    set({
      users: users,
      loading: false
    })
  }
}))
