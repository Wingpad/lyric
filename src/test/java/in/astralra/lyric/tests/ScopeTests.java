package in.astralra.lyric.tests;

import in.astralra.lyric.*;
import in.astralra.lyric.LNativeType;
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
        LDeclaration test = new LDeclaration(null, "test");
        LDeclaration childsTest = new LDeclaration(null, "test");

        grandparent.enter(parent).enter(child);

        grandparent.declare(test);

        List<LDeclaration> results = grandparent.findByName("test");

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
        LDeclaration declaration = new LDeclaration(null, "test");

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

        parent.declare(new LDeclaration(LNativeType.FUNCTION, "function", theirFunction1));
        parent.declare(new LDeclaration(LNativeType.FUNCTION, "function", theirFunction2));

        child.declare(new LDeclaration(LNativeType.FUNCTION, "function", myFunction));

        assertEquals("child should match our function", myFunction, child.findFunction("function", Collections.singletonList(LNativeType.INT)));

        assertEquals("but should still have parents other definition", child.findByName("function").size(), 2);
    }

    @Test
    public void testSetModifiers() {
        LScope scope = newInstance();

        LDeclaration declarable = new LDeclaration(LNativeType.VOID, "test");

        scope.declare(declarable, LModifier.FINAL);

        assertTrue("declarable should be final", LModifier.FINAL.isPresent(declarable.getModifiers()));
    }

    @Test
    public void testSetSelf() {
        LScope scope = newInstance();
        LObject object = new LClass("test");

        scope.setSelf(object);

        assertEquals("self should be set", scope.getSelf(), object);
    }

    @Test
    public void testAccessibility() {
        LScope parent = newInstance();
        LScope child = newInstance();

        parent.enter(child);

        LDeclaration privateOne = new LDeclaration(LNativeType.VOID, "private");
        LDeclaration publicOne = new LDeclaration(LNativeType.VOID, "public");
        LDeclaration protectedOne = new LDeclaration(LNativeType.VOID, "protected");

        parent.declare(privateOne, LModifier.PRIVATE);
        parent.declare(publicOne, LModifier.PUBLIC);
        parent.declare(protectedOne, LModifier.PROTECTED);

        assertTrue("protected one should be accessible in child", child.isAccessible(protectedOne));
        assertTrue("public one should be accessible in child", child.isAccessible(publicOne));
        assertFalse("private one should not be accessible in child", child.isAccessible(privateOne));
        assertTrue("but private one should be accessible in parent", parent.isAccessible(privateOne));
    }

    public LScope newInstance() {
        return new LScope();
    }
}
