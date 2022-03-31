
## PDF Compression Frontend - A Ghostscript frontend to compress PDF files
PDF Compression Frontend (PDFCF) is a simple, handy GUI tool that helps you
easily compress PDF files with varying levels of conversion quality. It should
be especially useful when dealing with documents containing high-resolution
images.

Under the hood, PDFCF leverages the venerable
[Ghostscript](https://www.ghostscript.com/) interpreter. If you use Windows,
you can install Ghostscript by downloading it from its
[official website](https://www.ghostscript.com/releases/index.html). If you use
Linux, Ghostscript is most likely available in the software repository of your
distribution. For example, Debian/Ubuntu Linux users can install Ghostscript using
the `apt` package manager:

```bash
$ sudo apt install ghostscript
```

To run PDFCF, you need an installation of [Java Runtime Environment 8 or later][link-jdk]. 

### Building
To build PDFCF, you need [JDK 8 or later][link-jdk],
[Maven](https://maven.apache.org/) and [Ant](https://ant.apache.org/). Execute
the commands below in the project directory:

```bash
$ mvn clean package
$ ant
```

This will build the tool and copy distribution files into the `dist/`
subdirectory. If you would like to modify or customize the Windows `.exe`
launcher, use [Launch4j](http://launch4j.sourceforge.net/) to open the `.xml`
file in the `launch4j/` subdirectory, make the desired changes and regenerate
the `.exe` file to your liking. Enjoy!

[link-jdk]: https://adoptium.net/
