package app.ito.akki.praisesns

import java.util.*

data class Groups (
    var id: String = UUID.randomUUID().toString(),
    var groupName: String = "",
    var groupID: String = ""
)