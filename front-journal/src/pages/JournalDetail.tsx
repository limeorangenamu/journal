import {useEffect, useRef, useState, type ChangeEvent} from 'react'
import {useLoaderData, useNavigate, useSearchParams} from 'react-router'
import type {JournalDTO, PhotosDTO} from '../types'
import {authStore} from '../store'

type DetailResponse = {
  journalDTO: JournalDTO
}

type UploadResult = {
  uuid: string
  fileName: string
  folderPath: string
  imageURL: string
  thumbnailURL: string
}

type EditablePhoto = PhotosDTO & {
  isNew: boolean
}

type RemoveResponse = {
  jno: number
  page: number
  type: string | null
  keyword: string | null
  message: string
}

export default function JournalDetail() {
  const loaderData = useLoaderData() as {
    id: string | null
  }

  const navigate = useNavigate()
  const [query] = useSearchParams()

  const token = authStore(state => state.user?.token)
  const user = authStore(state => state.user)

  const jno = Number(loaderData.id)

  const [journal, setJournal] = useState<JournalDTO | null>(null)

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  // false이면 상세보기, true이면 수정 화면입니다.
  const [editing, setEditing] = useState(false)

  const [editTitle, setEditTitle] = useState('')
  const [editContent, setEditContent] = useState('')

  const refEditFile = useRef<HTMLInputElement | null>(null)

  const [editPhotos, setEditPhotos] = useState<EditablePhoto[]>([])

  useEffect(() => {
    const loadJournal = async () => {
      if (!token) {
        navigate('/login', {replace: true})
        return
      }

      if (!Number.isInteger(jno) || jno <= 0) {
        setError('올바르지 않은 Journal 번호입니다.')
        setLoading(false)
        return
      }

      try {
        const response = await fetch(`http://localhost:8080/api/journal/read/${jno}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`
          }
        })

        const data = (await response.json()) as DetailResponse

        if (!response.ok) {
          throw new Error(`상세 조회 실패: ${response.status}`)
        }

        setJournal(data.journalDTO)
        setEditTitle(data.journalDTO.title)
        setEditContent(data.journalDTO.content)

        setEditPhotos(
          (data.journalDTO.photosDTOList ?? []).map(photo => ({
            ...photo,
            isNew: false
          }))
        )
      } catch (caught) {
        console.error('상세 조회 오류:', caught)

        setError(
          caught instanceof Error ? caught.message : 'Journal을 불러오지 못했습니다.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadJournal()
  }, [jno, token, navigate])

  const makeListUrl = () => {
    const params = new URLSearchParams({
      page: query.get('page') ?? '1',
      type: query.get('type') ?? '',
      keyword: query.get('keyword') ?? ''
    })

    return `/journals?${params.toString()}`
  }

  const startEdit = () => {
    if (!journal) return

    setEditTitle(journal.title)
    setEditContent(journal.content)

    setEditPhotos(
      journal.photosDTOList.map(photo => ({
        ...photo,
        isNew: false
      }))
    )

    setEditing(true)
  }

  const checkPhoto = (fileName: string, fileSize: number) => {
    const maxSize = 10 * 1024 * 1024

    if (fileSize > maxSize) {
      alert('사진 한 장의 크기는 최대 10MB까지 가능합니다.')
      return false
    }

    const imageExtension = /\.(jpg|jpeg|png|gif|bmp|webp)$/i

    if (!imageExtension.test(fileName)) {
      alert('jpg, jpeg, png, gif, bmp, webp 사진만 올릴 수 있습니다.')
      return false
    }

    return true
  }

  const uploadEditPhotos = async (e: ChangeEvent<HTMLInputElement>) => {
    if (!token) {
      alert('로그인이 필요합니다.')
      return
    }

    const files = Array.from(e.target.files ?? [])

    if (files.length === 0) return

    for (const file of files) {
      if (!checkPhoto(file.name, file.size)) {
        e.target.value = ''
        return
      }
    }

    const formData = new FormData()

    files.forEach(file => {
      formData.append('uploadFiles', file)
    })

    try {
      const response = await fetch('http://localhost:8080/api/uploadAjax', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`
        },
        body: formData
      })

      if (!response.ok) {
        throw new Error(`사진 업로드에 실패했습니다: ${response.status}`)
      }

      const uploadedPhotos = (await response.json()) as UploadResult[]

      const newPhotos: EditablePhoto[] = uploadedPhotos.map(photo => ({
        uuid: photo.uuid,
        photosName: photo.fileName,
        path: photo.folderPath,
        getPhotosURL: photo.imageURL,
        getThumbnailURL: photo.thumbnailURL,
        isNew: true
      }))

      setEditPhotos(previousPhotos => [...previousPhotos, ...newPhotos])
    } catch (caught) {
      console.error('사진 업로드 오류:', caught)

      alert(
        caught instanceof Error ? caught.message : '사진 업로드 중 오류가 발생했습니다.'
      )
    } finally {
      if (refEditFile.current) {
        refEditFile.current.value = ''
      }
    }
  }

  const deleteTemporaryPhotoFile = async (photo: EditablePhoto) => {
    if (!token || !photo.getPhotosURL) return

    const params = new URLSearchParams({
      fileName: photo.getPhotosURL
    })

    const response = await fetch(
      `http://localhost:8080/api/removeFile?${params.toString()}`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    )

    if (!response.ok) {
      throw new Error(`사진 파일 삭제에 실패했습니다: ${response.status}`)
    }
  }

  const removeEditPhoto = async (targetPhoto: EditablePhoto) => {
    const confirmed = window.confirm(`${targetPhoto.photosName} 사진을 제거하시겠습니까?`)

    if (!confirmed) return

    try {
      // 수정 화면에서 새로 올린 사진은 서버에 임시 파일이 생긴 상태입니다.
      // 따라서 목록에서 지울 때 실제 파일도 바로 지웁니다.
      if (targetPhoto.isNew) {
        await deleteTemporaryPhotoFile(targetPhoto)
      }

      // 기존 사진은 여기서 서버 파일을 바로 지우지 않습니다.
      // 수정 완료를 눌렀을 때 백엔드가 실제로 삭제합니다.
      setEditPhotos(previousPhotos =>
        previousPhotos.filter(photo => photo.uuid !== targetPhoto.uuid)
      )
    } catch (caught) {
      console.error('사진 제거 오류:', caught)

      alert(
        caught instanceof Error
          ? caught.message
          : '사진을 제거하는 중 오류가 발생했습니다.'
      )
    }
  }

  const cancelEdit = async () => {
    if (!journal) return

    // 수정 중 새로 올린 사진은 아직 Journal에 등록되지 않은 임시 파일입니다.
    const temporaryPhotos = editPhotos.filter(photo => photo.isNew)

    const deleteResults = await Promise.allSettled(
      temporaryPhotos.map(photo => deleteTemporaryPhotoFile(photo))
    )

    const hasDeleteFailure = deleteResults.some(result => result.status === 'rejected')

    if (hasDeleteFailure) {
      alert('일부 임시 사진 파일을 삭제하지 못했습니다.')
    }

    setEditTitle(journal.title)
    setEditContent(journal.content)

    setEditPhotos(
      journal.photosDTOList.map(photo => ({
        ...photo,
        isNew: false
      }))
    )

    if (refEditFile.current) {
      refEditFile.current.value = ''
    }

    setEditing(false)
  }

  const modifyJournal = async () => {
    if (!journal || !token) return

    const title = editTitle.trim()
    const content = editContent.trim()

    if (!title) {
      alert('제목을 입력하세요.')
      return
    }

    if (!content) {
      alert('내용을 입력하세요.')
      return
    }

    const requestData = {
      jno: journal.jno,
      title,
      content,

      // 사진을 수정하지 않을 때도 기존 사진 목록을 보냅니다.
      // 보내지 않으면 서버가 모든 사진을 삭제할 수 있습니다.
      photosDTOList: editPhotos.map(photo => ({
        uuid: photo.uuid,
        photosName: photo.photosName,
        path: photo.path
      }))
    }

    try {
      const response = await fetch('http://localhost:8080/api/journal/modify', {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(requestData)
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || `수정 실패: ${response.status}`)
      }

      const savedPhotos: PhotosDTO[] = editPhotos.map(photo => ({
        uuid: photo.uuid,
        photosName: photo.photosName,
        path: photo.path,
        getPhotosURL: photo.getPhotosURL,
        getThumbnailURL: photo.getThumbnailURL
      }))

      setJournal({
        ...journal,
        title,
        content,
        photosDTOList: savedPhotos
      })

      setEditPhotos(
        savedPhotos.map(photo => ({
          ...photo,
          isNew: false
        }))
      )

      setEditing(false)
      alert(data.msg || 'Journal이 수정되었습니다.')
    } catch (caught) {
      console.error('수정 오류:', caught)

      alert(caught instanceof Error ? caught.message : '수정 중 오류가 발생했습니다.')
    }
  }

  const removeJournal = async () => {
    if (!journal || !token) return

    const confirmed = window.confirm('정말 이 Journal을 삭제하시겠습니까?')

    if (!confirmed) return

    const pageRequestDTO = {
      page: Number(query.get('page') ?? 1),
      size: Number(query.get('size') ?? 12),
      type: query.get('type') ?? '',
      keyword: query.get('keyword') ?? ''
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/journal/remove/${journal.jno}`,
        {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json;charset=utf-8'
          },

          // 현재 Spring Controller가 @RequestBody를 요구합니다.
          body: JSON.stringify(pageRequestDTO)
        }
      )

      const data = (await response.json()) as RemoveResponse

      if (!response.ok) {
        throw new Error(data.message || `삭제 실패: ${response.status}`)
      }

      alert(data.message)

      const params = new URLSearchParams({
        page: String(data.page),
        type: data.type ?? '',
        keyword: data.keyword ?? ''
      })

      navigate(`/journals?${params.toString()}`)
    } catch (caught) {
      console.error('삭제 오류:', caught)

      alert(caught instanceof Error ? caught.message : '삭제 중 오류가 발생했습니다.')
    }
  }

  if (loading) {
    return (
      <section className="container py-5 text-center">
        <div className="spinner-border" />
        <p className="mt-3">Journal을 불러오는 중입니다.</p>
      </section>
    )
  }

  if (error || !journal) {
    return (
      <section className="container py-5">
        <div className="alert alert-danger">{error || 'Journal을 찾을 수 없습니다.'}</div>

        <button className="btn btn-outline-dark" onClick={() => navigate(makeListUrl())}>
          목록으로
        </button>
      </section>
    )
  }

  // 현재 로그인 사용자와 글 작성자가 같은지 확인합니다.
  const isOwner = user?.mid === journal.membersDTO.mid

  return (
    <section className="container py-5">
      <article className="mx-auto" style={{maxWidth: 800}}>
        {editing ? (
          <>
            <div className="mb-3">
              <label className="form-label" htmlFor="editTitle">
                제목
              </label>

              <input
                id="editTitle"
                className="form-control"
                value={editTitle}
                onChange={e => setEditTitle(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label className="form-label" htmlFor="editContent">
                내용
              </label>

              <textarea
                id="editContent"
                className="form-control"
                style={{height: 250}}
                value={editContent}
                onChange={e => setEditContent(e.target.value)}
              />
            </div>
            <div className="mb-4">
              <label className="form-label fw-bold" htmlFor="editPhotoInput">
                사진 추가
              </label>

              <input
                ref={refEditFile}
                id="editPhotoInput"
                type="file"
                accept="image/*"
                multiple
                className="form-control"
                onChange={uploadEditPhotos}
              />

              

              {editPhotos.length > 0 ? (
                <div className="row row-cols-2 row-cols-md-4 g-3 mt-1">
                  {editPhotos.map((photo, index) => {
                    const previewURL = photo.getThumbnailURL ?? photo.getPhotosURL

                    return (
                      <div className="col" key={photo.uuid}>
                        <div className="position-relative">
                          <div className="register-photo-frame">
                            {previewURL ? (
                              <img
                                className="register-photo-preview"
                                src={
                                  'http://localhost:8080/api/display?fileName=' +
                                  encodeURIComponent(previewURL)
                                }
                                alt={`${photo.photosName} 미리보기 ${index + 1}`}
                              />
                            ) : (
                              <span className="text-muted small">
                                미리보기가 없습니다.
                              </span>
                            )}
                          </div>

                          <button
                            type="button"
                            className="btn btn-sm btn-danger position-absolute top-0 end-0"
                            style={{zIndex: 2}}
                            onClick={() => removeEditPhoto(photo)}
                            aria-label={`${photo.photosName} 제거`}>
                            ×
                          </button>

                          <p
                            className="small text-center text-truncate mt-1 mb-0"
                            title={photo.photosName}>
                            {photo.photosName}
                          </p>

                          {photo.isNew}
                        </div>
                      </div>
                    )
                  })}
                </div>
              ) : (
                <div className="alert alert-secondary mt-3 mb-0">
                  사진이 없습니다.
                </div>
              )}
            </div>
            <div className="d-flex gap-2">
              <button type="button" className="btn btn-primary" onClick={modifyJournal}>
                수정 완료
              </button>

              <button type="button" className="btn btn-secondary" onClick={cancelEdit}>
                취소
              </button>
            </div>
          </>
        ) : (
          <>
            <h1 className="mb-3">{journal.title}</h1>

            <p className="text-secondary border-bottom pb-3">
              작성자: {journal.membersDTO.name}
            </p>

            {journal.photosDTOList.length > 0 && (
              <div className="row row-cols-1 row-cols-sm-2 row-cols-lg-3 g-3 mb-4 justify-content-center">
                {journal.photosDTOList.map((photo, index) => (
                  <div className="col" key={photo.uuid}>
                    <div className="journal-detail-photo-frame">
                      {photo.getPhotosURL ? (
                        <img
                          className="journal-detail-photo"
                          src={`http://localhost:8080/api/display?fileName=${photo.getPhotosURL}`}
                          alt={`${journal.title} 사진 ${index + 1}`}
                        />
                      ) : (
                        <span className="text-muted">사진을 불러올 수 없습니다.</span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}

            <div style={{whiteSpace: 'pre-wrap'}}>{journal.content}</div>

            <div className="d-flex gap-3 mt-4">
              <span>좋아요: {journal.likes}</span>
              <span>댓글: {journal.commentsCnt}</span>
            </div>

            <div className="d-flex gap-2 mt-4">
              <button
                type="button"
                className="btn btn-outline-dark"
                onClick={() => navigate(makeListUrl())}>
                목록으로
              </button>

              {isOwner && (
                <>
                  <button type="button" className="btn btn-primary" onClick={startEdit}>
                    수정
                  </button>

                  <button
                    type="button"
                    className="btn btn-danger"
                    onClick={removeJournal}>
                    삭제
                  </button>
                </>
              )}
            </div>
          </>
        )}
      </article>
    </section>
  )
}
