package com.javamonk.intellireview.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CodeParserService {

    public CodeStructure parseStructure(String codeSnippet) {
        CodeStructure structure = new CodeStructure();

        try {
            ParseResult<CompilationUnit> result = parseJava(codeSnippet);

            if (result.isSuccessful() && result.getResult().isPresent()) {
                CompilationUnit cu = result.getResult().get();

                structure.setParsedSuccessfully(true);
                structure.setParseMessage("Parsed successfully.");
                structure.setClassCount(cu.findAll(TypeDeclaration.class).size());
                structure.setMethodCount(cu.findAll(MethodDeclaration.class).size());
                structure.setFieldCount(cu.findAll(FieldDeclaration.class).size());
                structure.setForLoopCount(cu.findAll(ForStmt.class).size());
                structure.setWhileLoopCount(cu.findAll(WhileStmt.class).size());
                structure.setDoWhileLoopCount(cu.findAll(DoStmt.class).size());
                structure.setForeachLoopCount(cu.findAll(ForEachStmt.class).size());
                structure.setIfCount(cu.findAll(IfStmt.class).size());
                structure.setSwitchCount(cu.findAll(SwitchStmt.class).size());
                structure.setReturnCount(cu.findAll(ReturnStmt.class).size());
                structure.setMaxLoopNestingDepth(maxDepth(cu, 0, this::isLoopNode));
                structure.setMaxConditionalDepth(maxDepth(cu, 0, this::isConditionalNode));

                structure.setMethodNames(cu.findAll(MethodDeclaration.class).stream()
                        .map(MethodDeclaration::getNameAsString)
                        .distinct()
                        .collect(Collectors.toList()));

                structure.setMethodCalls(cu.findAll(MethodCallExpr.class).stream()
                        .map(MethodCallExpr::getNameAsString)
                        .distinct()
                        .collect(Collectors.toList()));

                structure.setVariableNames(cu.findAll(VariableDeclarator.class).stream()
                        .map(VariableDeclarator::getNameAsString)
                        .distinct()
                        .collect(Collectors.toList()));

                structure.setImportedTypes(cu.getImports().stream()
                        .map(importDeclaration -> importDeclaration.getNameAsString())
                        .collect(Collectors.toList()));

                detectRecursion(cu, structure);
                detectLoopCorrectnessRisks(cu, structure);
            } else {
                structure.setParsedSuccessfully(false);
                structure.setParseMessage(formatProblems(result.getProblems()));
            }

        } catch (ParseProblemException e) {
            structure.setParsedSuccessfully(false);
            structure.setParseMessage(formatProblems(e.getProblems()));
        } catch (RuntimeException e) {
            structure.setParsedSuccessfully(false);
            structure.setParseMessage("Parser error: " + e.getMessage());
        }

        return structure;
    }

    private String formatProblems(List<Problem> problems) {
        if (problems == null || problems.isEmpty()) {
            return "Syntax error: the Java code could not be parsed.";
        }

        return problems.stream()
                .map(this::formatProblem)
                .collect(Collectors.joining("\n"));
    }

    private String formatProblem(Problem problem) {
        return problem.getLocation()
                .flatMap(location -> location.getBegin().getRange())
                .map(range -> "Syntax error at line " + range.begin.line
                        + ", column " + range.begin.column + ": " + problem.getMessage())
                .orElse("Syntax error: " + problem.getMessage());
    }

    private ParseResult<CompilationUnit> parseJava(String codeSnippet) {
        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> result = parser.parse(codeSnippet);

        if (result.isSuccessful()) {
            return result;
        }

        String wrappedAsClass = "public class Snippet {\n" + codeSnippet + "\n}";
        ParseResult<CompilationUnit> wrappedClassResult = parser.parse(wrappedAsClass);
        if (wrappedClassResult.isSuccessful()) {
            return wrappedClassResult;
        }

        String wrappedAsMethod = "public class Snippet {\npublic void run() {\n" + codeSnippet + "\n}\n}";
        ParseResult<CompilationUnit> wrappedMethodResult = parser.parse(wrappedAsMethod);
        if (wrappedMethodResult.isSuccessful()) {
            return wrappedMethodResult;
        }

        return result;
    }

    private void detectRecursion(CompilationUnit cu, CodeStructure structure) {
        int maxRecursiveCalls = 0;
        List<String> recursiveMethods = cu.findAll(MethodDeclaration.class).stream()
                .filter(method -> {
                    long callsToSelf = method.findAll(MethodCallExpr.class).stream()
                            .filter(call -> call.getNameAsString().equals(method.getNameAsString()))
                            .count();
                    return callsToSelf > 0;
                })
                .map(method -> {
                    long callsToSelf = method.findAll(MethodCallExpr.class).stream()
                            .filter(call -> call.getNameAsString().equals(method.getNameAsString()))
                            .count();
                    return method.getNameAsString() + ":" + callsToSelf;
                })
                .collect(Collectors.toList());

        for (String recursiveMethod : recursiveMethods) {
            int callCount = Integer.parseInt(recursiveMethod.substring(recursiveMethod.indexOf(':') + 1));
            maxRecursiveCalls = Math.max(maxRecursiveCalls, callCount);
        }

        structure.setRecursionDetected(!recursiveMethods.isEmpty());
        structure.setMaxRecursiveCallsInMethod(maxRecursiveCalls);
        structure.setRecursiveMethods(recursiveMethods.stream()
                .map(value -> value.substring(0, value.indexOf(':')))
                .collect(Collectors.toList()));
    }

    private void detectLoopCorrectnessRisks(CompilationUnit cu, CodeStructure structure) {
        List<String> warnings = new ArrayList<>();

        for (ForStmt forStmt : cu.findAll(ForStmt.class)) {
            for (Expression update : forStmt.getUpdate()) {
                LoopStep loopStep = loopStepFrom(update);

                if (loopStep != null && Math.abs(loopStep.step()) > 1) {
                    boolean usedAsIndex = usesVariableAsIndex(forStmt, loopStep.variableName());
                    String warning = "Loop variable '" + loopStep.variableName()
                            + "' changes by " + loopStep.step()
                            + ", so the loop skips values.";

                    if (usedAsIndex) {
                        warning += " It is also used as an array/list index, so this can miss elements and produce a wrong answer.";
                    } else {
                        warning += " Confirm this stride is intentional; otherwise use a step of 1.";
                    }

                    warnings.add(warning);
                }
            }
        }

        structure.setCorrectnessWarnings(warnings);
    }

    private LoopStep loopStepFrom(Expression expression) {
        Expression normalized = unwrap(expression);

        if (normalized instanceof UnaryExpr unaryExpr && unaryExpr.getExpression() instanceof NameExpr nameExpr) {
            if (unaryExpr.getOperator() == UnaryExpr.Operator.POSTFIX_INCREMENT
                    || unaryExpr.getOperator() == UnaryExpr.Operator.PREFIX_INCREMENT) {
                return new LoopStep(nameExpr.getNameAsString(), 1);
            }
            if (unaryExpr.getOperator() == UnaryExpr.Operator.POSTFIX_DECREMENT
                    || unaryExpr.getOperator() == UnaryExpr.Operator.PREFIX_DECREMENT) {
                return new LoopStep(nameExpr.getNameAsString(), -1);
            }
        }

        if (normalized instanceof AssignExpr assignExpr && assignExpr.getTarget() instanceof NameExpr target) {
            String variableName = target.getNameAsString();

            if (assignExpr.getOperator() == AssignExpr.Operator.PLUS) {
                return integerStep(assignExpr.getValue(), variableName, 1);
            }
            if (assignExpr.getOperator() == AssignExpr.Operator.MINUS) {
                return integerStep(assignExpr.getValue(), variableName, -1);
            }
            if (assignExpr.getOperator() == AssignExpr.Operator.ASSIGN) {
                return assignedBinaryStep(assignExpr.getValue(), variableName);
            }
        }

        return null;
    }

    private LoopStep assignedBinaryStep(Expression expression, String variableName) {
        Expression normalized = unwrap(expression);

        if (!(normalized instanceof BinaryExpr binaryExpr)) {
            return null;
        }

        Expression left = unwrap(binaryExpr.getLeft());
        Expression right = unwrap(binaryExpr.getRight());

        if (left instanceof NameExpr leftName
                && leftName.getNameAsString().equals(variableName)
                && binaryExpr.getOperator() == BinaryExpr.Operator.PLUS) {
            return integerStep(right, variableName, 1);
        }

        if (left instanceof NameExpr leftName
                && leftName.getNameAsString().equals(variableName)
                && binaryExpr.getOperator() == BinaryExpr.Operator.MINUS) {
            return integerStep(right, variableName, -1);
        }

        return null;
    }

    private LoopStep integerStep(Expression expression, String variableName, int direction) {
        Expression normalized = unwrap(expression);

        if (normalized instanceof IntegerLiteralExpr integerLiteralExpr) {
            return new LoopStep(variableName, integerLiteralExpr.asInt() * direction);
        }

        return null;
    }

    private Expression unwrap(Expression expression) {
        Expression current = expression;
        while (current instanceof EnclosedExpr enclosedExpr) {
            current = enclosedExpr.getInner();
        }
        return current;
    }

    private boolean usesVariableAsIndex(ForStmt forStmt, String variableName) {
        boolean arrayIndexAccess = forStmt.getBody().findAll(ArrayAccessExpr.class).stream()
                .anyMatch(arrayAccessExpr -> referencesVariable(arrayAccessExpr.getIndex(), variableName));

        boolean listGetAccess = forStmt.getBody().findAll(MethodCallExpr.class).stream()
                .filter(methodCallExpr -> "get".equals(methodCallExpr.getNameAsString()))
                .anyMatch(methodCallExpr -> methodCallExpr.getArguments().stream()
                        .anyMatch(argument -> referencesVariable(argument, variableName)));

        return arrayIndexAccess || listGetAccess;
    }

    private boolean referencesVariable(Expression expression, String variableName) {
        return expression.findAll(NameExpr.class).stream()
                .anyMatch(nameExpr -> nameExpr.getNameAsString().equals(variableName));
    }

    private int maxDepth(Node node, int currentDepth, Predicate<Node> matcher) {
        int nextDepth = matcher.test(node) ? currentDepth + 1 : currentDepth;
        int maxDepth = nextDepth;

        for (Node child : node.getChildNodes()) {
            maxDepth = Math.max(maxDepth, maxDepth(child, nextDepth, matcher));
        }

        return maxDepth;
    }

    private boolean isLoopNode(Node node) {
        return node instanceof ForStmt
                || node instanceof ForEachStmt
                || node instanceof WhileStmt
                || node instanceof DoStmt;
    }

    private boolean isConditionalNode(Node node) {
        return node instanceof IfStmt || node instanceof SwitchStmt;
    }

    private record LoopStep(String variableName, int step) {
    }
}
