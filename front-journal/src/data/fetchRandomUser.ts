// start :: unknown, any 비교 =========================================================================
// any :: 모든 타입 할당 가능, 타입검사를 완전 포기하여 에러가 발생 안함
const doSomethingAny = (data: any) => {
  data.toUpperCase() // 에러 발생 안 함 (런타임 에러 위험)
}

// unknown :: 타입스크립트3.0부터 추가, any처럼 모든 타입 할당 가능, 안전하게 사용하려면 타입 먼저 확인
const doSomethingUnknown = (data: unknown) => {
  // data.toUpperCase(); ❌ 에러: 속성 'toUpperCase'는 'unknown' 형식에 없습니다.
  // 타입 좁히기(Type Narrowing) 적용
  if (typeof data === 'string') data.toUpperCase() // ✅ 정상 동작
}

let someValue: unknown = 'hello'
//let str1: string = someValue // ❌ 에러: unknown 형식은 string 형식에 할당할 수 없습니다.
let str2: string = someValue as string // ✅ 타입 단언(Type Assertion)을 사용해야 함
// end :: unknown, any 비교 =========================================================================

export type IRandomUser = {
  email: string
  name: {title: string; first: string; last: string}
  picture: {large: string}
}

const convertRandomUser = (result: unknown) => {
  const {email, name, picture} = result as IRandomUser
  return {email, name, picture}
}

export const fetchRandomUser = (): Promise<IRandomUser> =>
  new Promise((resolve, reject) => {
    fetch('https://randomuser.me/api/')
      .then(res => res.json())
      .then((data: unknown) => {
        console.log(data)
        const {results} = data as {results: IRandomUser[]}
        resolve(convertRandomUser(results[0]))
      })
      .catch(reject)
  })
