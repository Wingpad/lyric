package in.astralra.lyric;

import in.astralra.lyric.impl.LNativeType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LFunction extends LObject {

    private HashMap<String, LType> _arguments;
    private LType _returnType;

    public LFunction() {
        this(new HashMap<>());
    }

    public LFunction(HashMap<String, LType> _arguments) {
        this._arguments = _arguments;
    }

    public LType getReturnType() {
        return _returnType;
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
        return _arguments.values();
    }

    public void putArgument(String name, LType type) {
        _arguments.put(name, type);
    }

    public LType getType() {
        return LNativeType.FUNCTION;
    }

    @Override
    public LFunction invokeWith(Collection<LExpression> arguments) {
        if (argumentsMatch(arguments.stream().map(LExpression::getType).collect(Collectors.toList()))) {
            return this;
        } else {
            throw new IllegalArgumentException("Arguments don't match.");
        }
    }
}
