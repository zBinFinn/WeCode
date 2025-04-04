package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.templates.Arguments;
import dev.dfonline.flint.templates.CodeBlock;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.templates.VariableScope;
import dev.dfonline.flint.templates.argument.NumberArgument;
import dev.dfonline.flint.templates.argument.StringArgument;
import dev.dfonline.flint.templates.argument.TextArgument;
import dev.dfonline.flint.templates.argument.VariableArgument;
import dev.dfonline.flint.templates.argument.abstracts.Argument;
import dev.dfonline.flint.templates.codeblock.*;
import dev.dfonline.flint.templates.codeblock.abstracts.CodeBlockIfStatement;
import dev.dfonline.flint.templates.codeblock.abstracts.CodeBlockWithArguments;
import dev.dfonline.flint.templates.codeblock.target.PlayerTarget;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.action_dump.ActionDump;
import org.zbinfinn.wecode.action_dump.DumpAction;
import org.zbinfinn.wecode.template_editor.token.Token;
import org.zbinfinn.wecode.template_editor.token.TokenType;

import java.util.*;

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
    private State state = new State();
    private final Stack<Bracket.Type> bracketTypeStack = new Stack<>();
    
    private static class State {
        public CodeBlock block = null;
        public Arguments arguments = new Arguments();
        public int argumentIndex = 0;
        public String group = "";
        public String target = "";
        public boolean not = false;
    }
    
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
                case TARGET: parseTarget(); break;
                case ACTION_TYPE: parseActionType(); break;
                case NOT: state.not = true; consume(); break;
                case ACTION: parseAction(); break;
                case CLOSE_CURLY: parseCloseCurly(); break;
                default: consume(); break;
            }
        }

        return template;
    }

    private void parseCloseCurly() {
        var type = bracketTypeStack.pop();
        addBlock(new Bracket(type, Bracket.Direction.CLOSE));
        consume();
    }

    private void parseTarget() {
        Token token = consume();
        state.target = token.value;
    }

    private void parseGroup() {
        var actions = WeCode.ACTION_DUMP.actions.getGroups();
        state.group = "PA";
        for (Map.Entry<String, Set<String>> entry : actions.entrySet()) {
            if (entry.getValue().contains(peek().value)) {
                state.group = Tokenizer.ACTION_SPECIFIERS.inverse().get(entry.getKey());
                System.out.println("New Current group: " + state.group);
                break;
            }
        }

    }

    private void parseAction() {
        if (peek().value.equals("Else")) {
            addBlock(new Else());
            consume();
            return;
        }
        if (state.group.isEmpty()) {
            parseGroup();
        }

        Token token = consume();

        if (peekOpt().isPresent() && peekOpt().get().type == TokenType.NOT) {
            consume();
            state.not = true;
        }


        var groupMaps = WeCode.ACTION_DUMP.actions.getGroupsMaps();
        var groupName = Tokenizer.ACTION_SPECIFIERS.get(state.group);
        System.out.println("GROUP NAME: " + groupName);
        var groupMap = groupMaps.get(groupName);
        DumpAction action = groupMap.get(token.value);
        String realName = (action == null) ? token.value : action.nameWithSpaces();
        System.out.println("Name: '" + token.value + "' vs Real: '" + realName + "'");

        switch (state.group) {
            case "FN": {
                parseBlockWithArguments(new Function(realName));
                break;
            }
            case "CF": {
                parseBlockWithArguments(new CallFunction(realName));
                break;
            }
            case "SV": {
                parseBlockWithArguments(new SetVariable(realName));
                break;
            }
            case "IG": {
                parseBlockWithArguments(new IfGame(realName, state.not));
            }
            case "CT": {
                parseBlockWithArguments(new Control(realName));
                break;
            }
            case "PA": {
                parseBlockWithArguments(new PlayerAction(realName, PlayerTarget.fromString(state.target)));
                break;
            }
            case "IP": {
                parseBlockWithArguments(new IfPlayer(realName, PlayerTarget.fromString(state.target), state.not));
                break;
            }
            case "RP": {
                parseBlockWithArguments(new Repeat(realName, null, state.not));
                break;
            }
        }
    }

    private void parseBlockWithArguments(CodeBlock block) {
        state.block = block;
        parseArguments();
        ((CodeBlockWithArguments) state.block).setArguments(state.arguments);
        addBlock(state.block);
    }

    private void addBlock(CodeBlock block) {
        if (!state.target.isEmpty()) {
            // TODO: Uncomment
            //if (block instanceof PlayerAction pa) pa.setTarget(state.target);
            //if (block instanceof IfPlayer ip) ip.setTarget(state.target);
            //if (block instanceof EntityAction pa) pa.setTarget(state.target);
            //if (block instanceof IfEntity ie) ie.setTarget(state.target);
        }

        template.addBlock(block);
        System.out.println("Added Block: " + block);

        String group = state.group;
        state = new State();

        if (peekOpt().isPresent() && peekOpt().get().type == TokenType.OPEN_CURLY) {
            bracketTypeStack.add(switch (group) {
                case "IP", "IE", "IV", "IG" -> Bracket.Type.NORMAL;
                case "RP" -> Bracket.Type.REPEAT;
                default -> Bracket.Type.NORMAL;
            });
            consume();
            addBlock(new Bracket(bracketTypeStack.getLast(), Bracket.Direction.OPEN));
        }
    }

    private void parseArguments() {
        if (peekOpt().isEmpty()) {
            throw failedArguments();
        }
        consumeOrThrow(TokenType.OPEN_PAREN, failedArguments());

        state.arguments = new Arguments();
        state.argumentIndex = 0;
        while (peekOpt().isPresent() && peek().type != TokenType.CLOSE_PAREN) {
            parseArgument();
            consume();
        }

        consumeOrThrow(TokenType.CLOSE_PAREN, failedArguments());
    }

    private void parseArgument() {
        switch (peek().type) {
            case STRING_LIT: {
                addArgument(new StringArgument(state.argumentIndex++, peek().value));
                break;
            }
            case VARIABLE: {
                VariableScope scope;
                String name;
                System.out.println(peek().value);
                if (peek().value.endsWith("@s")) {
                    name = peek().value.substring(0, peek().value.length() - 2);
                    scope = VariableScope.SAVE;
                } else if (peek().value.endsWith("@g")) {
                    name = peek().value.substring(0, peek().value.length() - 2);
                    scope = VariableScope.GAME;
                } else if (peek().value.endsWith("@l")) {
                    name = peek().value.substring(0, peek().value.length() - 2);
                    scope = VariableScope.LOCAL;
                } else if (peek().value.endsWith("@li")) {
                    name = peek().value.substring(0, peek().value.length() - 3);
                    scope = VariableScope.LINE;
                } else {
                    throw failedArgument();
                }
                addArgument(new VariableArgument(state.argumentIndex++, name, scope));
                break;
            }
            case COMPONENT_LIT: {
                addArgument(new TextArgument(state.argumentIndex++, peek().value));
                break;
            }
            case NUMBER_LIT: {
                addArgument(new NumberArgument(state.argumentIndex++, peek().value));
                break;
            }
            default: throw failedArgument();
        }
    }

    private void addArgument(Argument arg) {
        state.arguments.add(arg);
    }

    private Token consumeOrThrow(TokenType expected, ParseException exception) {
        if (peek().type == expected) {
            return consume();
        }
        throw exception;
    }

    private void parseActionType() {
        state.group = peek().value;
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
