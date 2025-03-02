package net.bytebuddy;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClassFileVersionTest {

    @Test
    public void testExplicitConstructionOfUnknownVersion() throws Exception {
        double version = 0d;
        int value = 0;
        Pattern pattern = Pattern.compile("V[0-9]+(_[0-9]+)?");
        for (Field field : Opcodes.class.getFields()) {
            if (pattern.matcher(field.getName()).matches()) {
                if (version < Double.parseDouble(field.getName().substring(1).replace('_', '.'))) {
                    value = field.getInt(null);
                }
            }
        }
        assertThat(ClassFileVersion.ofMinorMajor(value + 1).getMinorMajorVersion(), is(value + 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalVersion() throws Exception {
        ClassFileVersion.ofMinorMajor(ClassFileVersion.BASE_VERSION);
    }

    @Test
    public void testComparison() throws Exception {
        assertThat(new ClassFileVersion(Opcodes.V1_1).compareTo(new ClassFileVersion(Opcodes.V1_1)), is(0));
        assertThat(new ClassFileVersion(Opcodes.V1_1).compareTo(new ClassFileVersion(Opcodes.V1_2)), is(-1));
        assertThat(new ClassFileVersion(Opcodes.V1_2).compareTo(new ClassFileVersion(Opcodes.V1_1)), is(1));
        assertThat(new ClassFileVersion(Opcodes.V1_2).compareTo(new ClassFileVersion(Opcodes.V1_2)), is(0));
        assertThat(new ClassFileVersion(Opcodes.V1_3).compareTo(new ClassFileVersion(Opcodes.V1_2)), is(1));
        assertThat(new ClassFileVersion(Opcodes.V1_2).compareTo(new ClassFileVersion(Opcodes.V1_3)), is(-1));
    }

    @Test
    public void testVersionOfClass() throws Exception {
        assertThat(ClassFileVersion.of(Foo.class).compareTo(ClassFileVersion.ofThisVm()) < 1, is(true));
    }

    @Test
    public void testClassFile() throws Exception {
        assertThat(ClassFileVersion.of(Object.class).getMinorMajorVersion(), not(0));
    }

    @Test
    public void name() {
        ClassFileVersion.ofThisVm();
    }

    @Test
    public void testLatestVersion() throws Exception {
        double version = 0d;
        int value = 0;
        Pattern pattern = Pattern.compile("V[0-9]+(_[0-9]+)?");
        for (Field field : Opcodes.class.getFields()) {
            if (pattern.matcher(field.getName()).matches()) {
                if (version < Double.parseDouble(field.getName().substring(1).replace('_', '.'))) {
                    value = field.getInt(null);
                }
            }
        }
        assertThat(ClassFileVersion.latest().getMajorVersion(), is((short) value));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalClassFile() throws Exception {
        ClassFileVersion.ofClassFile(new byte[0]);
    }

    @Test
    public void testClassFileVersion() {
        for (int i = 1; i < ClassFileVersion.latest().getJavaVersion(); i++) {
            byte major = (byte) (44 + i);
            byte minor = (byte) (i == 1 ? 3 : 0);

            ClassFileVersion expected = ClassFileVersion.ofJavaVersion(i);
            assertThat(ClassFileVersion.ofClassFile(new byte[]{0, 0, 0, 0, 0, minor, 0, major}), is(expected));
        }
    }

    private static class Foo {
        /* empty */
    }
}
