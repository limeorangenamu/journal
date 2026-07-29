import type {CSSProperties, DetailedHTMLProps, FC, HTMLAttributes} from 'react'

// span 태그가 가지는 기타 속성들을 받기 위한 변수 선언
type ReactSpanProps = DetailedHTMLProps<HTMLAttributes<HTMLSpanElement>, HTMLSpanElement>

export type IconProps = ReactSpanProps & {name: string; style?: CSSProperties}

// 최근에는 FC를 잘 사용하지 않음.
// export const Icon: FC<IconProps> = function ({name, style}) {
//   return (
//     <span className="material-icons" style={style}>
//       {name}
//     </span>
//   )
// }

export function Icon({name, style, className, ...props}: IconProps) {
  return (
    <span {...props} className={`material-icons ${className ?? ''}`} style={style}>
      {name}
    </span>
  )
}
