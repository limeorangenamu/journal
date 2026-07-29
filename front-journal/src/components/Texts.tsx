import type {DetailedHTMLProps, FC, HTMLAttributes} from 'react'
import {makeClassName} from './textUtil'

// prettier-ignore
// p 태그의 기본적인 속성 모두를 속성으로 받기 위한 DetailedHTMLProps
type TextProps = 
  DetailedHTMLProps<HTMLAttributes<HTMLParagraphElement>,HTMLParagraphElement>

export type TitleProps = TextProps & {numberOfLines?: number}
export const Title: FC<TitleProps> = function ({
  className: _className,
  numberOfLines: numberOfLines,
  ...props
}) {
  const className = makeClassName(
    'font-bold text-5xl text-center whitespace-pre-line',
    _className,
    numberOfLines
  )
  return <p {...props} className={className} />
}

export type SubtitleProps = TitleProps & {}
export const SubTitle: FC<SubtitleProps> = ({
  className: _className,
  numberOfLines: numberOfLines,
  ...props
}) => {
  const className = makeClassName(
    'font-semibold test-3xl text-center whitespace-pre-line',
    _className,
    numberOfLines
  )
  return <p {...props} className={className} />
}

export type SummaryProps = SubtitleProps & {}
export const Summary: FC<SummaryProps> = ({
  className: _className,
  numberOfLines,
  ...props
}) => {
  const className = makeClassName(
    'text-sm whitespace-pre-line',
    _className,
    numberOfLines
  )
  return <p {...props} className={className} />
}

export type ParagraphProps = SummaryProps & {}
export const Paragraph: FC<ParagraphProps> = ({
  className: _className,
  numberOfLines,
  ...props
}) => {
  const className = makeClassName(
    'font-normal text-base whitespace-pre-line',
    _className,
    numberOfLines
  )
  return <p {...props} className={className} />
}
