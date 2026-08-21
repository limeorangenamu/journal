import {useEffect, useState} from 'react'
import {Link} from 'react-router'
import JournalCard from '../components/JournalCard'
import {authStore} from '../store'
import type {JournalDTO} from '../types'

export default function Community() {
  const token = authStore(state => state.user?.token)
  const [journals, setJournals] = useState<JournalDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) return

    fetch('http://localhost:8080/api/journal/community/list?page=1&size=12', {
      headers: {Authorization: `Bearer ${token}`}
    })
      .then(async response => {
        if (!response.ok) {
          throw new Error(`공개 기록을 불러오지 못했습니다. (${response.status})`)
        }
        return response.json()
      })
      .then(data => setJournals(data.pageResultDTO?.dtoList ?? []))
      .catch(caught => {
        setError(caught instanceof Error ? caught.message : '오류가 발생했습니다.')
      })
      .finally(() => setLoading(false))
  }, [token])

  return (
    <section className="py-5">
      <div className="container px-5">
        <div className="text-center mb-5">
          <p className="text-uppercase text-primary fw-semibold mb-2">Public stories</p>
          <h1 className="display-5 fw-bolder mb-3">
            <span className="text-gradient">Community</span>
          </h1>
          <p className="lead text-muted mb-0">다른 사람들이 공개한 기록을 발견해 보세요.</p>
        </div>

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border" />
            <p className="mt-3 text-muted">기록을 불러오는 중입니다.</p>
          </div>
        ) : error ? (
          <div className="alert alert-danger">{error}</div>
        ) : journals.length === 0 ? (
          <div className="text-center bg-light rounded-4 py-5 text-muted">
            아직 공개된 기록이 없습니다.
          </div>
        ) : (
          <div className="row row-cols-1 row-cols-sm-2 row-cols-lg-3 g-4">
            {journals.map(journal => (
              <div className="col" key={journal.jno}>
                <Link
                  to={`/journals/${journal.jno}`}
                  className="card h-100 overflow-hidden shadow-sm rounded-4 border-0 text-decoration-none text-reset">
                  <JournalCard journal={journal} />
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  )
}
