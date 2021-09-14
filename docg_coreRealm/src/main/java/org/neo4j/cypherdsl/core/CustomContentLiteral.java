package org.neo4j.cypherdsl.core;

import org.neo4j.cypherdsl.core.internal.LiteralBase;

public class CustomContentLiteral extends LiteralBase<String> {

    public CustomContentLiteral(String content) {
        super(content);
    }

    @Override
    public String asString() {
        return String.valueOf(getContent());
    }
}
