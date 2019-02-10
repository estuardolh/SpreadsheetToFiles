# SpreadsheetToFiles
Generate Text FileS using a Spreadsheet and Templates for rendering.

## Examples
Files for example:
```
- stage/
  - tests.ods   spreadsheet, sheet products, table with headers id,name,cost,units
  - templates/  templates JSON, SQL, XML
    - product.json
    - product.sql
    - product.xml
  - outputs/    generated/renderized files
```

### Execution:
```
  java -jar tests.ods -t ./stage/templates -o ./stage/outputs
```
### Outputs:

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

## Libraries
- Apache libraries, [SODS](https://github.com/miachm/SODS), POI, Freemaker are under [Apache License Version 2.0](lib/LICENSE)
