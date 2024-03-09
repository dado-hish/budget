package mobi.sevenwinds.app.budget

import io.restassured.RestAssured
import mobi.sevenwinds.common.ServerTest
import mobi.sevenwinds.common.jsonBody
import mobi.sevenwinds.common.toResponse
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BudgetApiKtTest : ServerTest() {

    @BeforeEach
    internal fun setUp() {
        transaction {
//            AuthorTable.deleteAll()
//            BudgetTable.deleteAll()
        }
//        addRecord(BudgetRecord(2020, 5, 10, BudgetType.Приход, null))
//        addRecord(AuthorRecord("Первый автор", null));
//        addRecord(AuthorRecord("Второй автор"));
//        addRecord(AuthorRecord("Третий автор"));
//        addRecord(AuthorRecord("Четвертый автор"));
//        addRecord(AuthorRecord("Пятый автор"));
//        addRecord(AuthorRecord("Шестой автор"));
    }
    @Test
    fun testTest(){
        Assert.assertEquals(5, 5);
    }
    @Test
    fun testBudgetPagination() {
        addRecord(BudgetRecord(2020, 5, 10, BudgetType.Приход, 1))
        addRecord(BudgetRecord(2020, 5, 5, BudgetType.Приход, null))
        addRecord(BudgetRecord(2020, 5, 20, BudgetType.Приход, null))
        addRecord(BudgetRecord(2020, 5, 30, BudgetType.Приход, null))
        addRecord(BudgetRecord(2020, 5, 40, BudgetType.Приход, null))
        addRecord(BudgetRecord(2030, 1, 1, BudgetType.Расход, null))

        RestAssured.given()
            .queryParam("limit", 5)
            .queryParam("offset", 0)
            .get("/budget/year/2020/stats")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                println("${response.total} / ${response.items} / ${response.totalByType} ")

                Assert.assertEquals(5, response.total)
                Assert.assertEquals(5, response.items.size)
                Assert.assertEquals(105, response.totalByType[BudgetType.Приход.name])
            }
    }

    @Test
    fun testStatsSortOrder() {
        addRecord(BudgetRecord(2020, 5, 100, BudgetType.Приход, null))
        addRecord(BudgetRecord(2020, 1, 5, BudgetType.Приход, null))
        addRecord(BudgetRecord(2020, 5, 50, BudgetType.Приход, null))
        addRecord(BudgetRecord(2020, 1, 30, BudgetType.Приход, null))
        addRecord(BudgetRecord(2020, 5, 400, BudgetType.Приход, null ))

        // expected sort order - month ascending, amount descending

        RestAssured.given()
            .get("/budget/year/2020/stats?limit=100&offset=0")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                val sortedItems = response.items.sortedWith(compareBy<BudgetRecord>(
                    { it.month },
                    { -it.amount }
                ))
                    .toList()
                println(response.items)

                Assert.assertEquals(30, sortedItems[0].amount)
                Assert.assertEquals(5, sortedItems[1].amount)
                Assert.assertEquals(400, sortedItems[2].amount)
                Assert.assertEquals(100, sortedItems[3].amount)
                Assert.assertEquals(50, sortedItems[4].amount)
            }
    }

    @Test
    fun testInvalidMonthValues() {
        RestAssured.given()
            .jsonBody(BudgetRecord(2020, -5, 5, BudgetType.Приход, null))
            .post("/budget/add")
            .then().statusCode(400)

        RestAssured.given()
            .jsonBody(BudgetRecord(2020, 15, 5, BudgetType.Приход, null))
            .post("/budget/add")
            .then().statusCode(400)
    }

    private fun addRecord(record: BudgetRecord) {
        RestAssured.given()
            .jsonBody(record)
            .post("/budget/add")
//            .toResponse<BudgetRecord>().let { response ->
//                Assert.assertEquals(record, response)
//            }
    }

    private fun addRecord(record: AuthorRecord) {
        record.createdAt = DateTime().toString();
        RestAssured.given()
            .jsonBody(record)
            .post("/author/add")
            .toResponse<AuthorRecord>().let { response ->
                Assert.assertEquals(record, response)
            }
    }
}