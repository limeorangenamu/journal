import type {MembersDTO} from './members'
import type {PhotosDTO} from './photo'

export interface JournalDTO {
  jno: number
  title: string
  content: string
  photosDTOList: PhotosDTO[]
  membersDTO: MembersDTO
  likes: number
  commentsCnt: number
  regDate: string
  modDate: string
}
