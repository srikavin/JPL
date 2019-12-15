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

public class Number extends Node {
    public final double num;
    public final Token token;

    public Number(double num, Token token) {
        this.num = num;
        this.token = token;
    }

    public static class NumberTokenParser extends TokenParser {
        private static final TokenType[] indicatingTypes = new TokenType[]{TokenType.TYPE_NUMBER};

        @Override
        public Node parse(Parser parser, TokenSequence sequence, Node last) {
            Token number = sequence.advance(TokenType.TYPE_NUMBER);
            return new Number(Double.parseDouble(number.getValue()), number);
        }

        @Override
        public TokenType[] getIndicatingType() {
            return indicatingTypes;
        }
    }

}
