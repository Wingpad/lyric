package in.astralra.lyric.core;

import in.astralra.lyric.expression.LDeclaration;
import in.astralra.lyric.type.LClass;

import java.util.Collection;
import java.util.List;

/**
 * Created by jszaday on 8/5/2016.
 */
public interface LObject extends LInvocable {
    boolean isAccessible(LDeclaration declaration);

    LFunction findFunction(String name, Collection<LType> arguments);

    LFunction findFunction(String name, Collection<LType> arguments, boolean recursive);

    LDeclaration findDeclarableForFunction(LFunction function);

    List<LDeclaration> findByName(String name, boolean recursive);

    List<LDeclaration> findByName(String name);

    LType getType();
}
