package in.astralra.lyric;

import in.astralra.lyric.impl.LNativeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jszaday on 8/4/2016.
 */
public class LScope {
    private LScope parent;
    private List<LDeclarable> declarations = new ArrayList<>();

    private static boolean containsFunctions(List<LDeclarable> declarations) {
        return declarations.stream()
                .filter(declaration -> declaration.getType() == LNativeType.FUNCTION)
                .findFirst().isPresent();
    }

    public LScope leave() {
        return parent;
    }

    public LScope enter(LScope child) {
        child.setParent(this);

        return child;
    }

    public void setParent(LScope parent) {
        this.parent = parent;
    }

    public void declare(LDeclarable declaration) throws LAlreadyDeclaredException {
        if (doesNotContain(declaration)) {
            declarations.add(declaration);
        } else {
            throw new LAlreadyDeclaredException(declaration.getName());
        }
    }

    private boolean doesNotContain(LDeclarable declaration) {
        Optional<LObject> value = declaration.getValue();

        if (value.isPresent() && declaration.getType() == LNativeType.FUNCTION) {
            return findFunction(declaration.getName(), ((LFunction) value.get()).getArguments(), false) == null;
        } else {
            return findByName(declaration.getName(), false).isEmpty();
        }
    }

    public List<LDeclarable> findByName(String name, boolean recursive) {
        List<LDeclarable> declarations = this.declarations
                .stream()
                .filter(declaration -> declaration.getName().equals(name))
                .collect(Collectors.toList());

        // If we are in recursive mode, we have results and the results are functions,
        if (parent != null && recursive) {
            if (declarations.isEmpty()) {
                declarations = Stream
                        .concat(parent.findByName(name).stream(), declarations.stream())
                        .collect(Collectors.toList());
            } else if (containsFunctions(declarations)) {
                // Search parent scope
                declarations.addAll(parent.findByName(name).stream()
                        .filter(declaration -> declaration.getType() == LNativeType.FUNCTION)
                        .filter(declaration -> declaration.getValue().isPresent())
                        .filter(declaration -> findFunction(name, ((LFunction) declaration.getValue().get()).getArguments(), false) == null)
                        .collect(Collectors.toList()));
            }
        }

        return declarations;
    }

    public List<LDeclarable> findByName(String name) {
        return findByName(name, true);
    }

    public LFunction findFunction(String name, Collection<LType> arguments) {
        return findFunction(name, arguments, true);
    }

    public LFunction findFunction(String name, Collection<LType> arguments, boolean recursive) {
        Optional<LFunction> function = declarations.stream()
                .filter(declaration -> declaration.getName().equals(name))
                .filter(declaration -> declaration.getType() == LNativeType.FUNCTION)
                .map(LDeclarable::getValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(LFunction.class::cast)
                .filter(lFunction -> lFunction.argumentsMatch(arguments))
                .findFirst();

        if (function.isPresent()) {
            return function.get();
        } else if (parent != null && recursive) {
            return parent.findFunction(name, arguments);
        } else {
            return null;
        }
    }

    public static class LAlreadyDeclaredException extends RuntimeException {
        LAlreadyDeclaredException(String name) {
            super("Scope already contains a variable with name " + name);
        }
    }
}
