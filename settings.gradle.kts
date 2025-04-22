rootProject.name = "FEGA-Norway"

include(":cli:lega-commander")

include(":lib:crypt4gh")
include(":lib:clearinghouse")
include(":lib:tsd-file-api-client")

include(":services:cega-mock")
include(":services:tsd-api-mock")
include(":services:mq-interceptor")
include(":services:localega-tsd-proxy")

include(":e2eTests")

findProject(":cli:lega-commander")?.name = "lega-commander"
findProject(":lib:crypt4gh")?.name = "crypt4gh"
findProject(":lib:clearinghouse")?.name = "clearinghouse"
findProject(":lib:tsd-file-api-client")?.name = "tsd-file-api-client"
findProject(":services:cega-mock")?.name = "cega-mock"
findProject(":services:tsd-api-mock")?.name = "tsd-api-mock"
findProject(":services:mq-interceptor")?.name = "mq-interceptor"
findProject(":services:localega-tsd-proxy")?.name = "localega-tsd-proxy"
