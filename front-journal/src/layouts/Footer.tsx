import {Link} from 'react-router'

type DivProps = React.ComponentProps<'div'>

export default function Footer({className, ...props}: DivProps) {
  return (
    <footer className="bg-white py-4 mt-auto">
      <div className="container px-5">
        <div className="row align-items-center justify-content-between flex-column flex-sm-row">
          <div className="col-auto">
            <div className="small m-0">Copyright &copy; Your Website 2026</div>
          </div>
          <div className="col-auto">
            <Link className="small" to="/privacy">
              Privacy
            </Link>
            <span className="mx-1">&middot;</span>
            <Link className="small" to="/terms">
              Terms
            </Link>
            <span className="mx-1">&middot;</span>
            <Link className="small" to="/contact">
              Contact
            </Link>
          </div>
        </div>
      </div>
    </footer>
  )
}
