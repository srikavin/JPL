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
import me.srikavin.jpl.data.jpl.JPLArray;
import me.srikavin.jpl.data.jpl.JPLBoolean;
import me.srikavin.jpl.data.jpl.JPLDataType;
import me.srikavin.jpl.data.jpl.JPLString;
import me.srikavin.jpl.exception.ParseException;

public class NoOperator extends Node {
    public final JPLDataType value;
    public final NoOpType type;
    public final Token token;

    public NoOperator(JPLDataType value, NoOpType type, Token token) {
        this.value = value;
        this.type = type;
        this.token = token;
    }

    public static class ValuesTokenParser extends TokenParser {
        @Override
        public Node parse(Parser parser, TokenSequence sequence) {
            Token token = sequence.peek();
            if (token.getType() == TokenType.KEYWORD_TRUE) {
                sequence.advance(TokenType.KEYWORD_TRUE);
                return new NoOperator(new JPLBoolean(true), NoOpType.BOOLEAN, token);
            } else if (token.getType() == TokenType.KEYWORD_FALSE) {
                sequence.advance(TokenType.KEYWORD_FALSE);
                return new NoOperator(new JPLBoolean(false), NoOpType.BOOLEAN, token);
            } else if (token.getType() == TokenType.STRING_LITERAL) {
                sequence.advance(TokenType.STRING_LITERAL);
                return new NoOperator(new JPLString(token.getValue()), NoOpType.STRING, token);
            } else if (token.getType() == TokenType.ARRAY_INITIALIZER) {
                sequence.advance(TokenType.ARRAY_INITIALIZER);
                return new NoOperator(new JPLArray(), NoOpType.ARRAY, token);
            }
            throw new ParseException(1, 1, "Unknown no op type: " + token.getType());
        }

        @Override
        public TokenType[] getIndicatingType() {
            return new TokenType[]{TokenType.KEYWORD_TRUE, TokenType.KEYWORD_FALSE, TokenType.STRING_LITERAL, TokenType.ARRAY_INITIALIZER};
        }
    }
}
