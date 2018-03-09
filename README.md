# Project Generator
- Convert minimal project specifications to project structures required by specific tools

## Specification

Specification is marshalled from a text file in [Developers Value Notation](https://github.com/SeanShubin/devon)

See comments in [Specification](domain/src/main/scala/com/seanshubin/project/generator/domain/Specification.scala) for details

## Examples

### Devon Specification
Note that detangler only runs for the domain module.  Omitted consoleEntryPoint and mavenPlugin. 
```
{
  prefix [com seanshubin]
  name [devon]
  description 'A simple, language neutral notation for representing structured values'
  version 1.1.2
  developer
  {
    name 'Sean Shubin'
    githubName SeanShubin
    mavenUserName SeanShubin
    organization 'Sean Shubin'
    url http://seanshubin.com/
  }
  dependencies
  {
    scala-library
    {
      group org.scala-lang
      artifact scala-library
    }
    scala-reflect
    {
      group org.scala-lang
      artifact scala-reflect
    }
    scala-test
    {
      group org.scalatest
      artifact scalatest_2.12
      scope test
    }
  }
  global
  [
    scala-library
    scala-test
  ]
  modules
  {
    domain     [tokenizer reflection]
    tokenizer  [rules]
    reflection [scala-reflect]
    rules      [parser]
    parser     [string]
    string     []
  }
  detangler [domain]
  primary domain
}
```

### Detangler Specification 
Only the "console" module has an entry point, which is com.seanshubin.detangler.console.ConsoleApplication.
The "maven-plugin" module is is a maven plugin.
Detangler is omitted.
```
{
  prefix [com seanshubin]
  name [detangler]
  description 'Analyzes dependency structure and cycles'
  version 0.9.1
  developer {
    name 'Sean Shubin'
    githubName SeanShubin
    organization 'Sean Shubin'
    url http://seanshubin.com/
  }
  dependencies {
    scala-library {
      group org.scala-lang
      artifact scala-library
      version 2.12.4
    }
    scala-test {
      group org.scalatest
      artifact scalatest_2.12
      version 3.0.4
      scope test
    }
    devon {
      group com.seanshubin.devon
      artifact devon-domain
      version 1.1.2
    }
    jsoup {
      group org.jsoup
      artifact jsoup
      version 1.11.2
    }
    maven-plugin-api {
      group org.apache.maven
      artifact maven-plugin-api
      version 3.5.2
    }
    maven-plugin-annotations {
      group org.apache.maven.plugin-tools
      artifact maven-plugin-annotations
      version 3.5
      scope provided
    }
  }
  modules {
    analysis       [model data collection devon]
    bytecode       [collection]
    collection     []
    compare        []
    console        [domain]
    contract       []
    contract-test  [contract]
    data           []
    domain         [analysis report scanner contract-test devon]
    graphviz       [collection compare]
    maven-plugin   [console maven-plugin-api maven-plugin-annotations]
    model          [compare]
    report         [model contract collection graphviz contract-test jsoup]
    scanner        [timer zip contract-test bytecode data contract]
    timer          []
    zip            [collection]
  }
  consoleEntryPoint {
    console com.seanshubin.detangler.console.ConsoleApplication
  }
  mavenPlugin [ maven-plugin ]
}
```

## To Do
- remove need to specify versions, use latest instead