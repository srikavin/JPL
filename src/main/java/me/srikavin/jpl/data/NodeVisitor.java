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

package me.srikavin.jpl.data;

import me.srikavin.jpl.JPLParser;
import me.srikavin.jpl.data.jpl.JPLDataType;
import me.srikavin.jpl.data.jpl.JPLNull;
import me.srikavin.jpl.data.node.ArrayOperator;
import me.srikavin.jpl.data.node.BinaryOperator;
import me.srikavin.jpl.data.node.Compound;
import me.srikavin.jpl.data.node.ConditionalNode;
import me.srikavin.jpl.data.node.Literal;
import me.srikavin.jpl.data.node.NoOperator;
import me.srikavin.jpl.data.node.Node;
import me.srikavin.jpl.data.node.Number;
import me.srikavin.jpl.data.node.TrinaryOperator;
import me.srikavin.jpl.data.node.UnaryOperator;
import me.srikavin.jpl.data.node.VariableOperator;

public interface NodeVisitor {
    JPLDataType visitBinOp(BinaryOperator node);

    JPLDataType visitNum(Number node);

    JPLDataType visitVarOp(VariableOperator node);

    default JPLDataType visit(Node node) {
        if (node instanceof Compound) {
            return visitCompound((Compound) node);
        } else if (node instanceof BinaryOperator) {
            return visitBinOp((BinaryOperator) node);
        } else if (node instanceof Number) {
            return visitNum((Number) node);
        } else if (node instanceof UnaryOperator) {
            return visitUnOp((UnaryOperator) node);
        } else if (node instanceof VariableOperator) {
            return visitVarOp((VariableOperator) node);
        } else if (node instanceof NoOperator) {
            return visitNoOp((NoOperator) node);
        } else if (node instanceof ConditionalNode) {
            return visitConditional((ConditionalNode) node);
        } else if (node instanceof ArrayOperator) {
            return visitArrayOp((ArrayOperator) node);
        } else if (node instanceof Literal) {
            return visitLiteral((Literal) node);
        } else if (node instanceof TrinaryOperator) {
            return visitTriOp((TrinaryOperator) node);
        } else if (node.getClass() == Node.class) {
            return new JPLNull();
        }
        throw new RuntimeException("No visitor found for " + node.getClass().getSimpleName());
    }

    JPLDataType visitTriOp(TrinaryOperator operator);

    JPLDataType visitLiteral(Literal node);

    JPLDataType visitArrayOp(ArrayOperator node);

    JPLDataType visitCompound(Compound node);

    default JPLDataType interpret(JPLParser parser) {
        return visit(parser.getNode());
    }

    JPLDataType visitUnOp(UnaryOperator node);

    JPLDataType visitNoOp(NoOperator node);

    JPLDataType visitConditional(ConditionalNode node);
}
