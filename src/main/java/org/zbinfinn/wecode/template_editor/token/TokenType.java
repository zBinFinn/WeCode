package org.zbinfinn.wecode.template_editor.token;

public enum TokenType {
    OPEN_PAREN,
    CLOSE_PAREN,

    OPEN_CURLY,
    CLOSE_CURLY,

    ACTION,
    ACTION_TYPE,
    TARGET,

    VARIABLE,
    STRING_LIT,
    COMPONENT_LIT,
    NUMBER_LIT,

    SPACE,
    PLACEHOLDER,
    PLAIN,

    NOT,
    EOL,
    COMMENT
}
