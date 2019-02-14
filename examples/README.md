## Examples

**ODs content**

![ODs content](./examples/ods_content.png "ODs file example")

**Template**

```
<?xml version="1.0" encoding="UTF-8"?>
<products>
<#list products as product>
  <product>
    <id>${product.id}</id>
    <units>${product.units?eval}</units>
    <cost>${product.cost}</cost>
    <name>${product.name}</name>
  </product>
</#list>
</products>
```

**Execution:**
```
  java -jar SpreadsheetToFiles-1.0.jar tests.ods -t ./examples/templates/ -o ./examples/outputs/
  or
  java -jar SpreadsheetToFiles-1.0.jar
```
Note: For the second way to execute it uses configuration.ini parameters.


**Output**

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

