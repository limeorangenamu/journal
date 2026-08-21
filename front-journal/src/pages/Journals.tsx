import {useState, useCallback, useEffect, type ChangeEvent, type SubmitEvent} from 'react'
import {Link, useNavigate, useSearchParams} from 'react-router'
import JournalCard from '../components/JournalCard'
import type {PageRequestDTO, PageResultDTO, JournalDTO} from '../types'
import {useToken} from '../hooks'
import {authStore} from '../store'

type DivProps = React.ComponentProps<'div'>

export default function Journals({className, ...props}: DivProps) {
  // const token = useToken() // sessionStorage의 값을 가져올 때
  const user = authStore(state => state.user)
  const token = user?.token
  const navigate = useNavigate()
  const [query] = useSearchParams()
  const PAGE_SIZE = 12
  const [pageRequestDTO, setPageRequestDTO] = useState<PageRequestDTO>({
    page: 1,
    size: PAGE_SIZE,
    type: '',
    keyword: ''
  })
  const [pageResultDTO, setPageResultDTO] = useState<PageResultDTO | null>(null)
  const [journalsDTO, setJournalsDTO] = useState<JournalDTO[]>([])

  const [types, setTypes] = useState<string>('')
  const [keywords, setKeywords] = useState<string>('')
  const [disabled, setDisabled] = useState<boolean>(true)
  const options = [
    {value: '', label: '전체 보기'},
    {value: 't', label: '제목'},
    {value: 'c', label: '내용'},
    {value: 'w', label: '작성자'},
    {value: 'tc', label: '제목 + 내용'},
    {value: 'tcw', label: '제목 + 내용 + 작성자'}
  ]

  useEffect(() => {
    const page = query.get('page') || '1'
    const type = query.get('type') || ''
    const keyword = query.get('keyword') || ''

    // 검색창 입력 상태 동기화
    setTypes(type)
    setKeywords(keyword)
    setDisabled(!type) // 검색 조건(type)이 없으면 비활성화

    // API 전송용 쿼리스트링 생성
    const queryParams: string[] = []
    queryParams.push(`page=${page}`)
    queryParams.push(`size=${PAGE_SIZE}`)

    if (type) {
      queryParams.push(`type=${type}`)
    }

    if (keyword) {
      queryParams.push(`keyword=${encodeURIComponent(keyword)}`)
    }

    if (!user?.mid || !token) return

    let url = `http://localhost:8080/api/journal/my-list/${user.mid}`
    if (queryParams.length > 0) {
      url += '?' + queryParams.join('&')
    }

    fetch(url, {method: 'GET', headers: {Authorization: `Bearer ${token}`}})
      .then(res => {
        if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`)
        return res.json()
      })
      .then(data => {
        setPageRequestDTO(data.pageRequestDTO)
        setPageResultDTO(data.pageResultDTO)

        const dtoList = data.pageResultDTO?.dtoList ?? []
        setJournalsDTO(dtoList)
      })
      .catch(err => console.error('Data Fetching Error:', err))
  }, [query, token, user?.mid])

  const handleSearchSubmit = useCallback(
    (e: SubmitEvent) => {
      e.preventDefault()

      if (!types) {
        // 전체 보기 상태일 때는 조건 없이 메인으로 이동
        navigate('/journals')
        return
      }

      if (!keywords.trim()) {
        alert('검색어를 입력해주세요.')
        return
      }
      navigate(`/journals?type=${types}&keyword=${encodeURIComponent(keywords)}&page=1`)
    },
    [types, keywords, navigate]
  )

  const handleSelectChange = useCallback(
    (e: ChangeEvent<HTMLSelectElement>) => {
      const selectedType = e.target.value
      setTypes(selectedType)

      if (!selectedType) {
        // '전체 보기'를 고른 경우 검색창 내용 비우고 비활성화
        setKeywords('')
        setDisabled(true)
        navigate('/journals') // 검색조건 초기화하여 메인으로
      } else {
        setDisabled(false)
      }
    },
    [navigate]
  )

  const goRegister = useCallback(() => {
    const params = new URLSearchParams({
      page: String(pageRequestDTO.page ?? 1),
      size: String(pageRequestDTO.size ?? 12),
      type: pageRequestDTO.type ?? '',
      keyword: pageRequestDTO.keyword ?? ''
    })

    navigate(`/journal-register?${params.toString()}`)
  }, [pageRequestDTO, navigate])

  return (
    <>
      <section className="py-5">
        <div className="container px-5 mb-5">
          <div className="text-center mb-5">
            <h1 className="display-5 fw-bolder mb-0">
              <span className="text-gradient d-inline">My Journal</span>
            </h1>
          </div>
          <div className="d-flex align-items-center justify-content-between mb-4">
            <form className="flex-grow-1 me-3" onSubmit={handleSearchSubmit}>
              <div className="input-group">
                <select
                  id="type"
                  name="type"
                  className="form-select py-3"
                  value={types}
                  style={{maxWidth: 180}}
                  onChange={handleSelectChange}>
                  {options.map((item, idx) => (
                    <option key={idx} value={item.value}>
                      {item.label}
                    </option>
                  ))}
                </select>
                <input
                  className="form-control py-3"
                  value={keywords}
                  type="text"
                  name="keyword"
                  id="keyword"
                  disabled={disabled}
                  onChange={e => setKeywords(e.target.value)}
                  placeholder={
                    disabled ? '검색 조건을 먼저 선택하세요' : '검색어를 입력하세요'
                  }
                />
                <button
                  className="btn btn-outline-primary px-4 py-3"
                  disabled={disabled}
                  type="submit">
                  <div className="d-inline-block bi bi-search me-2"></div>
                  Find
                </button>
              </div>
            </form>
            <button
              className="btn btn-secondary px-4 py-3"
              type="button"
              style={{color: 'white', fontWeight: 'bold'}}
              onClick={goRegister}>
              <div className="d-inline-block bi-plus-circle me-2"></div>
              Register
            </button>
          </div>
          <div className="row gx-5 justify-content-center bg-light pt-3 rounded-4">
            {/* journal start */}
            <div className="bg-light p-3 rounded-4">
              {journalsDTO.length > 0 ? (
                <div className="row row-cols-1 row-cols-sm-2 row-cols-lg-4 g-4">
                  {journalsDTO.map(journal => {
                    const params = new URLSearchParams({
                      page: query.get('page') ?? '1',
                      size: query.get('size') ?? '12',
                      type: query.get('type') ?? '',
                      keyword: query.get('keyword') ?? ''
                    })

                    return (
                      <div className="col" key={journal.jno}>
                        <Link
                          to={`/journals/${journal.jno}?${params.toString()}`}
                          className="card h-100 overflow-hidden shadow-sm rounded-4 border-0 text-decoration-none text-reset">
                          <JournalCard journal={journal} />
                        </Link>
                      </div>
                    )
                  })}
                </div>
              ) : (
                <div className="text-center py-5 text-muted">
                  등록된 Journal이 없습니다. 첫 이야기를 작성해 보세요!
                </div>
              )}

              <div className="d-flex justify-content-center align-items-center mt-5 w-100">
                <ul className="pagination mb-0">
                  {pageResultDTO?.prev && (
                    <li className="page-item">
                      <a
                        className="page-link"
                        href={`/journals?page=${Math.max(1, pageResultDTO.start - 1)}&type=${
                          query.get('type') ?? ''
                        }&keyword=${query.get('keyword') ?? ''}`}>
                        Prev
                      </a>
                    </li>
                  )}
                  {pageResultDTO?.pageList.map(page => (
                    <li
                      key={page}
                      className={`page-item ${pageResultDTO?.page === page ? 'active' : ''}`}>
                      <a
                        className="page-link"
                        href={`/journals?page=${page ?? ''}&type=${query.get('type') ?? ''}&keyword=${
                          query.get('keyword') ?? ''
                        }`}>
                        {page}
                      </a>
                    </li>
                  ))}
                  {pageResultDTO?.next ? (
                    <li className="page-item">
                      <a
                        className="page-link"
                        href={`/journals?page=${pageResultDTO.end + 1}&type=${
                          query.get('type') ?? ''
                        }&keyword=${query.get('keyword') ?? ''}`}>
                        Next
                      </a>
                    </li>
                  ) : null}
                </ul>
              </div>
            </div>
            {/* journal end */}
          </div>
        </div>
      </section>
      <section className="py-5 bg-gradient-primary-to-secondary text-white">
        <div className="container px-5 my-5">
          <div className="text-center">
            <h2 className="display-4 fw-bolder mb-4">
              Let's Connect And Share Journals.
            </h2>
            <a className="btn btn-outline-light btn-lg px-5 py-3 fs-6 fw-bolder" href="#">
              Top
            </a>
          </div>
        </div>
      </section>
    </>
  )
}
