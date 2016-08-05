package in.astralra.lyric.tests;

import in.astralra.lyric.LDeclarable;
import in.astralra.lyric.LFunction;
import in.astralra.lyric.LScope;
import in.astralra.lyric.impl.LNativeType;
import in.astralra.lyric.impl.LSimpleDeclaration;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jszaday on 8/4/2016.
 */
public class ScopeTests {

    @Test
    public void testEnter() {
        LScope parent = newInstance();
        LScope child = newInstance();

        assertEquals("Parent must enter child.", child, parent.enter(child));

        assertEquals("Enter must set parent.", parent, child.leave());
    }

    @Test
    public void testSetParent() {
        LScope parent = newInstance();
        LScope child = newInstance();

        child.setParent(parent);

        assertEquals("SetParent must set parent.", parent, child.leave());
    }

    @Test
    public void testInheritanceAndDeclaration() {
        LScope grandparent = newInstance();
        LScope parent = newInstance();
        LScope child = newInstance();
        LDeclarable test = new LSimpleDeclaration(null, "test");
        LDeclarable childsTest = new LSimpleDeclaration(null, "test");

        grandparent.enter(parent).enter(child);

        grandparent.declare(test);

        List<LDeclarable> results = grandparent.findByName("test");

        assertFalse("Test must be found in Grandparent.", results.isEmpty() || results.get(0) != test);

        results = parent.findByName("test");

        assertFalse("Test must be found in Parent.", results.isEmpty() || results.get(0) != test);

        results = parent.findByName("test", false);

        assertTrue("But not *explicitly* in Parent.", results.isEmpty());

        results = child.findByName("test");

        assertFalse("Test must be found in Child.", results.isEmpty() || results.get(0) != test);

        child.declare(childsTest);
        results = child.findByName("test");

        assertTrue("Child must override Parent/Grandparent.", !results.isEmpty() && results.get(0) == childsTest);
    }

    @Test(expected = LScope.LAlreadyDeclaredException.class)
    public void shouldNotAllowMultipleDefinitions() {
        LScope scope = newInstance();
        LDeclarable declaration = new LSimpleDeclaration(null, "test");

        scope.declare(declaration);

        scope.declare(declaration);
    }

    @Test
    public void functionsShouldOverrideParentsFunctions() {
        LScope parent = newInstance();
        LScope child = newInstance();

        parent.enter(child);

        LFunction myFunction = new LFunction();
        myFunction.putArgument("test", LNativeType.INT);

        LFunction theirFunction1 = new LFunction();
        theirFunction1.putArgument("test", LNativeType.INT);

        LFunction theirFunction2 = new LFunction();
        theirFunction2.putArgument("i", LNativeType.INT);
        theirFunction2.putArgument("j", LNativeType.INT);

        parent.declare(new LSimpleDeclaration(LNativeType.FUNCTION, "function", theirFunction1));
        parent.declare(new LSimpleDeclaration(LNativeType.FUNCTION, "function", theirFunction2));

        child.declare(new LSimpleDeclaration(LNativeType.FUNCTION, "function", myFunction));

        assertEquals("child should match our function", myFunction, child.findFunction("function", Collections.singletonList(LNativeType.INT)));

        assertEquals("but should still have parents other definition", child.findByName("function").size(), 2);
    }

    public LScope newInstance() {
        return new LScope();
    }
}
