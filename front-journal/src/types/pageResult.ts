import type {JournalDTO} from './journal'
import type {PhotosDTO} from './photo'

export interface PageResultDTO {
  dtoList: JournalDTO[]
  page: number
  start: number
  end: number
  photosDTOList: PhotosDTO[]
  pageList: number[]
  prev: boolean
  next: boolean
}
