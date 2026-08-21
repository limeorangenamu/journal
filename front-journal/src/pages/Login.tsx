import {useState, useRef, useCallback} from 'react'
import {useNavigate} from 'react-router'
import {authStore} from '../store'

type DivProps = React.ComponentProps<'div'>

export default function Login({className, ...props}: DivProps) {
  const navigate = useNavigate()
  const loginAction = authStore(state => state.loginAction)

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string>('')
  const [loginFail, setLoginFail] = useState<boolean>(false)

  const refPass = useRef<HTMLInputElement>(null)
  const refEmail = useRef<HTMLInputElement>(null)

  const login = useCallback(
    (e: React.SubmitEvent<HTMLFormElement>) => {
      e.preventDefault()
      if (!email.trim()) {
        alert('이메일을 입력하세요.')
        refEmail.current?.focus()
        return
      }
      if (!password.trim()) {
        alert('비밀번호를 입력하세요.')
        refPass.current?.focus()
        return
      }
      const params = new URLSearchParams()
      params.append('email', email)
      params.append('password', password)
      fetch(`http://localhost:8080/api/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
      })
        .then(async res => {
          if (!res.ok) {
            const errorData = await res.json()
            throw new Error(errorData.errror || '로그인에 실패했습니다.')
          }
          return res.json()
        })
        .then(data => {
          loginAction({
            mid: data.mid,
            name: data.name,
            email: data.email,
            token: data.token
          })
          sessionStorage.setItem('token', data.token)
          setLoginFail(false)
          navigate('/journals')
        })
        .catch(err => {
          console.error(err)
          setLoginFail(true)
          setError(err.message || '이메일과 비밀번호를 확인하세요')
        })
    },
    [email, password, loginAction, navigate]
  )

  return (
    <section className="py-5">
      <div className="container px-5">
        <div className="bg-light rounded-4 py-5 px-4 px-md-5">
          <div className="text-center mb-5">
            <div className="feature bg-primary bg-gradient-primary-to-secondary text-white rounded-3 mb-3">
              <i className="bi bi-box-arrow-in-right"></i>
            </div>
            <h1 className="fw-bolder">로그인</h1>
            <p className="lead fw-normal text-muted mb-0">
              로그인하고 소중한 기록을 이어가세요
            </p>
          </div>
          <div className="row gx-5 justify-content-center">
            <div className="col-lg-8 col-xl-6">
              <form id="contactForm" data-sb-form-api-token="API_TOKEN" onSubmit={login}>
                <div className="form-floating mb-3">
                  <input
                    className="form-control"
                    id="email"
                    type="email"
                    name="email"
                    ref={refEmail}
                    placeholder="name@example.com"
                    data-sb-validations="required,email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                  />
                  <label htmlFor="email">이메일</label>
                  <div className="invalid-feedback" data-sb-feedback="email:required">
                    이메일을 입력하세요.
                  </div>
                  <div className="invalid-feedback" data-sb-feedback="email:email">
                    유효하지 않은 이메일입니다.
                  </div>
                </div>
                <div className="form-floating mb-3">
                  <input
                    className="form-control"
                    id="password"
                    type="password"
                    name="password"
                    ref={refPass}
                    placeholder=""
                    data-sb-validations="required,email"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                  />
                  <label htmlFor="password">비밀번호</label>
                  <div className="invalid-feedback" data-sb-feedback="password:required">
                    비밀번호를 입력하세요.
                  </div>
                  <div className="invalid-feedback" data-sb-feedback="password:password">
                    유효하지 않은 비밀번호입니다.
                  </div>
                </div>
                <div className="d-none" id="submitSuccessMessage">
                  <div className="text-center mb-3">
                    <div className="fw-bolder">Form submission successful!</div>
                    To activate this form, sign up at
                    <br />
                    <a href="https://startbootstrap.com/solution/contact-forms">
                      https://startbootstrap.com/solution/contact-forms
                    </a>
                  </div>
                </div>
                {loginFail && (
                  <div className="text-center text-danger mb-3" id="submitErrorMessage">
                    <div className="text-center text-danger mb-3">
                      {error} 이메일과 비밀번호를 확인하여 주세요!
                    </div>
                  </div>
                )}
                <div className="d-grid">
                  <button
                    className="btn btn-primary btn-lg"
                    id="submitButton"
                    type="submit">
                    로그인
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>
    // <div className="container px-4 px-lg-5">
    //   <div className="card text-white bg-secondary my-5 py-4 text-center">
    //     <div className="card-body">
    //       <Title>Login</Title>

    //       <form onSubmit={login} className="mt-6">
    //         <div className="mb-3">
    //           <label className="form-label">Email</label>

    //           <input
    //             type="email"
    //             style={{border: '1px solid black'}}
    //             value={email}
    //             onChange={e => setEmail(e.target.value)}
    //           />
    //         </div>

    //         <div className="mb-3">
    //           <label className="form-label">Password</label>

    //           <input
    //             type="password"
    //             className="form-control"
    //             style={{border: '1px solid black'}}
    //             value={password}
    //             onChange={e => setPassword(e.target.value)}
    //           />
    //         </div>

    //         <button type="submit" className="btn btn-primary">
    //           Login
    //         </button>
    //       </form>
    //     </div>
    //   </div>
    // </div>
  )
}
