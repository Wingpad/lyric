package in.astralra.lyric.expression;

import in.astralra.lyric.core.LElement;
import in.astralra.lyric.core.LFunction;
import in.astralra.lyric.core.LObject;
import in.astralra.lyric.core.LType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by jszaday on 8/4/2016.
 */
public abstract class LExpression implements LObject, LElement {
    abstract LObject getObject();

    @Override
    public LType getType() {
        return getObject().getType();
    }

    @Override
    public boolean isMember(LDeclaration declaration) {
        return getObject().isMember(declaration);
    }

    @Override
    public LFunction liftFunction(Collection<LExpression> arguments) {
        return getObject().liftFunction(arguments);
    }

    @Override
    public boolean isAccessible(LDeclaration declaration) {
        return getObject().isAccessible(declaration);
    }

    @Override
    public LFunction findFunction(String name, Collection<LType> arguments) {
        return getObject().findFunction(name, arguments);
    }

    @Override
    public LFunction findFunction(String name, Collection<LType> arguments, boolean recursive) {
        return getObject().findFunction(name, arguments, recursive);
    }

    @Override
    public LDeclaration findDeclarableForFunction(LFunction function) {
        return getObject().findDeclarableForFunction(function);
    }

    @Override
    public List<LDeclaration> findByName(String name, boolean recursive) {
        return getObject().findByName(name, recursive);
    }

    @Override
    public List<LDeclaration> findByName(String name) {
        return getObject().findByName(name);
    }

    @Override
    public List<LElement> getBackElements() {
        return Collections.emptyList();
    }

    @Override
    public boolean needsSemicolon() {
        return true;
    }

    @Override
    public LObject getSelf() {
        return getObject().getSelf();
    }
}
