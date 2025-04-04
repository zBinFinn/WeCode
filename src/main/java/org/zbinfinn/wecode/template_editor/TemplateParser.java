package org.zbinfinn.wecode.template_editor;

import com.google.common.collect.BiMap;
import dev.dfonline.flint.template.ArgumentContainer;
import dev.dfonline.flint.template.Template;
import dev.dfonline.flint.template.block.BaseBlock;
import dev.dfonline.flint.template.block.CodeBlock;
import dev.dfonline.flint.template.block.impl.Bracket;
import dev.dfonline.flint.template.block.impl.Else;
import dev.dfonline.flint.template.value.Value;
import dev.dfonline.flint.template.value.impl.NumberValue;
import dev.dfonline.flint.template.value.impl.StringValue;
import dev.dfonline.flint.template.value.impl.TextValue;
import org.spongepowered.include.com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.Map;

public class TemplateParser {
    public static final HashBiMap<String, String> FLINT_ID_TO_WECODE_ID_MAP;
    static {
        FLINT_ID_TO_WECODE_ID_MAP = HashBiMap.create();
        FLINT_ID_TO_WECODE_ID_MAP.put("PE", "player_event"); // Player Event
        FLINT_ID_TO_WECODE_ID_MAP.put("PA", "player_action"); // Player Action
        FLINT_ID_TO_WECODE_ID_MAP.put("IP", "if_player"); // If Player

        FLINT_ID_TO_WECODE_ID_MAP.put("EE", "entity_event"); // Entity Event
        FLINT_ID_TO_WECODE_ID_MAP.put("EA", "entity_action"); // Entity Action
        FLINT_ID_TO_WECODE_ID_MAP.put("IE", "if_entity"); // If Entity

        FLINT_ID_TO_WECODE_ID_MAP.put("SV", "set_var"); // Set Variable
        FLINT_ID_TO_WECODE_ID_MAP.put("IV", "if_var"); // If Variable

        FLINT_ID_TO_WECODE_ID_MAP.put("GA", "game_action"); // Game Action
        FLINT_ID_TO_WECODE_ID_MAP.put("IG", "if_game"); // If Game

        FLINT_ID_TO_WECODE_ID_MAP.put("SO", "select_object"); // Select Object

        // Else Doesn't Have One it's just "Else"

        FLINT_ID_TO_WECODE_ID_MAP.put("FN", "func"); // Function (Always needs to be specified)
        FLINT_ID_TO_WECODE_ID_MAP.put("CF", "call_func"); // Call Function (Always needs to be specified)

        FLINT_ID_TO_WECODE_ID_MAP.put("PC", "process"); // Process (Always needs to be specified)
        FLINT_ID_TO_WECODE_ID_MAP.put("SP", "start_process"); // Start Process (Always needs to be specified)

        FLINT_ID_TO_WECODE_ID_MAP.put("CT", "control"); // Control

        FLINT_ID_TO_WECODE_ID_MAP.put("RP", "repeat"); // Repeat
    }
    private final Template template;
    private final StringBuilder builder = new StringBuilder();
    public TemplateParser(Template template) {
        this.template = template;
    }
    public String parse() {

        builder.append("// Parser Comment For Testing\n");
        for (CodeBlock block : template.getBlocks()) {
            System.out.println("Block!");
            builder.append(blockToCode(block));
            System.out.println("Block to code: " + blockToCode(block));
            if (!(block instanceof Bracket bracket && bracket.getDirect().equals(Bracket.Direction.OPEN.getValue()))) {
                builder.append('\n');
            }
        }

        System.out.println("Parsed: \n" + builder.toString());
        return builder.toString();
    }

    private String blockToCode(CodeBlock block) {
        if (block instanceof Else) {
            return "Else";
        }
        if (block instanceof BaseBlock baseBlock) {
            return baseBlockToCode(baseBlock);
        }

        return "EndOfBlockToCode";
    }

    private String baseBlockToCode(BaseBlock baseBlock) {
        return FLINT_ID_TO_WECODE_ID_MAP.inverse().get(baseBlock.getBlock()) + " " + baseBlock.getAction() + argumentsToCode(baseBlock.getArguments());
    }

    private String argumentsToCode(ArgumentContainer container) {
        StringBuilder arguments = new StringBuilder();
        arguments.append('(');
        int empties = 0;
        for (int i = 0; i < 27; i++) {
            Value item = container.get(i);
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

    private String valueToCode(Value item) {
        if (item instanceof NumberValue number) {
            return number.getValue();
        }
        if (item instanceof StringValue string) {
            return '"' + string.getText() + '"';
        }
        if (item instanceof TextValue text) {
            return "$\"" + text.getText() + '"';
        }

        return "FailedArgumentParsing";
    }
}
