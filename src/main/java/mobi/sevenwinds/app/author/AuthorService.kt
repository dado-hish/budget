package mobi.sevenwinds.app.author

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.budget.AuthorRecord
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object AuthorService {
    suspend fun addRecord(name: String): AuthorRecord = withContext(Dispatchers.IO) {
        transaction{
            val entity = AuthorEntity.new{
                this.name = name
                this.createdAt = DateTime();
            }
            return@transaction entity.toResponse()
        }
    }
}



