package org.neo4j.cypherdsl.core;

public class CustomContentLiteral extends LiteralBase<String> {

    private String content;

    public CustomContentLiteral(String content) {
        super(content);
        this.content = content;
    }

    @Override
    public String asString() {
        //return String.valueOf(getContent());
        return this.content;
    }
}
