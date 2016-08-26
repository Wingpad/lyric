package in.astralra.lyric.util;

import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import in.astralra.lyric.core.LObject;
import in.astralra.lyric.core.LType;
import in.astralra.lyric.expression.LConnector;
import in.astralra.lyric.expression.LDeclaration;
import in.astralra.lyric.expression.LExpression;
import in.astralra.lyric.type.LClass;
import in.astralra.lyric.type.LNativeType;

import java.util.List;

/**
 * Created by jszaday on 8/25/2016.
 */
public class LUnboxer {
    public static LObject unbox(LObject object, LNativeType target) {
        if (object.getType().isNativeType() && (target == LNativeType.VOID || target == object.getType())) {
            return object;
        } else if (!object.getType().isNativeType()) {
            if (target == LNativeType.VOID || target == LNativeType.OBJECT) {
                return object;
            }
            List<LDeclaration> declarations = object.findByName("value");
            LType found;
            if (declarations.isEmpty() || !(found = declarations.get(0).getType()).isNativeType() || found != target) {
                throw new RuntimeException("Cannot assign " + object + " to " + target);
            } else {
                return new LConnector(object, "value");
            }
        } else {
            throw new RuntimeException("Cannot assign " + object + " to " + target);
        }
    }
}
