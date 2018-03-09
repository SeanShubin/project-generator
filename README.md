# Project Generator
- Convert minimal project specifications to project structures required by specific tools

## Why this project exists
- I found creation and maintenance of maven project files tedious
- Particularly onerous were
    - the parent/child structure
    - updating the dependency structure
    - updating the module structure
    - remembering all the details required to push to maven central, especially the javadoc configuration
- Now I can manage my dependency structure from a single file in a concise, minimal format.

## How to use
- Create a directory where you want your project to reside
- Create a specification text file in [Developers Value Notation](https://github.com/SeanShubin/devon)
    - See comments in the [Specification](domain/src/main/scala/com/seanshubin/project/generator/domain/Specification.scala) source file for details
- Run [com.seanshubin.project.generator.console.EntryPoint](console/src/main/scala/com/seanshubin/project/generator/console/EntryPoint.scala) with the path to your specification file as the only command line argument
- All of the maven files will be generated with the proper dependency structure, as well as configuration options and scripts necessary for deploying to maven central
- If your dependency structure changes, update your specification file and re-run the project generator.  Your old files will be clobbered with the updated dependency structure.
    - modules no longer in your dependency structure will not be deleted. 

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