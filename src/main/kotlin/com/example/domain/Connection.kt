package com.example.domain

data class Connection(var connectFrom: String, var connectTo: String) {
    override fun toString(): String = "$connectFrom - $connectTo"
}