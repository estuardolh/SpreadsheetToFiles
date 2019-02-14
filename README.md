# SpreadsheetToFiles
Generate Text Files using a Spreadsheet and Templates for rendering.

It uses ODS as format for Spreadsheet, and Apache Freemaker as template system.

You can create your own template! so, imagination is the limit.

```
usage: java -jar SpreadsheetToFiles.jar <input ods file> -t <templates directory> -o <output directory> [ -d ]
 -d                         debug mode on
 -o <output directory>      output directory path
 -t <templates directory>   templates directory path
 -version                   show version
```

#### Templates
For template design check out [here](https://freemarker.apache.org/docs/dgui.html)	

## Examples
For more examples checkout [here](./examples/)

## Development
To test:

```
mvn test
```

To generate executable jar:

```
mvn package
```

## Libraries
- [Apache commons](http://commons.apache.org/), for license see [here](http://www.apache.org/licenses/)
- [Apache Freemaker](https://freemarker.apache.org/), for licence see [here](https://freemarker.apache.org/docs/app_license.html)
- [SODS](https://github.com/miachm/SODS), for licence see [here](https://github.com/miachm/SODS/blob/master/LICENSE)
