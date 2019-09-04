package token.type;

import token.TokenType;

public enum Operator implements TokenType {
    ASSIGNMENT("="),
    ASSIGNMENT_WITH_INCREASE("+="),
    ASSIGNMENT_WITH_DECREASE("-="),
    ASSIGNMENT_WITH_MULTIPLICATION("*="),
    ASSIGNMENT1("/="),
    ASSIGNMENT2("%=&="),
    ASSIGNMENT3("|="),
    ASSIGNMENT4("^="),
    ASSIGNMENT5("<<="),
    ASSIGNMENT6(">>="),
    ASSIGNMENT7(">>>=");

    private String value;

    Operator(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public TokenType getType() {
        return this;
    }
}
