# Project Generator
- Convert minimal project specifications to project structures required by specific tools

## Example
- devon specification to maven scala console application
- command line arguments
    - project-specification.txt ../../generated-projects

```
{
  pattern scala-console-application
  name [developers value notation]
  description 'A simple, language neutral notation for representing structured values'
  groupPrefix [com seanshubin]
  version 1.1.1
  dependencies {
    scala-reflect {
      group org.scala-lang
      artifact scala-relfect
    }
  }
  modules {
    domain { dependencies [tokenizer reflection] }
    tokenizer { dependencies [rules] }
    rules { dependencies [parser] }
    parser { dependencies [string] }
    reflection { dependencies [scala-reflect] }
    string { dependencies [] }
  }
}
```
