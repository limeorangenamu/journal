import {useCallback, useRef, useState} from 'react'
import {useNavigate, useSearchParams} from 'react-router'
import type {SubmitEvent, ChangeEvent} from 'react'
import type {PhotosDTO} from '../types'
import {authStore} from '../store'

export default function JournalRegister() {
  const user = authStore(state => state.user)
  const token = authStore(state => state.user?.token)
  const [query] = useSearchParams()
  const navigate = useNavigate()

  const refFile = useRef<HTMLInputElement | null>(null)
  const refTitle = useRef<HTMLInputElement | null>(null)
  const refContent = useRef<HTMLTextAreaElement | null>(null)

  const [photos, setPhotos] = useState<PhotosDTO[]>([])
  const [isPublic, setIsPublic] = useState(true)

  const checkExtension = useCallback((fileName: string, fileSize: number) => {
    const maxSize = 1024 * 1024 * 10
    if (fileSize >= maxSize) {
      alert('파일사이즈 초과 (최대 10MB)')
      return false
    }
    const regex = new RegExp('(.*?)\\.(jpg|jpeg|png|gif|bmp|webp)$', 'i')
    if (!regex.test(fileName)) {
      alert('해당 파일 형식은 업로드할 수 없습니다.')
      return false
    }
    return true
  }, [])
  const fileChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      const fileList = e.target.files
      if (!fileList || fileList.length === 0) return

      const formData = new FormData()
      for (const file of fileList) {
        if (!checkExtension(file.name, file.size)) {
          e.target.value = ''
          return
        }
        formData.append('uploadFiles', file)
      }
      if (token) {
        fetch('http://localhost:8080/api/uploadAjax', {
          method: 'POST',
          body: formData,
          headers: {Authorization: `Bearer ${token}`}
        })
          .then(res => res.json())
          .then((json: any[]) => {
            const mapped: PhotosDTO[] = json.map(item => ({
              uuid: item.uuid,
              photosName: item.fileName,
              path: item.folderPath,
              getThumbnailURL: item.thumbnailURL,
              getPhotosURL: item.imageURL
            }))
            setPhotos(prev => [...prev, ...mapped])
          })
          .catch(console.error)
      }
    },
    [checkExtension]
  )

  const handleRemove = useCallback((target: PhotosDTO) => {
    if (!token) return
    const removeUrl = `http://localhost:8080/api/removeFile?fileName=${target.getPhotosURL}`
    fetch(removeUrl, {
      method: 'POST',
      headers: {Authorization: `Bearer ${token}`}
    })
      .then(res => res.json())
      .then(json => {
        if (json === true) {
          setPhotos(prev => prev.filter(p => p.uuid !== target.uuid))
          if (refFile.current) refFile.current.value = ''
        }
      })
      .catch(console.error)
  }, [])

  const journalSubmit = useCallback(
    async (e: SubmitEvent<HTMLFormElement>) => {
      e.preventDefault()

      if (!token || !user?.mid) {
        alert('로그인이 필요합니다.')
        navigate('/login')
        return
      }

      const title = refTitle.current?.value.trim()
      const content = refContent.current?.value.trim()

      if (!title) {
        alert('제목을 입력하세요.')
        refTitle.current?.focus()
        return
      }

      if (!content) {
        alert('내용을 입력하세요.')
        refContent.current?.focus()
        return
      }

      const requestData = {
        title,
        content,
        isPublic,
        membersDTO: {
          mid: user.mid
        },

        // 서버에 필요한 사진 정보만 보냅니다.
        photosDTOList: photos.map(photo => ({
          uuid: photo.uuid,
          photosName: photo.photosName,
          path: photo.path
        }))
      }

      try {
        const response = await fetch('http://localhost:8080/api/journal/register', {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json;charset=utf-8'
          },
          body: JSON.stringify(requestData)
        })

        const responseText = await response.text()

        if (!response.ok) {
          throw new Error(responseText || `등록 실패: ${response.status}`)
        }

        // 서버가 새 Journal 번호를 반환합니다.
        const newJno = Number(responseText)

        const params = new URLSearchParams({
          page: query.get('page') ?? '1',
          type: query.get('type') ?? '',
          keyword: query.get('keyword') ?? ''
        })

        alert('Journal이 등록되었습니다.')

        // 등록된 글의 상세 페이지로 이동합니다.
        // navigate(`/journals/${newJno}?${params.toString()}`)

        // 저널 페이지로 이동
        navigate(`/journals`)
      } catch (error) {
        console.error('Journal 등록 오류:', error)

        alert(
          error instanceof Error ? error.message : 'Journal 등록 중 오류가 발생했습니다.'
        )
      }
    },
    [token, user?.mid, photos, query, navigate, isPublic]
  )
  return (
    <>
      <section className="py-3">
        <div className="container px-5">
          <div className="text-center mb-3">
            <h1 className="display-5 fw-bolder mb-0">
              <span className="text-gradient d-inline">Journals</span>
            </h1>
          </div>
          <div className="row gx-5 justify-content-center bg-light pt-3 rounded-4">
            <h3 style={{marginBottom: '30px'}}>Journal 등록</h3>
            <form onSubmit={journalSubmit}>
              <div className="form-floating mb-3">
                <input
                  ref={refTitle}
                  id="title"
                  name="title"
                  type="text"
                  className="form-control"
                  placeholder="Input Title"
                />
                <label htmlFor="title">Title</label>
              </div>
              <div className="form-floating mb-3">
                <textarea
                  ref={refContent}
                  id="content"
                  name="content"
                  className="form-control"
                  placeholder="Enter your Content here..."
                  style={{height: '15rem'}}
                />
                <label htmlFor="content">Content</label>
              </div>

              <div className="mb-3">
                <label className="form-label fw-semibold">공개 설정</label>
                <div className="form-check">
                  <input
                    className="form-check-input"
                    id="isPublic"
                    type="checkbox"
                    checked={isPublic}
                    onChange={e => setIsPublic(e.target.checked)}
                  />
                  <label className="form-check-label" htmlFor="isPublic">
                    Community에 공개하기
                  </label>
                </div>
                <div className="form-text">해제한 기록은 나만 볼 수 있습니다.</div>
              </div>

              <div className="form-floating mb-3">
                <input
                  ref={refFile}
                  id="fileInput"
                  type="file"
                  accept="image/*"
                  multiple
                  className="file-input w-full"
                  onChange={fileChange}
                />
              </div>

              <div className="uploadResult mb-4">
                {photos.length > 0 && (
                  <>
                    <p className="fw-bold mb-2">업로드한 사진 미리보기</p>

                    <div className="row row-cols-2 row-cols-md-4 row-cols-lg-6 g-3">
                      {photos.map(photo => (
                        <div className="col" key={photo.uuid}>
                          <div className="position-relative">
                            <div className="register-photo-frame">
                              {photo.getThumbnailURL ? (
                                <img
                                  className="register-photo-preview"
                                  src={`http://localhost:8080/api/display?fileName=${photo.getThumbnailURL}`}
                                  alt={photo.photosName}
                                />
                              ) : (
                                <span className="text-muted small">미리보기 없음</span>
                              )}
                            </div>

                            <button
                              type="button"
                              className="btn btn-sm btn-danger position-absolute top-0 end-0"
                              style={{zIndex: 2}}
                              onClick={() => handleRemove(photo)}
                              aria-label={`${photo.photosName} 삭제`}>
                              ×
                            </button>

                            <p
                              className="small text-center text-truncate mt-1 mb-0"
                              title={photo.photosName}>
                              {photo.photosName}
                            </p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </>
                )}
              </div>
              <div className="d-none"></div>
              <button className="btn btn-primary text-uppercase p-1" type="submit">
                등록
              </button>
            </form>
          </div>
        </div>
      </section>
    </>
  )
}
