# SpreadsheetToFiles
Generate Text Files using a Spreadsheet and Templates for rendering.

It uses ODS as format for Spreadsheet, and Apache Freemaker as template system.

```
usage: java -jar SpreadsheetToFiles.jar <input ods file> -t <templates directory> -o <output directory> [ -d ]
 -d                         debug mode on
 -o <output directory>      output file path
 -t <templates directory>   templates directory
```

#### Templates
For template design check out [here](https://freemarker.apache.org/docs/dgui.html)	



## Examples
Files for example:
```
- stage/
  - tests.ods              spreadsheet, sheet products, table with headers id,name,cost,units
  - templates/             templates JSON, SQL, XML
    - product.json
    - product.sql
    - product.xml
    - product.yml
  - outputs/               generated/renderized files
```

#### Execution:
```
  java -jar tests.ods -t ./stage/templates/ -o ./stage/outputs/
```
#### Outputs:

product.json
```
[
  {
  "id":"M1", 
  "units":"100",
  "cost":"10.0",
  "name":"SMART PHONE P"
  },

  {
  "id":"M2", 
  "units":"200",
  "cost":"20.0",
  "name":"SMART PHONE Q"
  },

  {
  "id":"M3", 
  "units":"300",
  "cost":"30.5",
  "name":"SMART PHONE R"
  }
]
```

product.sql:
```
BEGIN
  INSERT INTO PRODUCT(id, units, cost, name) VALUES('M1', 100, 10.0, 'SMART PHONE P');
  INSERT INTO PRODUCT(id, units, cost, name) VALUES('M2', 200, 20.0, 'SMART PHONE Q');
  INSERT INTO PRODUCT(id, units, cost, name) VALUES('M3', 300, 30.5, 'SMART PHONE R');
END;
```

product.xml:
```
<?xml version="1.0" encoding="UTF-8"?>
<products>
  <product>
    <id>M1</id>
    <units>100</units>
    <cost>10.0</cost>
    <name>SMART PHONE P</name>
  </product>
  <product>
    <id>M2</id>
    <units>200</units>
    <cost>20.0</cost>
    <name>SMART PHONE Q</name>
  </product>
  <product>
    <id>M3</id>
    <units>300</units>
    <cost>30.5</cost>
    <name>SMART PHONE R</name>
  </product>
</products>
```

## Development
To test:
```
mvn test
```

To generate executable jar:
```
mvn package
```

## Libraries used
- [Apache commons](http://commons.apache.org/), for license see [here](http://www.apache.org/licenses/)
- [Apache Freemaker](https://freemarker.apache.org/), for licence see [here](https://freemarker.apache.org/docs/app_license.html)
- [SODS](https://github.com/miachm/SODS), for licence see [here](https://github.com/miachm/SODS/blob/master/LICENSE)
