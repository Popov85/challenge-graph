package com.example.web

import com.example.domain.Connection
import com.example.service.TaskService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import org.slf4j.LoggerFactory


@Controller("/task")
class TaskController(private val taskService: TaskService) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Get("/connections")
    @Produces(MediaType.APPLICATION_JSON)
    fun connections(): List<Connection> {
        log.trace("Getting connections")
        return taskService.connections()
    }

    @Post("/apply/{connectFrom}")
    @Produces(MediaType.APPLICATION_JSON)
    fun apply(@PathVariable connectFrom: String, @Body connectTos: List<String>): String {
        log.trace("Apply from = {}, tos = {}", connectFrom, connectTos)
        taskService.apply(connectFrom, connectTos)
        return "Ok"
    }
}