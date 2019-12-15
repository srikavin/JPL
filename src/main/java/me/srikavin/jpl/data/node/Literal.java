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

package me.srikavin.jpl.data.node;

import me.srikavin.jpl.*;

public class Literal extends Node {
    public final char value;

    public Literal(char value) {
        this.value = value;
    }

    public static class LiteralTokenParser extends TokenParser {
        @Override
        public Node parse(Parser parser, TokenSequence sequence, Node last) {
            Token varName = sequence.advance(TokenType.VAR_NAME);

            Token assign = sequence.advance(TokenType.ASSIGN);
            Node val = parser.parse(sequence);
            return new BinaryOperator(new VariableOperator(varName.getValue(), true), assign, val);
        }

        @Override
        public TokenType[] getIndicatingType() {
            return new TokenType[]{TokenType.STRING_LITERAL};
        }
    }
}
