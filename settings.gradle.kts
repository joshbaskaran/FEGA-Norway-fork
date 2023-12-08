rootProject.name = "FEGA-Norway"
include("lib:clearinghouse")
findProject(":lib:clearinghouse")?.name = "clearinghouse"
