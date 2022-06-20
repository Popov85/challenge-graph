package com.example.service

import com.example.domain.Connection

interface TaskService {
    fun connections(): List<Connection>
    fun apply(connectFrom: String, connectTos: List<String>)

}