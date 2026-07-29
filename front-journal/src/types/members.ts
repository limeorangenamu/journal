export interface MembersDTO {
  mid: number
  email: string
  name: string
  nickname: string
  mobile: string
  fromSocial: boolean
  regDate: string
  modDate: string
}

export interface MembersRegisterRequest {
  email: string
  password: string
  name: string
  nickname: string
  mobile: string
  fromSocial: boolean
}
