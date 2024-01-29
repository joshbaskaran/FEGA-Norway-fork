rootProject.name = "FEGA-Norway"
include("lib:crypt4gh")
include("lib:clearinghouse")
include("lib:tsd-file-api-client")
include("services:mq-interceptor")
findProject(":lib:crypt4gh")?.name = "crypt4gh"
findProject(":lib:clearinghouse")?.name = "clearinghouse"
findProject(":lib:tsd-file-api-client")?.name = "tsd-file-api-client"
findProject(":services:mq-interceptor")?.name = "mq-interceptor"
