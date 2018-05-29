/*
 *    Copyright 2018 Srikavin Ramkumar
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.srikavin.jpl;

import me.srikavin.jpl.data.ConditionalType;
import me.srikavin.jpl.data.jpl.*;
import me.srikavin.jpl.data.node.*;
import me.srikavin.jpl.data.node.Number;
import me.srikavin.jpl.exception.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JPLParser {

    private Token currentToken;
    private JPLLexer lexer;

    public JPLParser(JPLLexer lexer) {
        this.lexer = lexer;
        currentToken = lexer.getNextToken();
    }

    public static void main(String[] a) {
        Interpreter interpreter = new Interpreter();

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("---");
        while (true) {
            while (scanner.hasNext()) {
                try {
                    String input = scanner.next();
                    JPLLexer lexer = new JPLLexer("<!jpl \n" + input + "\n !>");

                    Token t = lexer.getNextToken();
                    while (t != null) {
                        System.out.println(t.getType() + " - " + t.getValue());
                        if (t.getType() == TokenType.EOF) {
                            break;
                        }
                        t = lexer.getNextToken();
                    }

                    System.out.println("\n\n Parsing:");
                    lexer = new JPLLexer("<!jpl \n" + input + "\n !>");
                    JPLParser parser = new JPLParser(lexer);
                    System.out.println(interpreter.interpret(parser).asString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void eat(TokenType type) throws ParseException {
        if (currentToken.getType().equals(type)) {
            System.out.println(currentToken.getType() + " -  " + currentToken.getValue());
            currentToken = lexer.getNextToken();
            return;
        }
        throw new ParseException(currentToken.row, currentToken.column,
                "Expecting " + type + " got " + currentToken.getType());
    }

    private JPLDataType toJPLType(String toConvert) {
        JPLDataType toRet;
        try {
            toRet = new JPLNumber(Double.parseDouble(toConvert));
        } catch (NumberFormatException e) {
            switch (toConvert) {
                case "true":
                    toRet = new JPLBoolean(true);
                    break;
                case "false":
                    toRet = new JPLBoolean(false);
                    break;
                default:
                    toRet = new JPLString(toConvert);
                    break;
            }
        }
        return toRet;
    }

    private Node highPriority() {
        Token token = currentToken;

        if (token.getType() == TokenType.CURLY_BRACKET_LEFT) {
            eat(TokenType.CURLY_BRACKET_LEFT);
            List<Node> statements = new ArrayList<>();

            statements.add(parse());
            while (token.getType() != TokenType.CURLY_BRACKET_RIGHT) {
                statements.add(parse());
            }
            eat(TokenType.CURLY_BRACKET_RIGHT);
            return new Compound(statements);
        }

        if (token.getType() == TokenType.TYPE_NUMBER) {
            eat(TokenType.TYPE_NUMBER);
            return new Number(Double.valueOf(token.getValue()), token);
        } else if (token.getType() == TokenType.PARENTHESIS_LEFT) {
            eat(TokenType.PARENTHESIS_LEFT);
            Node node = parse();
            eat(TokenType.PARENTHESIS_RIGHT);
            return node;
        } else if (token.getType() == TokenType.LITERAL) {
            Character val = null;
            switch (token.getValue()) {
                case "\\n":
                    val = '\n';
                    break;
                case "\\\"":
                    val = '"';
                    break;
            }
            if (val == null) {
                throw new ParseException(token.row, token.column,
                        "Unknown escape sequence " + token.getValue());
            }
            return new Literal(val);
        } else if (token.getType() == TokenType.OP_PLUS) {
            eat(TokenType.OP_PLUS);
            return new UnaryOperator(token, parse());
        } else if (token.getType() == TokenType.OP_MINUS) {
            eat(TokenType.OP_MINUS);
            return new UnaryOperator(token, parse());
        } else if (token.getType() == TokenType.OP_NOT) {
            eat(TokenType.OP_NOT);
            return new UnaryOperator(token, parse());
        } else if (currentToken.getType() == TokenType.KEYWORD_VAR) {
            eat(TokenType.KEYWORD_VAR);
            if (currentToken.getType() == TokenType.VAR_NAME) {
                String name = currentToken.getValue();
                eat(TokenType.VAR_NAME);
                if (currentToken.getType() == TokenType.SEMI) {
                    token = currentToken;
                    Node val = parse();
                    eat(TokenType.SEMI);
                    return new Variable(token, name, null, val);
                } else if (currentToken.getType() == TokenType.ASSIGN) {
                    token = currentToken;
                    eat(TokenType.ASSIGN);
                    if (currentToken.getType() == TokenType.TYPE_NUMBER
                            || currentToken.getType() == TokenType.STRING_LITERAL
                            || currentToken.getType() == TokenType.VAR_NAME
                            || currentToken.getType() == TokenType.KEYWORD_FALSE
                            || currentToken.getType() == TokenType.KEYWORD_TRUE
                            || currentToken.getType() == TokenType.ARRAY_INITIALIZER
                    ) {
                        Node val = parse();
                        eat(TokenType.SEMI);
                        return new BinaryOperator(new VariableOperator(name, true), token, val);
                    }
                }
            }
            throw new ParseException(currentToken.row, currentToken.column);
        } else if (currentToken.getType() == TokenType.VAR_NAME) {
            String name = currentToken.getValue();
            eat(TokenType.VAR_NAME);
            if (currentToken.getType() == TokenType.ARRAY_KEY) {
                String key = currentToken.getValue();
                eat(TokenType.ARRAY_KEY);
                if (currentToken.getType() == TokenType.ASSIGN) {
                    eat(TokenType.ASSIGN);
                    Node val = parse();
                    eat(TokenType.SEMI);
                    return new ArrayOperator(true, toJPLType(key), name, val);
                }
                return new ArrayOperator(false, toJPLType(key), name);
            }
            if (currentToken.getType() == TokenType.ASSIGN) {
                token = currentToken;
                eat(TokenType.ASSIGN);
                if (currentToken.getType() == TokenType.TYPE_NUMBER
                        || currentToken.getType() == TokenType.VAR_NAME) {
                    Node val = parse();
                    eat(TokenType.SEMI);
                    return new BinaryOperator(new VariableOperator(name, true), token, val);
                }
            }
            return new VariableOperator(name, false);
        } else if (currentToken.getType() == TokenType.KEYWORD_ECHO) {
            eat(TokenType.KEYWORD_ECHO);
            Node val = parse();
            eat(TokenType.SEMI);
            return new UnaryOperator(token, val);
        }
        if (token.getType() == TokenType.KEYWORD_TRUE) {
            eat(TokenType.KEYWORD_TRUE);
            return new NoOperator(new JPLBoolean(true), NoOpType.BOOLEAN, token);
        } else if (token.getType() == TokenType.KEYWORD_FALSE) {
            eat(TokenType.KEYWORD_FALSE);
            return new NoOperator(new JPLBoolean(false), NoOpType.BOOLEAN, token);
        } else if (token.getType() == TokenType.STRING_LITERAL) {
            eat(TokenType.STRING_LITERAL);
            return new NoOperator(new JPLString(token.getValue()), NoOpType.STRING, token);
        } else if (token.getType() == TokenType.ARRAY_INITIALIZER) {
            eat(TokenType.ARRAY_INITIALIZER);
            return new NoOperator(new JPLArray(), NoOpType.ARRAY, token);
        }
        return new Node();
    }

    private Node mediumPriority() {
        Node node = highPriority();
        while (isBOperator(currentToken.getType())) {
            Token token = currentToken;
            TokenType type = token.getType();
            eat(type);
            node = new BinaryOperator(node, token, parse());
        }

        return node;
    }

    private boolean isBOperator(TokenType type) {
        TokenType[] types = {TokenType.OP_MULTIPLY, TokenType.OP_DIVIDE, TokenType.OP_EXPONENT};
        for (TokenType e : types) {
            if (e == type) {
                return true;
            }
        }
        return false;
    }

    private Node lowPriority() throws ParseException {
        Node node = mediumPriority();
        if (currentToken.getType() == TokenType.OP_PLUS ||
                currentToken.getType() == TokenType.OP_MINUS ||
                currentToken.getType() == TokenType.STRING_CONCATENATE) {
            Token token = currentToken;
            eat(currentToken.getType());
            node = new BinaryOperator(node, token, parse());
        }

        return node;
    }

    private Node lowerPriority() throws ParseException {
        Node node = lowPriority();
        if (currentToken.getType() == TokenType.OP_LT ||
                currentToken.getType() == TokenType.OP_LTE ||
                currentToken.getType() == TokenType.OP_GT ||
                currentToken.getType() == TokenType.OP_GTE ||
                currentToken.getType() == TokenType.OP_EQUALS ||
                currentToken.getType() == TokenType.OP_NOT_EQUAL) {
            Token token = currentToken;
            eat(currentToken.getType());
            node = new BinaryOperator(node, token, parse());
        } else if (currentToken.getType() == TokenType.TERNARY_START) {
            Token token = currentToken;
            eat(TokenType.TERNARY_START);
            Node val1 = parse();
            eat(TokenType.TERNARY_SEPERATOR);
            Node val2 = parse();
            node = new TrinaryOperator(node, val1, val2, token);
        }

        return node;
    }

    private Node lowestPriority(boolean first) throws ParseException {
        Node node = lowerPriority();
        List<Node> nodes = new ArrayList<>();
        nodes.add(node);

        if (currentToken.getType() == TokenType.KEYWORD_IF) {
            eat(TokenType.KEYWORD_IF);
            eat(TokenType.PARENTHESIS_LEFT);
            Node cond = parse();
            eat(TokenType.PARENTHESIS_RIGHT);
            Node statements = parse();
            nodes.add(new ConditionalNode(statements, cond, ConditionalType.IF));
        } else if (currentToken.getType() == TokenType.KEYWORD_WHILE) {
            eat(TokenType.KEYWORD_WHILE);
            eat(TokenType.PARENTHESIS_LEFT);
            Node cond = parse();
            eat(TokenType.PARENTHESIS_RIGHT);
            Node statements = parse();
            nodes.add(new ConditionalNode(statements, cond, ConditionalType.WHILE));
        } else if (currentToken.getType() == TokenType.KEYWORD_FOR) {
            Token start = currentToken;
            eat(TokenType.KEYWORD_FOR);
            eat(TokenType.PARENTHESIS_LEFT);
            Node varInit = parse();
            eat(TokenType.SEMI);
            Node cond = parse();
            eat(TokenType.SEMI);
            Node increment = parse();
            eat(TokenType.PARENTHESIS_RIGHT);
            Node statements = parse();
            nodes.add(new ForOperator(varInit, cond, increment, statements, start));
        } else {
//            eat(TokenType.EOF);
        }
        if (first) {
            while (currentToken.getType() != TokenType.EOF) {
                nodes.add(parse());
            }
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        return new Compound(nodes);
    }

    private Node parse(boolean first) throws ParseException {
        return lowestPriority(first);
    }

    public Node parse() throws ParseException {
        return parse(false);
    }

    public Node getNode() throws ParseException {
        Node n = parse(true);
        return n;
    }

}
