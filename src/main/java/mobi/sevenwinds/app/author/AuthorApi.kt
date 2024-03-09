package mobi.sevenwinds.app.budget

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import mobi.sevenwinds.app.author.AuthorService
import java.time.LocalDateTime


fun NormalOpenAPIRoute.author() {
    route("/author") {
        route("/add").post<Unit, AuthorRecord, AuthorRecord>(info("Добавить запись")) { param, body ->
            body.createdAt = LocalDateTime.now().toString();
            respond(AuthorService.addRecord(body.name))
        }
    }
}


data class AuthorRecord(
    val name: String,
    var createdAt: String?
)
