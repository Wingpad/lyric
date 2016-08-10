package in.astralra.lyric;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LFunction extends LObject implements LBlock {

    private HashMap<String, LType> arguments;
    private LType returnType;
    private LBlock block;
    private List<Object> elements = new ArrayList<>();

    public LFunction(HashMap<String, LType> arguments) {
        this.arguments = arguments;
    }

    public LType getReturnType() {
        return returnType;
    }

    public boolean argumentsMatch(LFunction other) {
        return argumentsMatch(other.getArguments());
    }

    public boolean argumentsMatch(Collection<LType> theirs) {
        Collection<LType> ours = getArguments();
        Iterator<LType> ourIterator = ours.iterator(),
                theirIterator = theirs.iterator();
        // Verify the lengths are the same
        boolean flag = ours.size() == theirs.size();
        // And until one differs
        while (flag && ourIterator.hasNext()) {
            // Iterate through the list
            flag = ourIterator.next().isAssignableFrom(theirIterator.next());
        }
        // Returning the flag when finished
        return flag;

    }

    public Collection<LType> getArguments() {
        return arguments.values();
    }

    public void putArgument(String name, LType type) {
        arguments.put(name, type);
    }

    public LType getType() {
        return LNativeType.FUNCTION;
    }

    @Override
    public LFunction invokeWith(Collection<LExpression> arguments) {
        if (argumentsMatch(map(arguments))) {
            return this;
        } else {
            throw new IllegalArgumentException("Arguments don't match.");
        }
    }

    @Override
    public LScope getScope() {
        return this;
    }

    @Override
    public LBlock add(Object object) {
        elements.add(object);

        return this;
    }

    @Override
    public List<Object> list() {
        return elements;
    }

    public static Collection<LType> map(Collection<LExpression> expressions) {
        return expressions.stream().map(LExpression::getType).collect(Collectors.toList());
    }
}
