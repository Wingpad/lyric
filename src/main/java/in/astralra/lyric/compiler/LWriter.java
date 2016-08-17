package in.astralra.lyric.compiler;

import in.astralra.lyric.core.*;
import in.astralra.lyric.expression.LDeclaration;
import in.astralra.lyric.expression.LFunctionCall;
import in.astralra.lyric.gen.LyricVisitor;
import in.astralra.lyric.type.LClass;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/13/2016.
 */
public class LWriter {
    private final LBlock mainBlock;
//    private LScope current;

    public LWriter(LBlock mainBlock) {
        this.mainBlock = mainBlock;
    }

    public String visit() {
        return this.visit(mainBlock);
    }

    private String visit(Object element) {
//        if (element instanceof LScope) {
//            current = (LScope) element;
//        }

        if (element instanceof LBlock) {
            return visitBlock((LBlock) element);
        } else if (element instanceof LDeclaration) {
            return visitDeclaration((LDeclaration) element);
        } else if (element instanceof LFunctionCall) {
            return visitFunctionCall((LFunctionCall) element);
        } else {
            return null;
        }
    }

    private String visitBlock(LBlock block) {
        final StringBuilder result = new StringBuilder();

        if (block != mainBlock) {
            result.append("{");
        }

        block.list().forEach(element -> result.append(visit(element)));

        if (block != mainBlock) {
            result.append("}");
        }

        return result.toString();
    }

    private String visitDeclaration(LDeclaration declaration) {
        final StringBuilder builder = new StringBuilder();

        builder.append("\t").append(declaration.getType().getIdentifier());
        builder.append(" ").append(declaration.getName());

        Optional<LObject> value = declaration.getValue();
        if (value.isPresent()) {
            if (declaration.getType() == LNativeType.CLASS) {

            } else if (declaration.getType() == LNativeType.FUNCTION) {
                return visitFunction((LFunction) value.get());
            } else {
                builder.append(" = ").append(visit(value.get()));
            }
        } else {
            builder.append(";").append(System.lineSeparator());
        }


        return builder.toString();
    }

    private String visitFunction(LFunction function) {
        final StringBuilder builder = new StringBuilder();

        builder
                .append(LNativeType.OBJECT.getIdentifier())
                .append(" ")
                .append(function.getExternalName()).append("(LObject* self, uint32_t argc, void** argv) ");

        builder.append(visitBlock(function));

        return builder.toString();
    }

    private String visitFunctionCall(LFunctionCall functionCall) {
        final StringBuilder builder = new StringBuilder();
        LFunction target = (LFunction) functionCall.getObject();

        builder.append("LObject_invoke(").append(functionCall.getInvocable()).append(", ").append(target.getExternalName()).append(", ");

        builder.append(functionCall.getArguments().stream().map(this::visit).collect(Collectors.joining(", "))).append(")");

        return builder.toString();
    }
}
