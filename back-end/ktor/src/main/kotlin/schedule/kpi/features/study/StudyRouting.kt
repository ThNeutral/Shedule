package schedule.kpi.features.study


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import schedule.kpi.database.lessons.LessonDTO
import schedule.kpi.database.lessons.Lessons

fun Application.configureStudyRouting() {
    routing {
        post("/study") {
            val studyController = StudyController(call)
            studyController.registerNewLesson()
        }
        delete("/study/{id}"){
            val itemId = call.parameters["id"]?.toIntOrNull()
            if (itemId != null) {
                transaction { Lessons.deleteWhere { Lessons.id eq itemId } }
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid id format")
            }
        }
        patch("/study/{id}"){
            val itemId = call.parameters["id"]?.toIntOrNull()
            if (itemId != null) {
                val studyController = StudyController(call)
                studyController.updateLesson()
                transaction { Lessons.deleteWhere { Lessons.id eq itemId } }
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid id format")
            }
        }
        get("/study/lessonsByGroup") {
            val groupParam = call.parameters["group"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Group parameter is missing")

            val lessonsForGroup = transaction {
                Lessons.select { Lessons.group eq groupParam }
                    .orderBy(Lessons.period, SortOrder.ASC)
                    .orderBy(Lessons.day, SortOrder.ASC)
                    .orderBy(Lessons.time, SortOrder.ASC)
                    .map {
                        LessonDTO(
                            period = it[Lessons.period],
                            lesson = it[Lessons.lesson],
                            teacher = it[Lessons.teacher],
                            link = it[Lessons.link],
                            group = it[Lessons.group],
                            time = it[Lessons.time],
                            day= it[Lessons.day]
                        )
                    }
            }

            if (lessonsForGroup.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, lessonsForGroup)
            } else {
                call.respond(HttpStatusCode.NotFound, "No lessons found for the specified group")
            }
        }









    }

}