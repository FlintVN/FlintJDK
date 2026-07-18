# FlintJDK build scripts

All three entry points discover modules from `src/*/module-info.java` and use
the shared `META-INF/MANIFEST.MF` when packaging every JAR.

Requirements: JDK 17 or newer (`javac` and `jar` in `PATH`).

```powershell
.\scripts\build.ps1
.\scripts\build.ps1 -Clean
```

```bat
scripts\build.bat
scripts\build.bat --clean
```

```sh
./scripts/build.sh
./scripts/build.sh --clean
```

Runtime JARs are written to `bin/run`. Development JARs, including source
files and debug information, are written to `bin/dev`.
