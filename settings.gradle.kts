rootProject.name = "FEGA-Norway"
include("lib:crypt4gh")
include("lib:clearinghouse")
findProject(":lib:crypt4gh")?.name = "crypt4gh"
findProject(":lib:clearinghouse")?.name = "clearinghouse"
