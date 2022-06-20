package com.example.service

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

@MicronautTest
class TaskServiceImplTest extends Specification {
    def "Apply"() {
        given: 'existing connections of a, d, k and z'
        def task = new TaskServiceImpl()
        task.apply("a", ["b", "c"])
        task.apply("d", ["e", "f"])
        task.apply("k", ["l"])
        //task.apply("x", ["y", "z"])

        when: 'a new connection is defined between a, d and k'
        task.apply("a", ["d", "k"])

        then: 'the connections should be listed correctly'
        task.connections*.toString() == [
                'a - b',
                'a - c',
                'a - d',
                'a - e',
                'a - f',
                'a - k',
                'a - l',
                'b - a',
                'b - c',
                'b - d',
                'b - e',
                'b - f',
                'b - k',
                'b - l',
                'c - a',
                'c - b',
                'c - d',
                'c - e',
                'c - f',
                'c - k',
                'c - l',
                'd - a',
                'd - b',
                'd - c',
                'd - e',
                'd - f',
                'd - k',
                'd - l',
                'e - a',
                'e - b',
                'e - c',
                'e - d',
                'e - f',
                'e - k',
                'e - l',
                'f - a',
                'f - b',
                'f - c',
                'f - d',
                'f - e',
                'f - k',
                'f - l',
                'k - a',
                'k - b',
                'k - c',
                'k - d',
                'k - e',
                'k - f',
                'k - l',
                'l - a',
                'l - b',
                'l - c',
                'l - d',
                'l - e',
                'l - f',
                'l - k',
        ]
    }
}
