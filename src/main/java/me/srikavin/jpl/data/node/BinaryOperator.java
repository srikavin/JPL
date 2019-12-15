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

public class BinaryOperator extends Node {

    public final Node left;
    public final Node right;
    public final Token token;


    public BinaryOperator(Node left, Token token, Node right){
        this.left = left;
        this.right = right;
        this.token = token;
    }

    public static class ArithmeticOperatorParser extends TokenParser {
        private static final TokenType[] indicatingTypes = new TokenType[]{TokenType.OP_PLUS, TokenType.OP_MINUS, TokenType.OP_DIVIDE, TokenType.OP_MULTIPLY};

        @Override
        public Node parse(Parser parser, TokenSequence sequence, Node last) {
            Token type = sequence.advance(TokenType.OP_PLUS, TokenType.OP_MINUS, TokenType.OP_DIVIDE, TokenType.OP_MULTIPLY);

            Node val = parser.parse(sequence);

            System.out.println(last);
            return new BinaryOperator(last, type, val);
        }

        @Override
        public TokenType[] getIndicatingType() {
            return indicatingTypes;
        }
    }
}
