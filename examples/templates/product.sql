BEGIN
<#list products as product>
  INSERT INTO PRODUCT(id, units, cost, name) VALUES('${product.id}', ${product.units?eval}, ${product.cost}, '${product.name}');
</#list>
END;
