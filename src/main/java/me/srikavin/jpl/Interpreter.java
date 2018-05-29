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

import me.srikavin.jpl.data.ConditionalType;
import me.srikavin.jpl.data.NodeVisitor;
import me.srikavin.jpl.data.jpl.*;
import me.srikavin.jpl.data.node.*;
import me.srikavin.jpl.data.node.Number;

import java.util.HashMap;
import java.util.Map;

public class Interpreter implements NodeVisitor {

    private String output = "";
    private Map<String, Variable> variables;

    public Interpreter() {
        this(new HashMap<>());
    }

    public Interpreter(Map<String, Variable> variables) {
        this.variables = variables;
    }

    private JPLNumber numberOperation(BinaryOperator node) {
        JPLDataType visitLeft = visit(node.left);
        JPLDataType visitRight = visit(node.right);
        double left = visitLeft.asNumber().getValue();
        double right = visitRight.asNumber().getValue();
        Double toReturn = null;
        if (node.token.getType() == TokenType.OP_PLUS) {
            toReturn = left + right;
        } else if (node.token.getType() == TokenType.OP_MINUS) {
            toReturn = left - right;
        } else if (node.token.getType() == TokenType.OP_MULTIPLY) {
            toReturn = left * right;
        } else if (node.token.getType() == TokenType.OP_DIVIDE) {
            toReturn = left / right;
        } else if (node.token.getType() == TokenType.OP_EXPONENT) {
            toReturn = Math.pow(left, right);
        }
        if (toReturn == null) {
            throw new RuntimeException(
                    "Operator: " + node.token.getType().toString() + " not found!");
        }
        return new JPLNumber(toReturn);
    }

    private JPLDataType assignVariable(BinaryOperator node) {
        VariableOperator noOp;
        Node notNoOp;
        if (node.left instanceof VariableOperator) {
            noOp = (VariableOperator) node.left;
            notNoOp = node.right;
        } else if (node.right instanceof VariableOperator) {
            noOp = (VariableOperator) node.right;
            notNoOp = node.left;
        } else {
            throw new RuntimeException("Not assigning to a variable!");
        }
        String name = noOp.name;
        JPLDataType toRet = visit(notNoOp);
        Variable v = new Variable(node.token, name, toRet, new Node());

        variables.put(name, v);
        return toRet;
    }

    @Override
    public JPLDataType visitTriOp(TrinaryOperator node) {
        if (node.token.getType() == TokenType.TERNARY_START) {
            JPLBoolean cond = visit(node.left).asBoolean();
            if (cond.getValue()) {
                return visit(node.center);
            } else {
                return visit(node.right);
            }
        }
        if (node instanceof ForOperator) {
            if (node.token.getType() == TokenType.KEYWORD_FOR) {
                if (node.left instanceof BinaryOperator) {
                    if (((BinaryOperator) node.left).left instanceof VariableOperator) {
                        visit(node.left);
                        String name = ((VariableOperator) ((BinaryOperator) node.left).left).name;
                        JPLBoolean cond = visit(node.center).asBoolean();
                        while (cond.getValue()) {
                            visit(node.right);
                            cond = visit(node.center).asBoolean();
                            visit(((ForOperator) node).statements);
                        }
                        variables.remove(name);
                        return new JPLNull();
                    }
                }
            }
        }
        throw new RuntimeException("Unknown Operator: " + node.token.getType());
    }

    @Override
    public JPLDataType visitBinOp(BinaryOperator node) {
        if (node.token.getType() == TokenType.ASSIGN) {
            return assignVariable(node);
        } else if (node.token.hasBooleanOperator()) {
            return booleanOperation(node);
        } else if (node.token.hasNumericOperator()) {
            return numberOperation(node);
        } else if (node.token.getType() == TokenType.STRING_CONCATENATE) {
            return new JPLString(
                    visit(node.left).asString().toString() + visit(node.right).asString().toString());
        } else {
            throw new RuntimeException("Unknown Operator: " + node.token.getType());
        }
    }

    @Override
    public JPLDataType visitNum(Number node) {
        return new JPLNumber(node.num);
    }

    @Override
    public JPLDataType visitVarOp(VariableOperator node) {
        if (node.assigning) {
            Variable v = variables.get(node.name);
            Variable newVar = new Variable(v.token, v.name, visit(v.node), v.node);
            variables.put(v.name, newVar);
            return newVar.value;
        } else {
            Variable a = getVariable(node.name);
            if (a != null && a.value instanceof JPLArray) {
                return a.value.asString();
            }
            if (a != null) {
                return a.value;
            }
        }
        throw new RuntimeException("Variable " + node.name + " not initialized!");
    }

    @Override
    public JPLDataType visitLiteral(Literal node) {
        return new JPLString(Character.toString(node.value));
    }

    @Override
    public JPLDataType visitArrayOp(ArrayOperator node) {
        Variable v = getVariable(node.varName);
        if (v == null || v.value == null) {
            throw new RuntimeException("Variable not initialized!");
        }
        if (!(v.value instanceof JPLArray)) {
            throw new RuntimeException("Cannot access properties of non arrays!");
        }
        if (node.assigning) {
            JPLArray array = (JPLArray) v.value;
            array.set(node.key.asString().toString(), visit(node.value));
            Variable n = new Variable(v.token, node.varName, array, node);
            variables.put(node.varName, n);
        } else {
            return ((JPLArray) variables.get(node.varName).value)
                    .get(node.key.asString().toString());
        }
        return getVariable(node.varName).value.asString();
    }

    @Override
    public JPLDataType visitCompound(Compound node) {
        if (node.statements.size() == 1) {
            return visit(node.statements.get(0));
        }
        for (Node e : node.statements) {
            visit(e);
        }
        return new JPLNull();
    }

    public JPLDataType visitUnOp(UnaryOperator node) {
        Token operator = node.token;
        if (operator.getType() == TokenType.OP_PLUS) {
            return new JPLNumber(+(visit(node.node).asNumber().getValue()));
        } else if (operator.getType() == TokenType.OP_MINUS) {
            return new JPLNumber(-(visit(node.node).asNumber().getValue()));
        } else if (operator.getType() == TokenType.KEYWORD_ECHO) {
            String eval = visit(node.node).asString().toString();
            System.out.println(eval);
            output += eval + "\n";
            return new JPLNumber(0);
        } else if (operator.getType() == TokenType.OP_NOT) {
            return new JPLBoolean(!visit(node.node).asBoolean().getValue());
        }
        throw new RuntimeException("Unknown unary operator!");
    }

    @Override
    public JPLDataType visitNoOp(NoOperator node) {
        if (node.type == NoOpType.BOOLEAN) {
            return node.value;
        }
        if (node.type == NoOpType.STRING) {
            return node.value;
        }
        if (node.type == NoOpType.ARRAY) {
            return node.value;
        }
        if (node.type == NoOpType.NONE) {
            return node.value;
        }
        throw new RuntimeException(
                "Unknown no operator type:" + node.getClass().getCanonicalName());
    }

    @Override
    public JPLDataType visitConditional(ConditionalNode node) {
        if (node.type == ConditionalType.IF) {
            if (visit(node.conditional).asBoolean().getValue()) {
                visit(node.nodes);
            }

        } else if (node.type == ConditionalType.WHILE) {
            while (visit(node.conditional).asBoolean().getValue()) {
                visit(node.nodes);
            }
        }
        return new JPLNull();
    }

    private JPLDataType booleanOperation(BinaryOperator node) {
        switch (node.token.getType()) {
            case OP_NOT_EQUAL:
                return new JPLBoolean(!visit(node.left).equals(visit(node.right)));
            case OP_EQUALS:
                return new JPLBoolean(visit(node.left).equals(visit(node.right)));
            case OP_LT:
                return new JPLBoolean(
                        visit(node.left).asNumber().getValue() < visit(node.right).asNumber()
                                .getValue());
            case OP_LTE:
                return new JPLBoolean(
                        visit(node.left).asNumber().getValue() <= visit(node.right).asNumber()
                                .getValue());
            case OP_GT:
                return new JPLBoolean(
                        visit(node.left).asNumber().getValue() > visit(node.right).asNumber()
                                .getValue());
            case OP_GTE:
                return new JPLBoolean(
                        visit(node.left).asNumber().getValue() >= visit(node.right).asNumber()
                                .getValue());
        }
        throw new RuntimeException("Unknown Operator: " + node.token.getType());
    }

    private Variable getVariable(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        return null;
    }

    public String getOutput() {
        return output;
    }

}
