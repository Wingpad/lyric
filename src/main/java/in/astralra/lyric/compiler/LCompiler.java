package in.astralra.lyric.compiler;

import in.astralra.lyric.*;
import in.astralra.lyric.gen.LyricBaseVisitor;
import in.astralra.lyric.gen.LyricParser;

import java.util.*;

/**
 * Created by jszaday on 8/9/2016.
 */
public class LCompiler extends LyricBaseVisitor<Object> {

    private LScope global;
    private LScope current;

    @Override
    public Object visitProgram(LyricParser.ProgramContext ctx) {
        global = new LScope();

        current = global;

        ctx.children.forEach(this::visit);

        return global;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitClassDefinition(LyricParser.ClassDefinitionContext ctx) {
        LClass lClass = new LClass(ctx.Id().getText(), (List<LType>) visitTypeParameters(ctx.typeParameters()));
        LDeclaration declaration = new LDeclaration(LNativeType.CLASS, lClass.getName(), lClass);
        current.declare(declaration);

        current = current.enter(lClass);

        ctx.classDeclaration().forEach(this::visitClassDeclaration);

        current = lClass.leave();

        return declaration;
    }

    @Override
    public Object visitClassDeclaration(LyricParser.ClassDeclarationContext ctx) {
        LDeclaration declaration;

        if (ctx.declaration() == null) {
            declaration = (LDeclaration) visitConstructor(ctx.constructor());
        } else {
            declaration = (LDeclaration) visitDeclaration(ctx.declaration());
        }

        for (LyricParser.DeclarationModifierContext modifierContext : ctx.declarationModifier()) {
            declaration.setModifiers(LModifier.valueOf(modifierContext.getText().toUpperCase()).getFlag());
        }

        return declaration;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitConstructor(LyricParser.ConstructorContext ctx) {
        LFunction function = new LFunction(
                (HashMap<String, LType>) visitFunctionArgumentList(ctx.functionArgumentList())
        );

        visitBlock(ctx.block(), function);

        if (current instanceof LClass) {
            // TODO: 8/9/2016 Add to class in a way that preserves modifiers.
            ((LClass) current).addConstructor(function);
        }

        return new LDeclaration(LNativeType.FUNCTION, "init", function);
    }

    @Override
    public Object visitDeclaration(LyricParser.DeclarationContext ctx) {
        LDeclaration declaration;

        if (ctx.block() == null) {
            LType type;
            LExpression expression;

            if (ctx.expression() == null) {
                expression = null;
            } else {
                expression = (LExpression) visitExpression(ctx.expression());
            }

            if (ctx.type() == null) {
                assert (expression != null);
                type = expression.getType();
            } else {
                type = (LType) visitType(ctx.type());
            }

            int flags = ctx.Val() == null ? 0 : LModifier.FINAL.getFlag();

            declaration = new LDeclaration(type, ctx.Id().getText(), expression);

            declaration.setModifiers(flags);
        } else {
            declaration = null;
        }

        current.declare(declaration);

        return declaration;
    }

    @Override
    public Object visitTypeParameters(LyricParser.TypeParametersContext ctx) {
        if (ctx == null) {
            return Collections.emptyList();
        } else {
            LyricParser.TypeListContext listContext = ctx.typeList();
            List<LType> typeParameters = new ArrayList<>();
            while (listContext != null) {
                typeParameters.add((LType) visitBoundedType(listContext.boundedType()));
            }
            return typeParameters;
        }
    }

    @Override
    public Object visitBoundedType(LyricParser.BoundedTypeContext ctx) {

        return null;
    }

    @Override
    public Object visitType(LyricParser.TypeContext ctx) {
        if (ctx.NativeType() != null) {
            String identifier = ctx.NativeType().getText().substring(3);
            Optional<LNativeType> optional = LNativeType.lookup(identifier);

            if (optional.isPresent()) {
                return optional.get();
            } else {
                throw new RuntimeException("Unable to resolve native type " + identifier);
            }
        } else {
            return super.visitType(ctx);
        }
    }

    @Override
    public Object visitFunctionArgumentList(LyricParser.FunctionArgumentListContext ctx) {
        if (ctx == null) {
            return Collections.emptyMap();
        }

        HashMap<String, LType> arguments = new HashMap<>();

        while (ctx != null) {
            LType type;

            if (ctx.type() == null) {
                type = null;
            } else {
                type = (LType) visitType(ctx.type());
            }

            arguments.put(ctx.Id().getText(), type);

            ctx = ctx.functionArgumentList();
        }

        return arguments;
    }

    @Override
    public Object visitBlock(LyricParser.BlockContext ctx) {
        return visitBlock(ctx, new LSimpleBlock());
    }

    public Object visitBlock(LyricParser.BlockContext ctx, LBlock block) {
        current = current.enter(block.getScope());

        // Add all of the children to the block
        ctx.statement().stream()
                .map(this::visitStatement)
                .forEach(block::add);

        current = current.leave();

        return block;
    }

    @Override
    public Object visitAssignment(LyricParser.AssignmentContext ctx) {
        LExpression left = (LExpression) visit(ctx.postfixExpression());
        Optional<LOperator> operator = LOperator.lookup(ctx.assignmentOperator().getText().substring(1));
        LExpression right = (LExpression) visit(ctx.conditionalExpression());

        if (operator.isPresent()) {
            return new LAssignment(left, operator.get(), right);
        } else {
            throw new RuntimeException("No operator found for " + ctx.assignmentOperator().getText());
        }
    }

    @Override
    public Object visitStatement(LyricParser.StatementContext ctx) {
        return visit(ctx.children.get(0));
    }

    @Override
    public Object visitPrimaryExpression(LyricParser.PrimaryExpressionContext ctx) {
        if (ctx.Id() != null) {
            return new LReference(current, ctx.Id().getText());
        } else {
            return null;
        }
    }
}
