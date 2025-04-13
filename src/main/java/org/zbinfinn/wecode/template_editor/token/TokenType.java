package org.zbinfinn.wecode.template_editor.token;

public enum TokenType {

    OPEN_CURLY,
    CLOSE_CURLY,

    TARGET,
    ACTION_TYPE,
    ACTION,

    ATTRIBUTE_NOT,
    ATTRIBUTE_LS_CANCEL,

    OPEN_PAREN,

    VARIABLE,
    TAG_LIT,
    STRING_LIT,
    TEXT_LIT,
    NUMBER_LIT,
    HINT_LIT,
    VECTOR_LIT,
    LOCATION_LIT,
    SOUND_LIT,
    ITEM_LIT,
    POTION_LIT,
    PARTICLE_LIT,
    PARAMETER_LIT,
    GAME_VALUE_LIT,
    EXPRESSION_LIT,

    EMPTY_ARGUMENTS,

    CLOSE_PAREN,

    EOL,
    SPACE,
    PLAIN,
    COMMENT,
    PLACEHOLDER

}
