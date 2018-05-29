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

import me.srikavin.jpl.exception.ParseException;

import java.util.List;

public class TokenSequence {
    private int startIndex;
    private int endIndex;
    private int currentIndex;
    private List<Token> tokens;

    public TokenSequence(List<Token> tokens, int startIndex, int endIndex) {
        this.tokens = tokens;
        if (endIndex > tokens.size()) {
            throw new RuntimeException("End index is over token list size!");
        }
        if (startIndex < 0) {
            throw new RuntimeException("Start index is negative!");
        }

        this.startIndex = startIndex;
        this.currentIndex = startIndex;
        this.endIndex = endIndex;
    }

    public void reset() {
        currentIndex = startIndex;
    }

    public Token peek() {
        if (currentIndex < endIndex) {
            return tokens.get(currentIndex);
        }
        return new Token(TokenType.EOF, "", 1, currentIndex);
    }

    public boolean finished() {
        return currentIndex >= endIndex - 1;
    }

    public Token getNext() {
        if (currentIndex <= endIndex) {
            Token cur = tokens.get(currentIndex);
            currentIndex++;
            return cur;
        }
        return new Token(TokenType.EOF, "", 1, currentIndex);
    }

    public Token advance(TokenType tokenType) {
        Token token = getNext();
        if (token.getType() != tokenType) {
            throw new ParseException(0, currentIndex,
                    String.format("Unexpected character at index %d! Expected: %s, actual: %s",
                            currentIndex, tokenType, token.getType()));
        }
        return token;
    }
}
