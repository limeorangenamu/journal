import {create} from 'zustand'

type Todo = {
  id: number
  text: string
}

type TodoStore = {
  todos: Todo[]
  addTodo: (text: string) => void
  removeTodo: (id: number) => void
}

// set :: zustand에서 제공하는 상태 변경 함수(라이브러리)
// create<T>((set, get) => {})
// 변수일 때 set({ 상태명: 새로운 값 }) :: set({ todos:[] }) :: todos = []
// 함수일 때 set(state => ({ todos:[...기존값, 새로운 값] })) :: set(state=>({todos:[...state.todos, newTodo]}))
export const useTodoStore = create<TodoStore>(set => ({
  todos: [],
  addTodo: text =>
    set(state => ({
      todos: [
        ...state.todos, // 기존 값
        {
          id: Date.now(), // 새로운 값
          text
        }
      ]
    })),
  removeTodo: id =>
    set(state => ({
      todos: state.todos.filter(todo => todo.id !== id)
    }))
}))
