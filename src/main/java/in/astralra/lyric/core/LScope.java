package in.astralra.lyric.core;

import in.astralra.lyric.expression.LDeclaration;
import in.astralra.lyric.expression.LExpression;
import in.astralra.lyric.type.LClass;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jszaday on 8/4/2016.
 */
public abstract class LScope implements LObject {
    private LScope parent;
    private List<LDeclaration> declarations = new ArrayList<>();

    private static boolean containsFunctions(List<LDeclaration> declarations) {
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

    public LScope getParent() {
        return parent;
    }

    public void setParent(LScope parent) {
        this.parent = parent;
    }

    public void declare(LDeclaration declarable, LModifier... modifiers) throws LAlreadyDeclaredException {
        if (doesNotContain(declarable)) {
            declarable.setModifiers(LModifier.reduceToInt(declarable.getModifiers(), modifiers));
            
            declarations.add(declarable);
        } else {
            throw new LAlreadyDeclaredException(declarable.getName());
        }
    }

    private boolean doesNotContain(LDeclaration declaration) {
        Optional<LObject> value = declaration.getValue();

        if (value.isPresent() && declaration.getType() == LNativeType.FUNCTION) {
            return findFunction(declaration.getName(), ((LFunction) value.get()).getArguments(), false) == null;
        } else {
            return findByName(declaration.getName(), false).isEmpty();
        }
    }

    public List<LDeclaration> findByName(String name, boolean recursive) {
        List<LDeclaration> declarations = this.declarations
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

    public List<LDeclaration> findByName(String name) {
        return findByName(name, true);
    }

    public LFunction findFunction(String name, Collection<LType> arguments) {
        return findFunction(name, arguments, true);
    }

    public LFunction findFunction(String name, Collection<LType> arguments, boolean recursive) {
        Optional<LFunction> function = declarations.stream()
                .filter(declaration -> declaration.getName().equals(name))
                .filter(declaration -> declaration.getType() == LNativeType.FUNCTION)
                .map(LDeclaration::getValue)
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

    public LDeclaration findDeclarableForFunction(LFunction function) {
        Optional<LDeclaration> found = declarations.stream()
                .filter(declarable -> declarable.getType().equals(LNativeType.FUNCTION))
                .filter(declarable -> declarable.getValue().isPresent())
                .filter(declarable -> declarable.getValue().get().equals(function))
                .findFirst();

        if (found.isPresent()) {
            return found.get();
        } else if (parent != null) {
            return parent.findDeclarableForFunction(function);
        } else {
            return null;
        }
    }

    public static class LAlreadyDeclaredException extends RuntimeException {
        LAlreadyDeclaredException(String name) {
            super("Scope already contains a variable with name " + name);
        }
    }

    private boolean isDeclaredInScope(final LDeclaration declarable) {
        return declarations.stream().anyMatch(declared -> declarable == declared);
    }

    public boolean isMember(final LDeclaration declaration) {
        LScope self = getSelf();

        if (self == null) {
            return false;
        } else {
            return self.isDeclaredInScope(declaration);
        }
    }

    public boolean isAccessible(final LDeclaration declarable) {
        LScope self = getSelf();

        boolean isDeclaredInSelf = self != null && self.isDeclaredInScope(declarable);
        boolean isDeclaredInParent = parent != null && parent.isDeclaredInScope(declarable);
        boolean isDeclaredHere = isDeclaredInScope(declarable);

        if (LModifier.PRIVATE.isPresent(declarable.getModifiers())) {
            return isDeclaredHere || isDeclaredInSelf;
        } else if (LModifier.PROTECTED.isPresent(declarable.getModifiers())) {
            return isDeclaredHere || isDeclaredInSelf || isDeclaredInParent;
        } else {
            // A declaration is accessible when it is A) Public or B) Default
            // TODO: 8/9/2016 Should this just return true?? What about static?
            return LModifier.PUBLIC.isPresent(declarable.getModifiers()) || declarable.getModifiers() == 0;
        }
    }

    public LClass getSelf() {
        if (this instanceof LClass) {
            return (LClass) this;
        } else if (parent == null) {
            return null;
        } else {
            return parent.getSelf();
        }
    }
}
