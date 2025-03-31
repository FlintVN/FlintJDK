
package java.lang.invoke;

import java.lang.invoke.MethodHandles.Lookup;
import java.util.Objects;

import static java.lang.invoke.MethodType.methodType;

public final class StringConcatFactory {
    public static CallSite makeConcat(MethodHandles.Lookup lookup, String name, MethodType concatType) throws StringConcatException {
        String recipe = "\u0001".repeat(concatType.parameterCount());
        return makeConcatWithConstants(lookup, name, concatType, recipe);
    }

    public static CallSite makeConcatWithConstants(MethodHandles.Lookup lookup, String name, MethodType concatType, String recipe, Object... constants) throws StringConcatException {
        Objects.requireNonNull(lookup, "Lookup is null");
        Objects.requireNonNull(name, "Name is null");
        Objects.requireNonNull(recipe, "Recipe is null");
        Objects.requireNonNull(concatType, "Concat type is null");
        Objects.requireNonNull(constants, "Constants are null");
        // TODO
        throw new UnsupportedOperationException();
    }

    private StringConcatFactory() {

    }
}
