type DivProps = React.ComponentProps<'div'>

export default function Community({className, ...props}: DivProps) {
  return (
    <div className="container px-5 my-5">
      <div className="text-center mb-5">
        <h1 className="display-5 fw-bolder mb-0">
          <span className="text-gradient d-inline">Community</span>
        </h1>
      </div>
      <div className="row gx-5 justify-content-center bg-light pt-3 rounded-4">
        <div className="col-lg-11 col-xl-9 col-xxl-8">
          <section>
            <div className="d-flex align-items-center justify-content-between mb-4">
              <h2 className="text-primary fw-bolder mb-0">Following</h2>
              <a className="btn btn-primary px-4 py-3" href="#!">
                <div className="d-inline-block bi bi-search me-2"></div>
                Find
              </a>
            </div>
            <div className="card shadow border-0 rounded-4 mb-5">
              <div className="card-body p-2">
                <div className="row align-items-center gx-5">
                  <div className="col text-center text-lg-start mb-4 mb-lg-0">
                    <div className="bg-light p-4 rounded-4">
                      <div className="text-primary fw-bolder mb-2">2019 - Present</div>
                      <div className="small fw-bolder">Web Developer</div>
                      <div className="small text-muted">Stark Industries</div>
                      <div className="small text-muted">Los Angeles, CA</div>
                    </div>
                  </div>
                  <div className="col-lg-8">
                    <div>
                      Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus
                      laudantium, voluptatem quis repellendus eaque sit animi illo ipsam
                      amet officiis corporis sed aliquam non voluptate corrupti excepturi
                      maxime porro fuga.
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>
          <section>
            <h2 className="text-secondary fw-bolder mb-4">Follower</h2>
            <div className="card shadow border-0 rounded-4 mb-5">
              <div className="card-body p-2">
                <div className="row align-items-center gx-5">
                  <div className="col text-center text-lg-start mb-4 mb-lg-0">
                    <div className="bg-light p-4 rounded-4">
                      <div className="text-secondary fw-bolder mb-2">2015 - 2017</div>
                      <div className="mb-2">
                        <div className="small fw-bolder">Barnett College</div>
                        <div className="small text-muted">Fairfield, NY</div>
                      </div>
                      <div className="fst-italic">
                        <div className="small text-muted">Master's</div>
                        <div className="small text-muted">Web Development</div>
                      </div>
                    </div>
                  </div>
                  <div className="col-lg-8">
                    <div>
                      Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus
                      laudantium, voluptatem quis repellendus eaque sit animi illo ipsam
                      amet officiis corporis sed aliquam non voluptate corrupti excepturi
                      maxime porro fuga.
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>
          <div className="pb-5"></div>
        </div>
      </div>
    </div>
  )
}
