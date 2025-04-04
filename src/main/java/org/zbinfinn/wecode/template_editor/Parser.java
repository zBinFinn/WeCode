package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.template.ArgumentContainer;
import dev.dfonline.flint.template.Template;
import dev.dfonline.flint.template.block.BaseBlock;
import dev.dfonline.flint.template.block.CodeBlock;
import dev.dfonline.flint.template.block.impl.Control;
import dev.dfonline.flint.template.block.impl.Function;
import dev.dfonline.flint.template.block.impl.IfPlayer;
import dev.dfonline.flint.template.block.impl.PlayerAction;
import dev.dfonline.flint.template.value.Value;
import dev.dfonline.flint.template.value.VariableScope;
import dev.dfonline.flint.template.value.impl.NumberValue;
import dev.dfonline.flint.template.value.impl.StringValue;
import dev.dfonline.flint.template.value.impl.TextValue;
import dev.dfonline.flint.template.value.impl.VariableValue;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.template_editor.token.Token;
import org.zbinfinn.wecode.template_editor.token.TokenType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Parser {
    private static ParseException failedArguments() {
        return new ParseException("Failed to parse arguments");
    }
    private static ParseException failedArgument() {
        return new ParseException("Failed to parse argument");
    }

    private final List<Token> tokens;
    private int index;
    private Template template;
    private CodeBlock currentBlock;
    private ArgumentContainer currentArguments;
    private int currentArgumentIndex;
    private String currentGroup = "PA";

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Template parse() {
        index = 0;
        template = new Template();
        template.setAuthor("WeCode TEditor");
        template.setName("Exported");

        while (peekOpt().isPresent()) {
            WeCode.LOGGER.warn("Parsing: " + peek().debugString());
            switch (peek().type) {
                case EOL: consume(); break;
                case ACTION_TYPE: parseActionType(); break;
                case ACTION: parseAction(); break;
                default: consume(); break;
            }
        }

        return template;
    }

    private void parseGroup() {
        var actions = WeCode.ACTION_DUMP.actions.getGroups();
        currentGroup = "PA";
        for (Map.Entry<String, Set<String>> entry : actions.entrySet()) {
            if (entry.getValue().contains(peek().value)) {
                currentGroup = Tokenizer.ACTION_SPECIFIERS.inverse().get(entry.getKey());
                System.out.println("New Current group: " + currentGroup);
                break;
            }
        }

    }

    private void parseAction() {
        if (currentGroup.isEmpty()) {
            System.out.println("EMPTY GROUP");
            parseGroup();
        }
        Token token = consume();
        switch (currentGroup) {
            case "FN": {
                System.out.println("Parsing FN");
                parseBlockWithArguments(new Function(token.value));
                break;
            }
            case "PA": {
                System.out.println("Parsing PA");
                parseBlockWithArguments(new PlayerAction(token.value));
                break;
            }
            case "IP": {
                System.out.println("Parsing IP");
                parseBlockWithArguments(new IfPlayer(token.value));
                break;
            }
            case "CT": {
                System.out.println("Parsing CT");
                parseBlockWithArguments(new Control(token.value));
                break;
            }
        }
    }

    private void parseBlockWithArguments(BaseBlock action) {
        currentBlock = action;
        parseArguments();
        ((BaseBlock) currentBlock).setArguments(currentArguments);
        addBlock(currentBlock);
    }

    private void addBlock(CodeBlock block) {
        template.addBlock(block);
        System.out.println("Added Block: " + block);
        currentGroup = "";
        currentBlock = null;
        currentArgumentIndex = 0;
        currentArguments = new ArgumentContainer();
    }

    private void parseArguments() {
        if (peekOpt().isEmpty()) {
            throw failedArguments();
        }
        consumeOrThrow(TokenType.OPEN_PAREN, failedArguments());

        currentArguments = new ArgumentContainer();
        currentArgumentIndex = 0;
        while (peekOpt().isPresent() && peek().type != TokenType.CLOSE_PAREN) {
            parseArgument();
            consume();
        }

        consumeOrThrow(TokenType.CLOSE_PAREN, failedArguments());
    }

    private void parseArgument() {
        switch (peek().type) {
            case STRING_LIT: {
                addArgument(new StringValue(peek().value));
                break;
            }
            case VARIABLE: {
                addArgument(new VariableValue(peek().value, VariableScope.LINE));
                break;
            }
            case COMPONENT_LIT: {
                addArgument(new TextValue(peek().value));
                break;
            }
            case NUMBER_LIT: {
                addArgument(new NumberValue(peek().value));
                break;
            }
            default: throw failedArgument();
        }
    }

    private void addArgument(Value arg) {
        currentArguments.set(currentArgumentIndex++, arg);
    }

    private Token consumeOrThrow(TokenType expected, ParseException exception) {
        if (peek().type == expected) {
            return consume();
        }
        throw exception;
    }

    private void parseActionType() {
        currentGroup = peek().value;
        consume();
    }

    private Token consume() {
        Token consumed = tokens.get(index);
        System.out.println("Consumed: " + consumed.debugString());
        return tokens.get(index++);
    }
    private Optional<Token> peekOpt() {
        return peekOpt(0);
    }
    private Optional<Token> peekOpt(int ahead) {
        if (index + ahead >= tokens.size()) {
            return Optional.empty();
        }
        return Optional.of(tokens.get(index + ahead));
    }
    private Token peek(int ahead) {
        return tokens.get(index + ahead);
    }
    private Token peek() {
        return peek(0);
    }
}
