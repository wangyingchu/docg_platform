package org.neo4j.cypherdsl.core;

import static org.apiguardian.api.API.Status.*;

import java.util.Collections;

import org.apiguardian.api.API;

@API(status = EXPERIMENTAL, since = "1.0")
public final class Functions2 {

    /**
     * @param node The named node to be counted
     * @return A function call for {@code datetime()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation datetime(Node node) {
        Assert.notNull(node, "The node parameter is required.");
        return datetime(node.getRequiredSymbolicName());
    }
    /**
     * Creates a function invocation for the {@code datetime()} function.
     * See <a href="https://neo4j.com/docs/cypher-manual/current/functions/aggregating/#functions-count">count</a>.
     *
     * @param expression An expression describing the things to count.
     * @return A function call for {@code datetime()} for an expression like {@link Cypher#asterisk()} etc.
     */
    public static FunctionInvocation datetime(Expression expression) {
        Assert.notNull(expression, "The expression to count is required.");
        return new FunctionInvocation("datetime", expression);
    }

    /**
     * @param node The named node to be counted
     * @return A function call for {@code keys()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation keys(Node node) {
        Assert.notNull(node, "The node parameter is required.");
        return keys(node.getRequiredSymbolicName());
    }
    /**
     * Creates a function invocation for the {@code keys()} function.
     * See <a href="https://neo4j.com/docs/cypher-manual/current/functions/aggregating/#functions-count">count</a>.
     *
     * @param expression An expression describing the things to count.
     * @return A function call for {@code keys()} for an expression like {@link Cypher#asterisk()} etc.
     */
    public static FunctionInvocation keys(Expression expression) {
        Assert.notNull(expression, "The expression to count is required.");
        return new FunctionInvocation("keys", expression);
    }

    /**
     * @param node The named node to be counted
     * @return A function call for {@code properties()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation properties(Node node) {
        Assert.notNull(node, "The node parameter is required.");
        return properties(node.getRequiredSymbolicName());
    }
    /**
     * Creates a function invocation for the {@code properties()} function.
     * See <a href="https://neo4j.com/docs/cypher-manual/current/functions/aggregating/#functions-count">count</a>.
     *
     * @param expression An expression describing the things to count.
     * @return A function call for {@code properties()} for an expression like {@link Cypher#asterisk()} etc.
     */
    public static FunctionInvocation properties(Expression expression) {
        Assert.notNull(expression, "The expression to count is required.");
        return new FunctionInvocation("properties", expression);
    }


    private Functions2() {}
}
