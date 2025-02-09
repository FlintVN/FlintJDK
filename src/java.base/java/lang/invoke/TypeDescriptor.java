package java.lang.invoke;

import java.util.List;

public interface TypeDescriptor {
    String descriptorString();

    interface OfField<F extends TypeDescriptor.OfField<F>> extends TypeDescriptor {
        boolean isArray();

        boolean isPrimitive();

        F componentType();

        F arrayType();
    }

    interface OfMethod<F extends TypeDescriptor.OfField<F>, M extends TypeDescriptor.OfMethod<F, M>> extends TypeDescriptor {
        int parameterCount();

        F parameterType(int i);

        F returnType();

        F[] parameterArray();

        List<F> parameterList();

        M changeReturnType(F newReturn);

        M changeParameterType(int index, F paramType);

        M dropParameterTypes(int start, int end);

        @SuppressWarnings("unchecked")
        M insertParameterTypes(int pos, F... paramTypes);
    }
}
