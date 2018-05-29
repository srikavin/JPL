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

import me.srikavin.jpl.data.node.Compound;
import me.srikavin.jpl.data.node.NoOperator;
import me.srikavin.jpl.data.node.Node;
import me.srikavin.jpl.data.node.VariableOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Lexer lexer = new JPLLexer("<!jpl var name = \"123\"; !>");
        TokenSequence sequence = lexer.lex();
        Parser parser = new DefaultParser();

        Node node = parser.parseAll(sequence);
        Interpreter interpreter = new Interpreter();
        System.out.println(interpreter.visit(node).asString().toString());
        System.out.println(node);
    }

    private void registerDefaults(){
        registerNodeParser(new VariableOperator.VariableTokenParser());
        registerNodeParser(new NoOperator.ValuesTokenParser());
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
        while (!tokenSequence.finished()) {
            nodes.add(parse(tokenSequence));
            if (tokenSequence.peek().getType() == TokenType.SEMI) {
                tokenSequence.advance(TokenType.SEMI);
            }
        }
        return new Compound(nodes);
    }

    @Override
    public Node parse(TokenSequence tokenSequence) {
        TokenType tokenType = tokenSequence.peek().getType();
        TokenParser parser = indicatingTypeParserMap.get(tokenType);

        System.out.println(tokenType);

        if (parser == null) {
            throw new RuntimeException("Unknown token found: " + tokenType);
        }

        return (parser.parse(this, tokenSequence));
    }
}
