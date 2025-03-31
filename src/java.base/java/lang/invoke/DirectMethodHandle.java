package java.lang.invoke;

sealed class DirectMethodHandle extends MethodHandle {
    final MemberName member;

    private DirectMethodHandle(MethodType mtype, MemberName member) {
        super(mtype);
        this.member = member;
    }

    Object checkReceiver(Object recv) {
        throw new InternalError("Should only be invoked on a subclass");
    }

    static final class Special extends DirectMethodHandle {
        private final Class<?> caller;

        private Special(MethodType mtype, MemberName member, Class<?> caller) {
            super(mtype, member);
            this.caller = caller;
        }

        @Override
        boolean isInvokeSpecial() {
            return true;
        }

        // @Override
        // MethodHandle copyWith(MethodType mt, LambdaForm lf) {
        //     return new Special(mt, lf, member, crackable, caller);
        // }

        // @Override
        // MethodHandle viewAsType(MethodType newType, boolean strict) {
        //     assert(viewAsTypeChecks(newType, strict));
        //     return new Special(newType, form, member, false, caller);
        // }

        @Override
        Object checkReceiver(Object recv) {
            if(!caller.isInstance(recv)) {
                if(recv != null) {
                    String msg = "Receiver class " + recv.getClass().getName() + " is not a subclass of caller class " + caller.getName();
                    throw new IncompatibleClassChangeError(msg);
                }
                else {
                    String msg = "Cannot invoke " + member + " with null receiver";
                    throw new NullPointerException(msg);
                }
            }
            return recv;
        }
    }

    static final class Interface extends DirectMethodHandle {
        private final Class<?> refc;
        private Interface(MethodType mtype, MemberName member, Class<?> refc) {
            super(mtype, member);
            this.refc = refc;
        }

        // @Override
        // MethodHandle copyWith(MethodType mt, LambdaForm lf) {
        //     return new Interface(mt, lf, member, crackable, refc);
        // }

        // @Override
        // MethodHandle viewAsType(MethodType newType, boolean strict) {
        //     assert(viewAsTypeChecks(newType, strict));
        //     return new Interface(newType, form, member, false, refc);
        // }

        @Override
        Object checkReceiver(Object recv) {
            if(!refc.isInstance(recv)) {
                if(recv != null) {
                    String msg = "Receiver class " + recv.getClass().getName() + " does not implement the requested interface " + refc.getName();
                    throw new IncompatibleClassChangeError(msg);
                }
                else {
                    String msg = "Cannot invoke " + member + " with null receiver";
                    throw new NullPointerException(msg);
                }
            }
            return recv;
        }
    }

    static final class Constructor extends DirectMethodHandle {
        final MemberName initMethod;
        final Class<?> instanceClass;

        private Constructor(MethodType mtype, MemberName constructor, MemberName initMethod, Class<?> instanceClass) {
            super(mtype, constructor);
            this.initMethod = initMethod;
            this.instanceClass = instanceClass;
        }

        // @Override
        // MethodHandle copyWith(MethodType mt, LambdaForm lf) {
        //     return new Constructor(mt, lf, member, crackable, initMethod, instanceClass);
        // }

        // @Override
        // MethodHandle viewAsType(MethodType newType, boolean strict) {
        //     assert(viewAsTypeChecks(newType, strict));
        //     return new Constructor(newType, form, member, false, initMethod, instanceClass);
        // }
    }
}
