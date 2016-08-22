package in.astralra.lyric.compiler;

import in.astralra.lyric.core.*;
import in.astralra.lyric.expression.LDeclaration;
import in.astralra.lyric.expression.LManualElement;
import in.astralra.lyric.expression.LNativeValue;
import in.astralra.lyric.expression.LReturn;
import in.astralra.lyric.type.LClass;
import in.astralra.lyric.type.LNativeType;

import java.util.Optional;

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
        return visit(element, true);
    }

    private String visit(Object element, boolean visitBackElements) {
        final StringBuilder result = new StringBuilder();

        if (element instanceof LElement && visitBackElements) {
            ((LElement) element).getBackElements()
                    .forEach(backElement -> result.append(visit(backElement)));
        }

        if (element instanceof LBlock) {
            result.append(visitBlock((LBlock) element));
        } else if (element instanceof LDeclaration) {
            result.append(visitDeclaration((LDeclaration) element));
        } else {
            if (visitBackElements) {
                result.append("\t");
            }

            result.append(String.valueOf(element));
        }

        if (element instanceof LElement && ((LElement) element).needsSemicolon() && visitBackElements) {
            result.append(";").append(System.lineSeparator());
        }

        return result.toString();
    }

    private String visitBlock(LBlock block) {
        final StringBuilder result = new StringBuilder();

        if (block != mainBlock) {
            result.append("{").append(System.lineSeparator());
        }

        block.list().forEach(element -> result.append(visit(element)));

        if (block != mainBlock) {
            result.append("}");
        }

        return result.toString();
    }

    private String visitDeclaration(LDeclaration declaration) {
        final StringBuilder builder = new StringBuilder();

        if (!declaration.isValid()) {
            throw new RuntimeException("Invalid declaration, " + declaration + "!");
        }

        builder.append("\t").append(declaration.getType().getIdentifier());
        builder.append(" ").append(declaration.getName());

        Optional<LObject> value = declaration.getValue();
        if (value.isPresent()) {
            if (declaration.getType() == LNativeType.CLASS) {
                return visitClass((LClass) value.get());
            } else if (declaration.getType() == LNativeType.FUNCTION) {
                return visitFunction((LFunction) value.get());
            } else {
                builder.append(" = ").append(visit(value.get(), false));
            }
        }


        return builder.toString();
    }

    private String visitClass(LClass lClass) {
        final StringBuilder builder = new StringBuilder();

        lClass.getConstructors().stream()
                .map(this::visit)
                .forEach(builder::append);

        lClass.getMembers().stream()
                .map(this::visitFunction)
                .forEach(builder::append);

        LFunction initializer = new LFunction();
        initializer.add(new LManualElement("LObject* lClass = LObject_new(\"LClass\", NULL)"));

        String next = "NULL";
        for (LDeclaration constructor : lClass.getConstructors()) {
            LFunction function = (LFunction) constructor.getValue().get();
            String init = "LFunction_new(lClass, " + function.getExternalName() + ", \"" + function.getIdentifier() + "\")";
            initializer.add(new LManualElement("lClass->first = LObjectNode_new(\"LFunction\", \"invoke\", " + init + ",  " + next + ")"));
            next = next.equals("NULL") ? "lClass->first" : next;
        }

        initializer.add(new LManualElement("lClass->first = LObjectNode_new(\"C\", \"name\", \"" + lClass.getName() + "\", " + next + ")"));

        next = "NULL";
        for (LDeclaration field : lClass.getDeclarations()) {
            String value;
            Optional<LObject> object = field.getValue();
            if (object.isPresent()) {
                if (object.get() instanceof LFunction) {
                    LFunction member = (LFunction) object.get();
                    value = "LFunction_new(NULL, " + member.getExternalName() + ", \"" + member.getIdentifier() + "\")";
                } else {
                    throw new RuntimeException("Default values not supported for type " + object.get().getType() + "!");
                }
            } else {
                value = "NULL";
            }

            initializer.add(new LManualElement("lClass->metadata = LObjectNode_new(\"" + field.getType().getName() + "\", \"" + field.getName() + "\", " + value + ", " + next + ")"));

            next = next.equals("NULL") ? "(LObjectNode*) lClass->metadata" : next;
        }

        initializer.add(new LManualElement("return lClass"));

        builder.append(LNativeType.OBJECT.getIdentifier()).append(" ").append(lClass.getName()).append("_allocate() ").append(visit(initializer));

        return builder.toString();
    }

    private String visitFunction(LFunction function) {
        return LNativeType.OBJECT.getIdentifier() + " " +
                function.getExternalName() + "(LObject* self, uint32_t argc, void** argv) " +
                visitBlock(function);
    }
}
