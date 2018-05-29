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

public enum TokenType {
    // Line Comment:       # ...
    // Block Comment:      /* ... */
    KEYWORD_IF,            // if
    STRING_LITERAL,        // "
    KEYWORD_ELSE,          // else
    KEYWORD_ELSE_IF,       // elif
    KEYWORD_WHILE,         // while
    KEYWORD_FOR,           // for
    KEYWORD_VAR,           // var
    KEYWORD_ECHO,          // echo
    VAR_NAME,              // any alphabetic word (other than other keywords)
    ASSIGN,                // =
    LITERAL,               // escaped values - "\n"
    SEMI,                  // ;
    KEYWORD_TRUE,          // true
    KEYWORD_FALSE,         // false
    CURLY_BRACKET_LEFT,    // {
    CURLY_BRACKET_RIGHT,   // }
    TYPE_NUMBER,           // any numerical digit(s)
    STRING_CONCATENATE,    // .
    OP_PLUS,               // +
    OP_MINUS,              // -
    OP_MULTIPLY,           // *
    OP_DIVIDE,             // /
    OP_LT,                 // <
    OP_LTE,                // <=
    OP_GT,                 // >
    OP_GTE,                // >=
    OP_NOT,                // !
    OP_NOT_EQUAL,          // !=
    OP_EQUALS,             // ==
    OP_EXPONENT,           // ^
    PARENTHESIS_LEFT,      // (
    PARENTHESIS_RIGHT,     // )
    ARRAY_INITIALIZER,     // {} var a = {}
    ARRAY_KEY,             // [] a[1]
    TERNARY_START,         // ?
    TERNARY_SEPERATOR,     // :
    EOF,
}

