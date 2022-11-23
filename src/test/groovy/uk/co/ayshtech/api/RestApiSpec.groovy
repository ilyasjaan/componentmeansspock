package uk.co.ayshtech.api

import groovy.json.JsonSlurper
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import spock.lang.Unroll
import uk.co.ayshtech.api.model.TaskDto
import uk.co.ayshtech.repository.TaskInMemoryRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestApiSpec extends spock.lang.Specification {

    @Autowired
    TaskInMemoryRepository repository

    @LocalServerPort
    int port;

    def setup() {
        RestAssured.port = port
    }

    def "POST:: should save task" () {
        given:
        def task = """
                {
                    "name": "log_sales",
                    "assignedTo": "user1",
                    "details": "find latest sales and log it"
                }
        """

        when:
        Response response =
                RestAssured.given()
                        .body(task)
                        .contentType(ContentType.JSON)
                        .post("/tasks")
                        .andReturn()

        and:
        assert response.statusCode() == 201
        def taskId = response.header("Location").split("/")[2]
        TaskDto savedTask = repository.getTask(taskId)

        then:
        savedTask.name == "log_sales"
        savedTask.assignedTo == "user1"
        savedTask.details == "find latest sales and log it"
    }

    def "GET:: should get task" () {
        given:
        String taskId = repository.saveTask(new TaskDto(
                name: "submit_receipt",
                assignedTo: "user2",
                details: "submit all receipts of today"
        ))

        when:
        Response response =
                RestAssured.given()
                        .accept(ContentType.JSON)
                        .get("/tasks/${taskId}")
                        .andReturn()

        and:
        assert response.statusCode() == 200
        def task = new JsonSlurper().parseText(response.asString())

        then:
        task["name"] == "submit_receipt"
        task["assignedTo"] == "user2"
        task["details"] == "submit all receipts of today"
    }

    def "GET:: should get task - (compare whole JSON)" () {
        given:
        String taskId = repository.saveTask(new TaskDto(
                name: "submit_receipt",
                assignedTo: "user2",
                details: "submit all receipts of today"
        ))
        def expectedJsonStr = """
            {
                "name": "submit_receipt",
                "assignedTo": "user2",
                "details": "submit all receipts of today"
            }
        """

        when:
        Response response =
                RestAssured.given()
                        .accept(ContentType.JSON)
                        .get("/tasks/${taskId}")
                        .andReturn()

        and:
        assert response.statusCode() == 200
        def taskJson = new JsonSlurper().parseText(response.asString())
        def expectedJson = new JsonSlurper().parseText(expectedJsonStr)

        then:
        taskJson == expectedJson
    }

    @Unroll
    def "should return BAD_REQUEST :: name [#name], assignedTo[#assignedTo] -> message [#message]" () {
        given:
        def task = [
                "name": name,
                "assignedTo": assignedTo,
                "details": "test details"
        ]

        when:
        Response response =
                RestAssured.given()
                        .body(task)
                        .contentType(ContentType.JSON)
                        .post("/tasks")
                        .andReturn()

        and:
        assert response.statusCode() == 400
        def responseMessage = response.asString()

        then:
        responseMessage == message

        where:
        name       | assignedTo | message
        null       | null       | "{\"name\":\"must not be empty\",\"assignedTo\":\"must not be empty\"}"
        "somename" | null       | "{\"assignedTo\":\"must not be empty\"}"
        null       | "someuser" | "{\"name\":\"must not be empty\"}"
        ""         | ""         | "{\"name\":\"must not be empty\",\"assignedTo\":\"must not be empty\"}"
        "somename" | ""         | "{\"assignedTo\":\"must not be empty\"}"
        ""         | "someuser" | "{\"name\":\"must not be empty\"}"
    }


}
