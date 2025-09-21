package jdk.internal.reflect;

import jdk.internal.vm.annotation.ForceInline;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public class Reflection {
    @CallerSensitive
    @IntrinsicCandidate
    public static native Class<?> getCallerClass();

    @IntrinsicCandidate
    public static native int getClassAccessFlags(Class<?> c);

    static boolean isSubclassOf(Class<?> queryClass, Class<?> ofClass) {
        while(queryClass != null) {
            if(queryClass == ofClass)
                return true;
            queryClass = queryClass.getSuperclass();
        }
        return false;
    }

    public static native boolean areNestMates(Class<?> currentClass, Class<?> memberClass);
}
