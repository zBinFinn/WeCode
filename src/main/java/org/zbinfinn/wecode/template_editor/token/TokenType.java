package org.zbinfinn.wecode.template_editor.token;

public enum TokenType {

    OPEN_CURLY,
    CLOSE_CURLY,

    TARGET,
    ACTION_TYPE,
    ACTION,
    NOT,

    OPEN_PAREN,

    VARIABLE,
    TAG_LIT,
    STRING_LIT,
    COMPONENT_LIT,
    NUMBER_LIT,
    HINT_LIT,
    EMPTY_ARGUMENTS,

    CLOSE_PAREN,

    EOL,
    SPACE,
    PLAIN,
    COMMENT,
    PLACEHOLDER

}
