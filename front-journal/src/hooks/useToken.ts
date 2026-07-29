import {useState, useEffect} from 'react'

export const useToken = () => {
  const [token, setToken] = useState<string | null>(null)

  useEffect(() => {
    const authStorage = sessionStorage.getItem('auth-storage')

    if (authStorage) {
      try {
        const parsed = JSON.parse(authStorage)
        setToken(parsed.token ?? null)
      } catch (error) {
        console.error('auth-storage 파싱 오류', error)
        setToken(null)
      }
    } else {
      setToken(null)
    }
  }, [])

  return token
}

/* sessionStorage의 token으로부터 바로 값을 가져올 때
import {useState, useEffect} from 'react'

export const useToken = () => {
  const [token, setToken] = useState<string | null>(null)

  useEffect(() => {
    const sessionToken = sessionStorage.getItem('token')
    if (sessionToken) {
      setToken(sessionToken)
    } else {
      setToken(null)
    }
  }, [])
  return token
}


*/
