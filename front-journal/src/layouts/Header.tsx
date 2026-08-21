import {NavLink, Link} from 'react-router'
import {authStore} from '../store'
import {useToggle} from '../hooks'
import {useCallback} from 'react'

type DivProps = React.ComponentProps<'div'>

export default function Header({className, ...props}: DivProps) {
  const user = authStore(state => state.user)
  const logoutAction = authStore(state => state.logoutAction)
  const [open, toggleOpen] = useToggle(false)
  const onLogout = useCallback(() => {
    logoutAction()
    toggleOpen()
  }, [])

  return (
    <>
      <nav className="navbar navbar-expand-lg navbar-light bg-white py-3">
        <div className="container px-5">
          {/* 로고는 활성화 표시가 필요 없으므로 일반 Link 사용 */}
          <Link className="navbar-brand" to="/">
            <span className="fw-bolder text-primary">journal</span>
          </Link>
          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent"
            aria-expanded="false"
            aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className="collapse navbar-collapse" id="navbarSupportedContent">
            <ul className="navbar-nav ms-auto mb-2 mb-lg-0 small fw-bolder">
              <li className="nav-item">
                {/* end 속성을 주어 정확히 메인("/")일 때만 active 클래스가 켜지도록 합니다 */}
                <NavLink className="nav-link" to="/" end>
                  Home
                </NavLink>
              </li>
              <li className="nav-item">
                <NavLink className="nav-link" to="/community">
                  Community
                </NavLink>
              </li>
              <li className="nav-item">
                <NavLink className="nav-link" to="/journals">
                  Journals
                </NavLink>
              </li>
              {!user ? (
                <>
                  <li className="nav-item">
                    <NavLink className="nav-link" to="/join">
                      회원가입
                    </NavLink>
                  </li>
                  <li className="nav-item">
                    <NavLink className="nav-link" to="/login">
                      로그인
                    </NavLink>
                  </li>
                </>
              ) : (
                <li className="nav-item">
                  <span
                    className="nav-link"
                    style={{cursor: 'pointer'}}
                    onClick={toggleOpen}>
                    로그아웃
                  </span>
                </li>
              )}
            </ul>
          </div>
        </div>
      </nav>
      {/* --- Bootstrap 5 전용 로그아웃 확인 모달 --- */}
      {open && (
        <>
          <div
            className="modal fade show"
            style={{display: 'block', backgroundColor: 'rgba(0,0,0,0.5)'}}
            tabIndex={-1}
            onClick={toggleOpen} // 모달 바깥 영역 클릭 시 닫기
          >
            <div
              className="modal-dialog modal-dialog-centered"
              onClick={e => e.stopPropagation()}>
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">로그아웃 확인</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={toggleOpen}
                    aria-label="Close"></button>
                </div>
                <div className="modal-body">
                  <p className="mb-0">정말로 로그아웃 하시겠습니까?</p>
                </div>
                <div className="modal-header border-0 pb-3 pt-0 d-flex justify-content-end gap-2">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={toggleOpen}>
                    취소
                  </button>
                  <button type="button" className="btn btn-primary" onClick={onLogout}>
                    로그아웃
                  </button>
                </div>
              </div>
            </div>
          </div>
          {/* 모달이 떴을 때 배경을 어둡게 처리하는 Bootstrap 백드롭 효과 */}
          <div className="modal-backdrop fade show"></div>
        </>
      )}
    </>
  )
}
