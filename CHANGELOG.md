# Change Log
## V0.0.9
- Remove RGB444 Graphics ColorMode.
- Support PinController.toggle() method.
- Added support for the following methods for AbstractStringBuilder:
  - AbstractStringBuilder.delete
  - AbstractStringBuilder.deleteCharAt
  - AbstractStringBuilder.replace
  - AbstractStringBuilder.insert
  - AbstractStringBuilder.repeat
  - AbstractStringBuilder.setLength
- Add jdk.internal.math.DoubleConsts and jdk.internal.math.FloatConsts.
- Complete for java.math.BigInteger.
- Added support for java.math.BigDecimal.
- Added support for support Math.clamp method.
- Add java.util.HashSet and java.util.ImmutableCollections classes.
- Add java.io.Closeable.
## V0.0.8
- Support creating jar files including java source.
- Update flint.machine package.
## V0.0.7
- Support following classes:
  - java.util.Collections.
  - java.util.TreeMap.
  - java.util.TreeSet.
  - java.util.Hashtable.
  - java.util.HashMap.
  - java.util.LinkedHashMap.
- Implement Comparator.reversed and Comparator.reverseOrder methods.
- Implement ReverseOrderListView.sort method.
- Implement Vector.subList method.
## V0.0.6
- Implement the following functions for java.util.Arrays:
  - Arrays.sort.
  - Arrays.equals.
  - Arrays.deepEquals.
  - Arrays.compare.
  - Arrays.mismatch.
- Support java.util.ArrayList, java.util.Vector and java.util.LinkedList.
## V0.0.5
- Fix bug in Long.toString method.
- Implement the following methods in java.lang.Class:
  - Class.getInterfaces.
  - Class.getField.
  - Class.getFields.
  - Class.getMethod.
  - Class.getMethods.
  - Class.getConstructor.
  - Class.getConstructors.
  - Class.desiredAssertionStatus.
  - Class.getDeclaringClass.
## V0.0.4
- Add support for classes:
  - java.util.StringJoiner.
  - java.util.Arrays (partial support).
  - java.util.Set (partial support).
  - java.util.Map (partial support).
  - Some exception classes and annotations.
- Add java.lang.reflect package (not completed).
- Update java.lang.Class (support componentType, arrayType, descriptorString).
- Support String.join method.
- Support stringSize method in Integer and Long classes.
- Rename String.toLower to String.toLowerCase.
- Rename String.toUpper to String.toUpperCase.
## V0.0.3
- Add packages:
  - jdk.internal.vm.annotation.
  - java.util.function.
  - flint.drawing.
  - flint.machine.
- Add classes:
  - java.util.Objects.
  - java.util.Comparator.
  - java.util.Comparators.
  - java.util.List.
  - java.lang.IncompatibleClassChangeError.
  - java.lang.NoSuchFieldError.
## V0.0.2
- Add method identityHashCode to java.lang.System class.
- Implementing BigInteger methods (Incomplete).
- Add additional Exception classes.
## V0.0.1
- The first version can run on the FlintJVM.
- Supports the most basic classes like String, System, Thread, PrintStream, Exception...