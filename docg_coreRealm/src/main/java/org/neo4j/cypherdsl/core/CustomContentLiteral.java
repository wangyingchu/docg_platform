package org.neo4j.cypherdsl.core;

public class CustomContentLiteral extends Literal<String>{

    public CustomContentLiteral(String content) {
        super(content);
    }

    @Override
    public String asString() {
        return String.valueOf(getContent());
    }
}
