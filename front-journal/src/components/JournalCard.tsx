import type {JournalDTO} from '../types'

interface JournalCardProps {
  journal: JournalDTO
  showViews?: boolean
}

export default function JournalCard({journal, showViews = false}: JournalCardProps) {
  const firstPhoto = journal.photosDTOList?.[0]

  const coverUrl = firstPhoto?.getPhotosURL
    ? `http://localhost:8080/api/display?fileName=${firstPhoto.getPhotosURL}`
    : null

  return (
    <>
      <div className="journal-card-cover">
        {coverUrl ? (
          <img
            className="journal-card-cover-image"
            src={coverUrl}
            alt={`${journal.title} 대표 사진`}
          />
        ) : (
          <span className="journal-card-no-image">등록된 사진이 없습니다.</span>
        )}
      </div>

      <div className="card-body d-flex align-items-center justify-content-center text-center p-3">
        <div>
          <h2 className="h5 fw-bold journal-card-title mb-1">{journal.title}</h2>
          <p className="small text-muted mb-0">{journal.membersDTO.nickname || journal.membersDTO.name}</p>
          <div className="d-flex align-items-center justify-content-center gap-2 mt-2">
            <span className={`badge ${journal.isPublic ? 'text-bg-primary' : 'text-bg-secondary'}`}>
              {journal.isPublic ? '공개' : '비공개'}
            </span>
            {showViews && <span className="small text-muted">조회 {journal.views ?? 0}</span>}
          </div>
        </div>
      </div>
    </>
  )
}
