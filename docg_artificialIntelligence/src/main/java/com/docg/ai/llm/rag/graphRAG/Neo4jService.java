package com.docg.ai.llm.rag.graphRAG;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import java.util.List;

public class Neo4jService implements AutoCloseable {
    private final Driver driver;

    public Neo4jService(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public List<String> queryGraph(String cypher) {
        try (Session session = driver.session()) {
            return session.run(cypher)
                .list(record -> record.toString());
        }
    }

    @Override
    public void close() {
        driver.close();
    }
} 