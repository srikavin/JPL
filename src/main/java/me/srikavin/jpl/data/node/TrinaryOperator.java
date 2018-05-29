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

import me.srikavin.jpl.Token;

public class TrinaryOperator extends Node {

    public final Node left;
    public final Node right;
    public final Node center;
    public final Token token;


    public TrinaryOperator(Node left, Node center, Node right, Token token) {
        this.left = left;
        this.center = center;
        this.right = right;
        this.token = token;
    }

}
