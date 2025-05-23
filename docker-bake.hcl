group "default" {
  targets = ["tsd-api-mock", "localega-tsd-proxy","mq-interceptor","cega-mock"]
}

target "tsd-api-mock" {
  context = "./services/tsd-api-mock"
  dockerfile = "Dockerfile"
  tags = ["tsd-api-mock:latest"]
}

target "localega-tsd-proxy" {
  context = "./services/localega-tsd-proxy"
  dockerfile = "Dockerfile"
  tags = ["localega-tsd-proxy:latest"]
}

target "mq-interceptor" {
  context = "./services/mq-interceptor"
  dockerfile = "Dockerfile"
  tags = ["mq-interceptor:latest"]
}

target "cega-mock" {
  context = "./services/cega-mock"
  dockerfile = "Dockerfile"
  tags = ["cega-mock:latest"]
}

