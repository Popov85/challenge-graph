package com.example.service

import com.example.domain.Connection
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Stream

@Singleton
class TaskServiceImpl : TaskService {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val connections: MutableList<Connection> = mutableListOf()

    private val connectionsSet: MutableSet<Connection> = mutableSetOf()

    private val cachedNodes: MutableMap<String, Set<String>> = mutableMapOf()

    override fun connections(): List<Connection> {
        val sortedWith = this.connections
                .sortedWith(compareBy(Connection::connectFrom, Connection::connectTo))
        return sortedWith
    }

    override fun apply(connectFrom: String, connectTos: List<String>) {
        // Guard statements!
        if (connectFrom.isEmpty() || connectTos.isEmpty())
            throw IllegalArgumentException("Invalid empty input!");
        // Filter out nulls and empty strings
        var filteredConnectTos = connectTos
                .filter{it != null && it.isNotEmpty() }
        if (filteredConnectTos.isEmpty())
            throw IllegalArgumentException("Invalid input! No valid connectTos!");
        var newNodes = connectTos.toMutableSet()
        newNodes.add(connectFrom)
        if (newNodes.size<2)
            throw IllegalArgumentException("Invalid input! No valid connectTos!");

        if (cachedNodes.isEmpty()) { // Init case
            processSeparateGraph(newNodes)
        } else { // Some graphs already exist
            // Find out if we face a separate graph?
            val resultOfSearch = newNodes
                    .filter { cachedNodes.containsKey(it) }
            if (resultOfSearch.isEmpty()) {
                // Like init case: new separate graph
                processSeparateGraph(newNodes)
            } else { // Case when some merge is required!
                //log.debug("No existing keys!")
                processConnectingGraphs(newNodes)
            }
        }

    }

    private fun processSeparateGraph(nodes: Set<String>) {
        nodes.forEach{createAndSaveNode(it, nodes)}
        // Create and save interconnections
        createAndSaveInterconnections(nodes);
    }

    private fun processConnectingGraphs(newNodes: Set<String>) {
        // Split into new nodes and existing
        val split = newNodes
                .groupByTo(mutableMapOf()) { cachedNodes.containsKey(it) }
                .mapValues { it.value.toMutableSet() }
        val existingNodes = split[true]!!
        val nonExistingNodes = split[false]
        if (nonExistingNodes != null
                && nonExistingNodes.isNotEmpty()
                && nonExistingNodes.size > 1) {
            // Create a new separate graph of 2+;
            processSeparateGraph(nonExistingNodes)
        }
        // Gather all graphs to be connected
        val setOfToBeConnectedGraphs = existingNodes
                .map {cachedNodes[it] ?: emptySet() }
                .toMutableSet()
        // Add non-existing graph if present
        setOfToBeConnectedGraphs
                .add(nonExistingNodes ?: emptySet())
        // Connect graphs
        connectGraphs(setOfToBeConnectedGraphs)
    }

    private fun createAndSaveNode(newNode: String, newConnectedNodes: Set<String>) {
        cachedNodes.putIfAbsent(newNode, newConnectedNodes)
    }

    private fun mergeConnectedNodes(oldNode: String, newConnectedNodes: Set<String>) {
        cachedNodes.computeIfAbsent(oldNode) { newConnectedNodes }
        cachedNodes.computeIfPresent(oldNode) { _: String, v: Set<String>
            -> Stream.concat(v.stream(), newConnectedNodes.stream()).collect(Collectors.toSet()) }
        //log.debug("Merged key = {}, values = {}", oldNode, newConnectedNodes);
    }

    private fun createAndSaveInterconnections(nodes: Set<String>) {
        nodes.forEach(Consumer { nodeFrom: String ->
            nodes.forEach(Consumer { nodeTo: String ->
                createAndSaveConnectionBetween(nodeFrom, nodeTo) }) })
    }

    private fun createAndSaveConnectionBetween(connectFrom: String, connectTo: String) {
        if (connectFrom != connectTo) {
            val newConnection = Connection(connectFrom, connectTo)
            val reversedNewConnection = Connection(connectTo, connectFrom)
            if (!connectionsSet.contains(newConnection)) {
                connections.add(newConnection)
                // Duplicate to the set to speed up "contains"!
                connectionsSet.add(newConnection)
            }
            if (!connectionsSet.contains(reversedNewConnection)) {
                connections.add(reversedNewConnection)
                // Duplicate to the set to speed up "contains"!
                connectionsSet.add(reversedNewConnection)
            }
            //log.info("Connected: {} - {}", connectFrom, connectTo);
        }
    }

    private fun connectGraphs(setToBeConnectedGraphs: MutableSet<Set<String>>) {
        //LOGGER.info("Connect graphs = {}", setToBeConnectedGraphs);
        for (setOfToBeConnectedGraph in setToBeConnectedGraphs) {
            for (nodeToBeConnected in setOfToBeConnectedGraph) {
                val setOfToBeMerged = prepareSetToBeMerged(setToBeConnectedGraphs, setOfToBeConnectedGraph)
                mergeConnectedNodes(nodeToBeConnected, setOfToBeMerged)
                // Create Connection objects
                setOfToBeMerged.add(nodeToBeConnected)
                createAndSaveInterconnections(setOfToBeMerged!!)
            }
        }
        //LOGGER.info("Connect graphs end");
    }

    private fun prepareSetToBeMerged(setToBeConnectedGraphs: Set<Set<String>>, setToBeConnectedGraph: Set<String>): MutableSet<String> {
        return setToBeConnectedGraphs
                .filter { it != setToBeConnectedGraph }
                .flatMap {it.toSet() }.toMutableSet()
    }
}