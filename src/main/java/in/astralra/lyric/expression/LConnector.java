package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static in.astralra.lyric.core.LFunction.map;

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

    public LConnector(LObject target, LExpression... expressions) {
        this.identifier = new LReference(target, "get");
        this.expressions = Arrays.asList(expressions);
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
                return new LFunctionCall(identifier, "get", expressions);
            case REFERENCE:
                throw new RuntimeException("You can't 'get' a lifted thingamajig.");
            default:
                return null;
        }
     }

    @Override
    public String lift(Collection<LExpression> arguments) {
        if (types == null || types.isEmpty()) {
            types = LFunction.map(arguments);
        }

        String typeIdentifier = types.stream().map(LType::getName).collect(Collectors.joining(";"));

        return "LObject_lift(" + identifier.getScope() + ", \"" + identifier.getTarget() + "\", \"" + typeIdentifier +
                "\")";
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
                return new LFunctionCall(identifier, "set", mutable).toString();
            default:
                throw new RuntimeException("Cannot set the value of " + identifier);
        }
    }



    private enum LConnectorType {
        DOT, ARRAY, REFERENCE
    }
}
