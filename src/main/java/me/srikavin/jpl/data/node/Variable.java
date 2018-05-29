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
import me.srikavin.jpl.data.jpl.JPLDataType;

public class Variable extends Node {
    public final Token token;
    public final String name;
    public final JPLDataType value;
    public final Node node;

    public Variable(Token token, String name, JPLDataType value, Node node) {
        this.token = token;
        this.name = name;
        this.value = value;
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Variable) {
            if (((Variable) o).name.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
