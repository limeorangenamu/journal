import {Title} from '../components'
type DivProps = React.ComponentProps<'div'>

export default function NotFound({className, ...props}: DivProps) {
  return (
    <div className={`mt-4 ${className} ?? ""`} style={props.style}>
      <Title>NotFound</Title>
      <h1>404 Not Found</h1>
    </div>
  )
}
