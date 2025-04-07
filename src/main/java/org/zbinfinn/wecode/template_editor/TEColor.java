package org.zbinfinn.wecode.template_editor;

import org.zbinfinn.wecode.template_editor.token.TokenType;

public enum TEColor {
    DEFAULT(0xFFFFFF),
    TARGET(0x88CC88),
    ACTION_TYPE(0x8888FF),
    ACTION(0xCC88FF),
    NOT(0xFFFF88),
    PAREN(0xCC8888),

    STRING(0x88FFCC),
    COMPONENT(0xAAFF88),
    VARIABLE(0xFFCC88),
    INTEGER(0xFF8888),
    LOCATION(0x88FF88),
    VECTOR(0x88AAFF),
    ITEM(0xCCCCFF),
    SOUND(0x88BBFF),

    TAG(0xFFFF88),
    HINT(0x888888),

    PLACEHOLDER(0xFF8888),


    COMMENT(0x888888),

    SUGGESTION_EXTRA(0xCC8888),
    SUGGESTION_TEXT(0x888888),
    SUGGESTION_HIGHLIGHT(0xFFFFFF),
    SUGGESTION_BACKGROUND(0xFF000000),
    ;

    private final int value;

    TEColor(int color) {
        value = color;
    }

    public static int fromType(TokenType type) {
        return switch (type) {
            case PLACEHOLDER -> PLACEHOLDER.value;
            case HINT_LIT -> HINT.value;
            case TAG_LIT -> TAG.value;
            case ITEM_LIT -> ITEM.value;
            case VECTOR_LIT -> VECTOR.value;
            case LOCATION_LIT -> LOCATION.value;
            case ATTRIBUTE_NOT -> NOT.value;
            case OPEN_PAREN, CLOSE_PAREN,
                 OPEN_CURLY, CLOSE_CURLY -> PAREN.value;
            case SOUND_LIT -> SOUND.value;
            case COMMENT -> COMMENT.value;
            case NUMBER_LIT -> INTEGER.value;
            case TARGET -> TARGET.value;
            case ACTION -> ACTION.value;
            case STRING_LIT -> STRING.value;
            case COMPONENT_LIT -> COMPONENT.value;
            case VARIABLE -> VARIABLE.value;
            case ACTION_TYPE -> ACTION_TYPE.value;
            default -> DEFAULT.value;
        };
    }

    public int value() {
        return value;
    }
}
