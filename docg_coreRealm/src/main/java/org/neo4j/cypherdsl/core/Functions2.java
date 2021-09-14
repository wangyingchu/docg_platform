package org.neo4j.cypherdsl.core;

import static org.apiguardian.api.API.Status.*;

import org.apiguardian.api.API;
import org.neo4j.cypherdsl.core.utils.Assertions;

@API(status = EXPERIMENTAL, since = "1.0")
public final class Functions2 {

    /**
     * @param node The named node to be counted
     * @return A function call for {@code datetime()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation datetime(Node node) {
        Assertions.notNull(node, "The node parameter is required.");
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
        Assertions.notNull(expression, "The expression to count is required.");
        FunctionInvocation.FunctionDefinition datetimeFunctionDefinition = new FunctionInvocation.FunctionDefinition() {
            @Override
            public String getImplementationName() {
                return "datetime";
            }
        };
        return FunctionInvocation.create(datetimeFunctionDefinition,expression);
    }

    /**
     * @param node The named node to be counted
     * @return A function call for {@code keys()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation keys(Node node) {
        Assertions.notNull(node, "The node parameter is required.");
        return keys(node.getRequiredSymbolicName());
    }

    /**
     * @param relationship The named relationship to be counted
     * @return A function call for {@code keys()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation keys(Relationship relationship) {
        Assertions.notNull(relationship, "The node parameter is required.");
        return keys(relationship.getRequiredSymbolicName());
    }

    /**
     * Creates a function invocation for the {@code keys()} function.
     * See <a href="https://neo4j.com/docs/cypher-manual/current/functions/aggregating/#functions-count">count</a>.
     *
     * @param expression An expression describing the things to count.
     * @return A function call for {@code keys()} for an expression like {@link Cypher#asterisk()} etc.
     */
    public static FunctionInvocation keys(Expression expression) {
        Assertions.notNull(expression, "The expression to count is required.");
        FunctionInvocation.FunctionDefinition keysFunctionDefinition = new FunctionInvocation.FunctionDefinition() {
            @Override
            public String getImplementationName() {
                return "keys";
            }
        };
        return FunctionInvocation.create(keysFunctionDefinition,expression);
    }

    /**
     * @param node The named node to be counted
     * @return A function call for {@code properties()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation properties(Node node) {
        Assertions.notNull(node, "The node parameter is required.");
        return properties(node.getRequiredSymbolicName());
    }

    /**
     * @param relationship The named node to be counted
     * @return A function call for {@code properties()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation properties(Relationship relationship) {
        Assertions.notNull(relationship, "The node parameter is required.");
        return properties(relationship.getRequiredSymbolicName());
    }

    /**
     * Creates a function invocation for the {@code properties()} function.
     * See <a href="https://neo4j.com/docs/cypher-manual/current/functions/aggregating/#functions-count">count</a>.
     *
     * @param expression An expression describing the things to count.
     * @return A function call for {@code properties()} for an expression like {@link Cypher#asterisk()} etc.
     */
    public static FunctionInvocation properties(Expression expression) {
        Assertions.notNull(expression, "The expression to count is required.");
        FunctionInvocation.FunctionDefinition propertiesFunctionDefinition = new FunctionInvocation.FunctionDefinition() {
            @Override
            public String getImplementationName() {
                return "properties";
            }
        };
        return FunctionInvocation.create(propertiesFunctionDefinition,expression);
    }

    /**
     * @param node The named node to be counted
     * @return A function call for {@code count()} for one named node
     * @see #count(Expression)
     */
    public static FunctionInvocation count(Node node) {
        Assertions.notNull(node, "The node parameter is required.");
        return count(node.getRequiredSymbolicName());
    }

    /**
     * @param relationship The named node to be counted
     * @return A function call for {@code count()} for one named node
     * @see #datetime(Expression)
     */
    public static FunctionInvocation count(Relationship relationship) {
        Assertions.notNull(relationship, "The node parameter is required.");
        return count(relationship.getRequiredSymbolicName());
    }

    /**
     * Creates a function invocation for the {@code properties()} function.
     * See <a href="https://neo4j.com/docs/cypher-manual/current/functions/aggregating/#functions-count">count</a>.
     *
     * @param expression An expression describing the things to count.
     * @return A function call for {@code count()} for an expression like {@link Cypher#asterisk()} etc.
     */
    public static FunctionInvocation count(Expression expression) {
        Assertions.notNull(expression, "The expression to count is required.");
        FunctionInvocation.FunctionDefinition countFunctionDefinition = new FunctionInvocation.FunctionDefinition() {
            @Override
            public String getImplementationName() {
                return "count";
            }
        };
        return FunctionInvocation.create(countFunctionDefinition,expression);
    }

    private Functions2() {}
}
