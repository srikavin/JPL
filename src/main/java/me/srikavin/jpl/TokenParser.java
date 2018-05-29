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

import me.srikavin.jpl.data.node.Node;

public abstract class TokenParser {
    /**
     * Returns a Node from parsing this token sequence. May return null, if this sequence does not match what is expected.
     * Only tokens that are parsed should be advanced past.
     *
     * @param parser   A parser to use, allowing for generating other Nodes, inside of this Node
     * @param sequence The sequence to parse. It is expected that the start and end positions are not modified.
     *
     * @return The Node that resulted from parsing the token sequence
     */
    public abstract Node parse(Parser parser, TokenSequence sequence);

    /**
     * Returns the indicating type for this token parser. This is what indicates this parser should be triggered.
     * For example, the indicating type of a for loop is {@link TokenType#KEYWORD_FOR}
     *
     * @return This parser's indicating type
     */
    public abstract TokenType[] getIndicatingType();
}
