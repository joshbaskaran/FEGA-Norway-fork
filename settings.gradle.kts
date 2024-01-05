rootProject.name = "FEGA-Norway"
include("lib:crypt4gh")
include("lib:clearinghouse")
include("services:tsd-api-mock")
findProject(":lib:crypt4gh")?.name = "crypt4gh"
findProject(":lib:clearinghouse")?.name = "clearinghouse"
findProject(":services:tsd-api-mock")?.name = "tsd-api-mock"
