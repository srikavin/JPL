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

import me.srikavin.jpl.data.node.Number;
import me.srikavin.jpl.data.node.*;

import java.util.*;

public class DefaultParser implements Parser {
    Map<TokenType, TokenParser> indicatingTypeParserMap;

    public DefaultParser(Map<TokenType, TokenParser> indicatingTypeParserMap) {
        this.indicatingTypeParserMap = indicatingTypeParserMap;
        registerDefaults();
    }

    public DefaultParser() {
        this(new HashMap<>());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            Lexer lexer = new JPLLexer(line);
            TokenSequence sequence = lexer.lex();
            Parser parser = new DefaultParser();

            Node node = parser.parseAll(sequence);
            Interpreter interpreter = new Interpreter();
            System.out.println(interpreter.visit(node).asString().toString());
            System.out.println(node);
        }
    }

    private void registerDefaults() {
        registerNodeParser(new VariableOperator.VariableTokenParser());
        registerNodeParser(new NoOperator.ValuesTokenParser());
        registerNodeParser(new Number.NumberTokenParser());
        registerNodeParser(new BinaryOperator.ArithmeticOperatorParser());
    }

    @Override
    public void registerNodeParser(TokenParser tokenParser) {
        TokenType[] indicatingTypes = tokenParser.getIndicatingType();
        for (TokenType e : indicatingTypes) {
            indicatingTypeParserMap.put(e, tokenParser);
        }
    }

    @Override
    public Compound parseAll(TokenSequence tokenSequence) {
        List<Node> nodes = new ArrayList<>();
        Node last = new Node();
        while (!tokenSequence.finished()) {
            last = parse(tokenSequence, last);
            nodes.add(last);
            if (tokenSequence.peek().getType() == TokenType.SEMI) {
                tokenSequence.advance(TokenType.SEMI);
            }
        }
        return new Compound(nodes);
    }

    @Override
    public Node parse(TokenSequence tokenSequence, Node last) {
        System.out.println("1" + tokenSequence.peek().getType());
        while (tokenSequence.peek().getType() != TokenType.SEMI && tokenSequence.peek().getType() != TokenType.EOF) {
            TokenType tokenType = tokenSequence.peek().getType();
            TokenParser parser = indicatingTypeParserMap.get(tokenType);

            System.out.println(tokenType);

            if (parser == null) {
                throw new RuntimeException("Unknown token found: " + tokenType);
            }

            last = parser.parse(this, tokenSequence, last);
        }

        tokenSequence.advance(TokenType.SEMI, TokenType.EOF);
        return last;
    }
}
