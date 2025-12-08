# Change Log
## V1.2.0
- Split flint.machine.CommPort class into flint.machine.InputPort and flint.machine.OutputPort.
- Support RxBufferSize parameter for SerialPort.
- Added support for the following classes to flint.io:
  - flint.machine.I2sMaster.
  - flint.machine.Adc.
  - flint.machine.Dac.
  - flint.machine.AnalogInputStream.
  - flint.machine.AnalogOutputStream.
## V1.1.0
- Support Throwable.addSuppressed and Throwable.getSuppressed to fix build error with try-with-resource.
- Implement FileInputStream.transferTo method.
- Split flint API into separate modules.
- Add support for java.util.Optional.
- Add support for some more protocols in flint.io:
  - flint.machine.SerialPort.
  - flint.machine.I2cMaster.
  - flint.machine.BitStream.
  - flint.machine.OneWire.
- Add support String.getBytes to get bytes in UTF8 format
## V1.0.0
- Add java.io.FilterOutputStream.
- Update java.io.PrintStream to inherit from FilterOutputStream.
- Use FileOutputStream(FileDescriptor.out) as default for System.out.
- flint.machine.CommInterface now extends AutoCloseable and added throws IOException to all methods in this class.
## V0.1.0
- Add missing methods to java.lang.Math class.
- Add support for Map.of, Map.ofEntries and Map.copyOf methods.
- Rename Character.toLower to Character.toLowerCase and Character.toUpper to Charactor.toUpperCase.
- Add support for String.compareToIgnoreCase method.
- Add support for java.io.File, java.io.FileInputStream and java.io.FileOutputStream.
## V0.0.11
- Fix bug for Integer.toString() and Long.toString() with MIN_VALUE.
- Add support Integer.getChars, Long.getChars and StringUTF16.getChars methods.
- Add java.lang.IllegalMonitorStateException and java.lang.StackOverflowError.
- Fix bug in Class.descriptorString method with void type.
## V0.0.10
- Added support Class.asSubclass, Class.getNestHost, Class.isNestmateOf and Class.getNestMembers methods.
- Added support some mentods in jdk.internal.reflect.Reflection.
- Removed flint.drawing package (will be split into separate library in future).
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