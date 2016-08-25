package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;
import in.astralra.lyric.type.LNativeType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jszaday on 8/5/2016.
 */
// TODO: 8/9/2016 Might have to extend Assignment 
public class LConnector extends LExpression implements LAssignable {

    private LReference identifier;
    private LConnectorType type;
    private List<LExpression> expressions;
    private List<LType> types;

    public LConnector(LObject target, String identifier) {
        this.identifier = new LReference(target, identifier);
        this.type = LConnectorType.DOT;
    }

    public LConnector(LObject target, String identifier, LType type, LType... types) {
        this.identifier = new LReference(target, identifier);
        this.types = Stream.concat(Stream.of(type), Arrays.stream(types))
                .filter(t -> t != null)
                .collect(Collectors.toList());
        this.type = LConnectorType.REFERENCE;
    }

    public LConnector(LObject target, List<LExpression> expressions) {
        this.identifier = new LReference(target, "get");
        this.expressions = expressions;
        this.type = LConnectorType.ARRAY;
    }

    @Override
    LObject getObject() {
        switch (type) {
            case DOT:
                if (identifier.getType() == LNativeType.FUNCTION) {
                    this.type = LConnectorType.REFERENCE;
                    this.types = new ArrayList<>(((LFunction) identifier.getObject()).getArguments());
                }
                return identifier;
            case ARRAY:
                return new LFunctionCall(identifier.getScope(), "get", expressions);
            case REFERENCE:
                // TODO determine what should really be here.
                return identifier;
            default:
                return null;
        }
     }

    @Override
    public String lift(Collection<LExpression> arguments) {
        if (type == LConnectorType.DOT) {
            type = LConnectorType.REFERENCE;
        }

        if (types == null || types.isEmpty()) {
            types = LFunction.map(arguments);
        }

        String typeIdentifier = types.stream().map(LType::getName).collect(Collectors.joining(";"));

        if (type == LConnectorType.REFERENCE) {
            return "LObject_lift(" + identifier.getScope() + ", \"" + identifier.getTarget() + "\", \"" + typeIdentifier +
                    "\")";
        } else if (type == LConnectorType.ARRAY) {
            return null;
        } else {
            throw new RuntimeException("No idea how to lift dis shit.");
        }
    }

    @Override
    public String assign(LExpression value) {
        // TODO handle other cases - like ARRAY
        switch (type) {
            case DOT:
                // Just set the value directly
                return "LObject_set(" + identifier.getScope() + ", \"" + identifier.getTarget() + "\", " + value + ")";
            case ARRAY:
                // Create a new, mutable instance of the expressions
                ArrayList<LExpression> mutable = new ArrayList<>(expressions);
                // Tacking on the new value
                mutable.add(value);
                // Then make the call
                return new LFunctionCall(identifier.getScope(), "set", mutable).toString();
            default:
                throw new RuntimeException("Cannot set the value of " + identifier);
        }
    }

    private enum LConnectorType {
        DOT, ARRAY, REFERENCE
    }

    @Override
    public String toString() {
        if (type == LConnectorType.ARRAY) {
            return String.valueOf(getObject());
        } else {
            return "LObject_get(" + identifier.getScope() + ", \"" + getObject().toString() + "\")";
        }
    }
}
