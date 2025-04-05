package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.templates.Arguments;
import dev.dfonline.flint.templates.CodeBlock;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.templates.VariableScope;
import dev.dfonline.flint.templates.argument.*;
import dev.dfonline.flint.templates.argument.abstracts.Argument;
import dev.dfonline.flint.templates.codeblock.Bracket;
import dev.dfonline.flint.templates.codeblock.Else;
import dev.dfonline.flint.templates.codeblock.Function;
import dev.dfonline.flint.templates.codeblock.abstracts.CodeBlockAction;
import org.spongepowered.include.com.google.common.collect.HashBiMap;


public class TemplateParser {
    public static final HashBiMap<String, String> WECODE_ID_TO_FLINT_ID_MAP;
    static {
        WECODE_ID_TO_FLINT_ID_MAP = HashBiMap.create();
        WECODE_ID_TO_FLINT_ID_MAP.put("PE", "player_event"); // Player Event
        WECODE_ID_TO_FLINT_ID_MAP.put("PA", "player_action"); // Player Action
        WECODE_ID_TO_FLINT_ID_MAP.put("IP", "if_player"); // If Player

        WECODE_ID_TO_FLINT_ID_MAP.put("EE", "entity_event"); // Entity Event
        WECODE_ID_TO_FLINT_ID_MAP.put("EA", "entity_action"); // Entity Action
        WECODE_ID_TO_FLINT_ID_MAP.put("IE", "if_entity"); // If Entity

        WECODE_ID_TO_FLINT_ID_MAP.put("SV", "set_var"); // Set Variable
        WECODE_ID_TO_FLINT_ID_MAP.put("IV", "if_var"); // If Variable

        WECODE_ID_TO_FLINT_ID_MAP.put("GA", "game_action"); // Game Action
        WECODE_ID_TO_FLINT_ID_MAP.put("IG", "if_game"); // If Game

        WECODE_ID_TO_FLINT_ID_MAP.put("SO", "select_object"); // Select Object

        // Else Doesn't Have One it's just "Else"

        WECODE_ID_TO_FLINT_ID_MAP.put("FN", "func"); // Function (Always needs to be specified)
        WECODE_ID_TO_FLINT_ID_MAP.put("CF", "call_func"); // Call Function (Always needs to be specified)

        WECODE_ID_TO_FLINT_ID_MAP.put("PC", "process"); // Process (Always needs to be specified)
        WECODE_ID_TO_FLINT_ID_MAP.put("SP", "start_process"); // Start Process (Always needs to be specified)

        WECODE_ID_TO_FLINT_ID_MAP.put("CT", "control"); // Control

        WECODE_ID_TO_FLINT_ID_MAP.put("RP", "repeat"); // Repeat
    }
    private final Template template;
    private final StringBuilder builder = new StringBuilder();
    private int indentation = 0;
    public TemplateParser(Template template) {
        this.template = template;
    }
    public String parse() {

        for (CodeBlock block : template.getBlocks().getBlocks()) {
            if (block instanceof Function fun) {
                System.out.println("FUNCTION FUNCTION FOUND: " + fun.getArguments().getOrderedList().getLast());
            }
        }

        builder.append("// Parser Comment For Testing\n");
        for (int i = 0; i < template.getBlocks().getBlocks().size(); i++) {
            CodeBlock block = template.getBlocks().getBlocks().get(i);
            CodeBlock peek = (i + 1 < template.getBlocks().getBlocks().size()) ? template.getBlocks().getBlocks().get(i + 1) : null;
            String code = blockToCode(block);
            if (block != null && !(block instanceof Bracket bracket && bracket.getDirection() == Bracket.Direction.OPEN)) {
                builder.append(" ".repeat(TemplateEditor.TAB_SPACES * indentation));
            }
            builder.append(code);
            if (peek != null && !(peek instanceof Bracket bracket && bracket.getDirection() == Bracket.Direction.OPEN)) {
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    private String blockToCode(CodeBlock block) {
        if (block instanceof Else) {
            return "Else";
        }
        if (block instanceof Bracket bracket) {
            if (bracket.getDirection() == Bracket.Direction.OPEN) {
                indentation++;
                return "{";
            } else {
                indentation--;
                return "}";
            }
        }
        if (block instanceof Function function) {
            return blockWithArgsToString(function.getBlock(), function.getFunctionName(), function.getArguments());
        }
        if (block instanceof CodeBlockAction baseBlock) {
            return baseBlockToCode(baseBlock);
        }

        return "EndOfBlockToCode";
    }

    private String baseBlockToCode(CodeBlockAction baseBlock) {
        return blockWithArgsToString(baseBlock.getBlock(), baseBlock.getAction(), baseBlock.getArguments());
    }

    private String blockWithArgsToString(String block, String action, Arguments arguments) {
        return WECODE_ID_TO_FLINT_ID_MAP.inverse().get(block) + " " + getActionString(action) + argumentsToCode(arguments);
    }

    private String getActionStringFromAction(CodeBlockAction action) {
        return getActionString(action.getAction());
    }

    private String getActionString(String action) {
        if (action.isEmpty()) {
            return "'' ";
        }
        if (!Character.isAlphabetic(action.toCharArray()[0])) {
            return "'" + action + "'";
        }
        return action;
    }

    private String argumentsToCode(Arguments container) {
        var list = container.getOrderedListWithEmpties();
        StringBuilder arguments = new StringBuilder();
        arguments.append('(');
        int empties = 0;
        for (int i = 0; i < 27; i++) {
            Argument item = list.get(i);
            if (item == null) {
                empties++;
                continue;
            }
            if (empties > 0) {
                arguments.append((i-empties==0) ? "!" : " !").append(empties).append("!");
                empties = 0;
            }
            arguments.append((i==0) ? "" : " ").append(valueToCode(item));
        }
        arguments.append(')');
        return arguments.toString();
    }

    private String valueToCode(Argument item) {
        if (item instanceof NumberArgument number) {
            return number.getNumber();
        }
        if (item instanceof StringArgument string) {
            return '"' + string.getValue() + '"';
        }
        if (item instanceof TextArgument text) {
            return "$\"" + text.getValue() + '"';
        }
        if (item instanceof VariableArgument variable) {
            return (Character.isAlphabetic(variable.getName().toCharArray()[0]) ? variable.getName() : "[" + variable.getName() + "]") + (getVariableScopeText(variable));
        }
        if (item instanceof TagArgument tag) {
            System.out.println("Found Tag: " + tag.getTag());
            return "T\"" + tag.getOption() + "\"";
        }
        if (item instanceof HintArgument hint) {
            return "H\"" + hint.getType().getID() + "\"";
        }

        return "FailedArgumentParsing";
    }

    private String getVariableScopeText(VariableArgument variable) {
        if (variable.getScope() == VariableScope.LINE) {
            return "";
        }
        return switch (variable.getScope()) {
            case SAVE -> "@s";
            case GAME -> "@g";
            case LOCAL -> "@l";
            default -> "@error";
        };
    }
}
