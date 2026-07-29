import {useNavigate} from 'react-router'

import {useCallback, useRef, useState} from 'react'
import type {ChangeEvent, SubmitEvent} from 'react'
import type {MembersRegisterRequest} from '../types'

type DivProps = React.ComponentProps<'div'>

// type 처리 방식
type JoinFormData = {
  email: string
  pass: string
  rePass: string
  name: string
  nickname: string
  mobile: string
}

export default function Join({className, ...props}: DivProps) {
  const navigate = useNavigate()

  // prettier-ignore
  const [form, setForm] = useState<JoinFormData>({
    email: '', pass: '', rePass: '', name: '',nickname: '', mobile: ''
  })

  const onChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    const {name, value} = e.target
    setForm(prev => ({...prev, [name]: value}))
  }, [])

  const refEmail = useRef<HTMLInputElement>(null)
  const refPass = useRef<HTMLInputElement>(null)
  const refRePass = useRef<HTMLInputElement>(null)
  const refName = useRef<HTMLInputElement>(null)
  const refNickName = useRef<HTMLInputElement>(null)
  const refMobile = useRef<HTMLInputElement>(null)

  const onSubmit = useCallback(
    async (e: SubmitEvent<HTMLFormElement>) => {
      e.preventDefault()

      if (!form.email.trim()) {
        alert('이메일을 입력하세요.')
        refEmail.current?.focus()
        return
      }

      if (!form.pass.trim()) {
        alert('비밀번호를 입력하세요.')
        refPass.current?.focus()
        return
      }

      if (!form.rePass.trim()) {
        alert('비밀번호 확인을 입력하세요.')
        refRePass.current?.focus()
        return
      }

      if (form.pass !== form.rePass) {
        alert('비밀번호가 일치하지 않습니다.')
        setForm(prev => ({
          ...prev,
          pass: '',
          rePass: ''
        }))
        refPass.current?.focus()
        return
      }

      if (!form.name.trim()) {
        alert('이름을 입력하세요.')
        refName.current?.focus()
        return
      }

      if (!form.nickname.trim()) {
        alert('별명을 입력하세요.')
        refNickName.current?.focus()
        return
      }

      if (!form.mobile.trim()) {
        alert('휴대폰 번호를 입력하세요.')
        refMobile.current?.focus()
        return
      }

      try {
        const requestData: MembersRegisterRequest = {
          email: form.email.trim(),
          password: form.pass,
          name: form.name.trim(),
          nickname: form.nickname.trim(),
          mobile: form.mobile.trim(),
          fromSocial: false
        }
        const response = await fetch('http://localhost:8080/api/members/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(requestData)
        })

        if (!response.ok) {
          const errorData = (await response.json().catch(() => null)) as {
            message?: string
          } | null

          throw new Error(errorData?.message || `회원가입 실패 : ${response.status}`)
        }

        alert('회원가입이 완료되었습니다.')
        navigate('/login')
      } catch (error) {
        console.error('회원가입 오류:', error)

        if (error instanceof Error) {
          alert(error.message)
        } else {
          alert('회원가입 중 알 수 없는 오류가 발생했습니다.')
        }
      }
    },
    [form, navigate]
  )

  return (
    <section className="py-5">
      <div className="container px-5">
        <div className="bg-light rounded-4 py-5 px-4 px-md-5">
          <div className="text-center mb-5">
            <div className="feature bg-primary bg-gradient-primary-to-secondary text-white rounded-3 mb-3">
              <i className="bi bi-person-plus"></i>
            </div>
            <h1 className="fw-bolder">회원가입</h1>
            <p className="lead fw-normal text-muted mb-0">
              가입하여 도감을 완성시켜주세요
            </p>
          </div>
          <div className="row gx-5 justify-content-center">
            <div className="col-lg-8 col-xl-6">
              <form
                id="contactForm"
                data-sb-form-api-token="API_TOKEN"
                onSubmit={onSubmit}>
                <div className="form-floating mb-3">
                  <input
                    className="form-control"
                    id="email"
                    type="email"
                    name="email"
                    ref={refEmail}
                    value={form.email}
                    onChange={onChange}
                    placeholder=""
                    data-sb-validations="required,email"
                  />
                  <label htmlFor="email">이메일</label>
                  <div className="invalid-feedback" data-sb-feedback="email:required">
                    이메일을 입력하세요.
                  </div>
                  <div className="invalid-feedback" data-sb-feedback="email:email">
                    이메일이 유효하지 않습니다.
                  </div>
                </div>
                <div className="form-floating mb-3">
                  <input
                    className="form-control"
                    type="password"
                    id="pass"
                    name="pass"
                    ref={refPass}
                    placeholder=""
                    data-sb-validations="required,pass"
                    value={form.pass}
                    onChange={onChange}
                  />
                  <label htmlFor="pass">비밀번호</label>
                  <div className="invalid-feedback" data-sb-feedback="password:required">
                    비밀번호를 입력하세요.
                  </div>
                  <div className="invalid-feedback" data-sb-feedback="password:password">
                    비밀번호가 유효하지 않습니다.
                  </div>
                </div>
                <div className="form-floating mb-3">
                  <input
                    className="form-control"
                    type="password"
                    id="rePass"
                    name="rePass"
                    ref={refRePass}
                    placeholder=""
                    data-sb-validations="required,email"
                    value={form.rePass}
                    onChange={onChange}
                  />
                  <label htmlFor="rePass">비밀번호 확인</label>
                  <div className="invalid-feedback" data-sb-feedback="password:required">
                    비밀번호 확인을 입력해주세요.
                  </div>
                  <div className="invalid-feedback" data-sb-feedback="password:password">
                    비밀번호가 일치하지 않습니다.
                  </div>
                </div>
                <div className="form-floating mb-3">
                  <input
                    className="form-control"
                    id="name"
                    name="name"
                    type="text"
                    ref={refName}
                    value={form.name}
                    onChange={onChange}
                    placeholder="Enter your name..."
                    data-sb-validations="required"
                  />
                  <label htmlFor="name">이름</label>
                  <div className="invalid-feedback" data-sb-feedback="name:required">
                    이름을 입력해주세요.
                  </div>
                </div>
                <div className="form-floating mb-3">
                  <input
                    className="form-control"
                    id="nickname"
                    name="nickname"
                    type="text"
                    ref={refNickName}
                    value={form.nickname}
                    onChange={onChange}
                    placeholder="Enter your nickname..."
                    data-sb-validations="required"
                  />
                  <label htmlFor="name">별명</label>
                  <div className="invalid-feedback" data-sb-feedback="nickname:required">
                    별명을 입력해주세요.
                  </div>
                </div>
                <div className="form-floating mb-3">
                  <input
                    className="form-control"
                    id="mobile"
                    type="tel"
                    name="mobile"
                    ref={refMobile}
                    value={form.mobile}
                    onChange={onChange}
                    placeholder="(123) 456-7890"
                    data-sb-validations="required"
                  />
                  <label htmlFor="mobile">휴대폰 번호</label>
                  <div className="invalid-feedback" data-sb-feedback="phone:required">
                    휴대폰 번호를 입력해주세요.
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
                <div className="d-none" id="submitErrorMessage">
                  <div className="text-center text-danger mb-3">
                    Error sending message!
                  </div>
                </div>
                <div className="d-grid">
                  <button
                    className="btn btn-primary btn-lg"
                    id="submitButton"
                    type="submit">
                    회원가입
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
