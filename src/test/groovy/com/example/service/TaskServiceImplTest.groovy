package com.example.service

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

@MicronautTest
class TaskServiceImplTest extends Specification {

    def "Nullable input"() {
        given: 'No existing nodes, nullable input'
        def task = new TaskServiceImpl()

        when: 'Apply wrong nullable input'
        task.apply("1", null)

        then:
        thrown(NullPointerException)
    }

    def "Empty input"() {
        given: 'No existing nodes, empty input'
        def task = new TaskServiceImpl()

        when: 'Apply wrong empty input'
        task.apply("1", [])

        then:
        thrown(IllegalArgumentException)
    }

    def "Invalid input"() {
        given: 'Some existing nodes, invalid input'
        def task = new TaskServiceImpl()
        task.apply("1", ["2", "3"])

        when: 'Apply wrong invalid input'
        task.apply("4", ["4", "4", "4"])

        then:
        thrown(IllegalArgumentException)
    }

    def "Two nodes, single non-existing graph"() {
        given: 'No existing nodes, simple valid input'
        def task = new TaskServiceImpl()

        when: 'Apply valid input'
        task.apply("1", ["2"])

        then:
        task.connections().toString() == [
                '1 - 2',
                '2 - 1'
        ] as String
    }

    def "Three nodes, single non-existing graph"() {
        given: 'No existing nodes, simple valid input'
        def task = new TaskServiceImpl()

        when: 'Apply valid input'
        task.apply("1", ["2", "3"])

        then:
        task.connections().toString() == [
                '1 - 2',
                '1 - 3',
                '2 - 1',
                '2 - 3',
                '3 - 1',
                '3 - 2'
        ] as String
    }

    def "5 nodes, two non-existing graphs"() {
        given: 'No existing nodes, simple valid input'
        def task = new TaskServiceImpl()

        when: 'Apply 2 valid inputs'
        task.apply("1", ["2", "3"])
        task.apply("4", ["5"])

        then:
        task.connections().toString() == [
                '1 - 2',
                '1 - 3',
                '2 - 1',
                '2 - 3',
                '3 - 1',
                '3 - 2',
                '4 - 5',
                '5 - 4',
        ] as String
    }

    def "8 nodes, three non-existing graphs"() {
        given: 'No existing nodes, simple valid input'
        def task = new TaskServiceImpl()

        when: 'Apply 3 valid inputs'
        task.apply("1", ["2", "3"])
        task.apply("4", ["5", "6"])
        task.apply("7", ["8"])

        then:
        task.connections().toString() == [
                '1 - 2',
                '1 - 3',
                '2 - 1',
                '2 - 3',
                '3 - 1',
                '3 - 2',
                '4 - 5',
                '4 - 6',
                '5 - 4',
                '5 - 6',
                '6 - 4',
                '6 - 5',
                '7 - 8',
                '8 - 7'
        ] as String
    }

    def "6 nodes, 1 existing and 1 non- graphs"() {
        given: 'No existing nodes, simple valid input'
        def task = new TaskServiceImpl()

        when: 'Apply 2 valid inputs'
        task.apply("1", ["2", "3"])
        task.apply("1", ["4"])

        then:
        task.connections().toString() == [
                '1 - 2',
                '1 - 3',
                '1 - 4',
                '2 - 1',
                '2 - 3',
                '2 - 4',
                '3 - 1',
                '3 - 2',
                '3 - 4',
                '4 - 1',
                '4 - 2',
                '4 - 3'
        ] as String
    }

    def "6 nodes, 1 existing and 1 non- graphs reversed"() {
        given: 'No existing nodes, simple valid input'
        def task = new TaskServiceImpl()

        when: 'Apply 2 valid inputs'
        task.apply("1", ["2", "3"])
        task.apply("4", ["1"])

        then:
        task.connections().toString() == [
                '1 - 2',
                '1 - 3',
                '1 - 4',
                '2 - 1',
                '2 - 3',
                '2 - 4',
                '3 - 1',
                '3 - 2',
                '3 - 4',
                '4 - 1',
                '4 - 2',
                '4 - 3'
        ] as String
    }

    def "Apply"() {
        given: 'existing connections of a, d and k'

        def task = new TaskServiceImpl()
        task.apply("a", ["b", "c"])
        task.apply("d", ["e", "f"])
        task.apply("k", ["l"])

        when: 'a new connection is defined between a, d and k'
        task.apply("a", ["d", "k"])

        then: 'the connections should be listed correctly'
        task.connections().toString() == [
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
                'l - k'
        ] as String
    }
}
