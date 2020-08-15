# StringParser 
StringParser is a simple string parsing library

### Introduction

Every text in {} is a code that will be parsed. 
Every text in '' is a string that will not be parsed. 
You can use {} in '' if you want it to be parsed. 

### Functions
```
num = Number
bool = True/False
str = String (text)
obj = Object (anything)
```


- ADD(num, num) -> num   Adds the 2 numbers together
- AND(bool...) -> bool   Returns True is all parameters are true, otherwise returns false.
- CONCAT(obj...) -> str   Joins all the parameters together
- ENDSWITH(obj, obj...) -> bool   Returns true, if the first parameter ends with any of the following parameters
- EQ(obj, obj) -> bool   Returns true if the first and the second parameter match
- GTEQ(num, num) -> bool   Returns true if the first parameter is greater than or equal to the second
- GT(num, num) -> bool   Returns true if the first parameter is greater than the second
- IF(bool, obj, obj) -> obj   If the first parameter is True, the second parameter is returned, if the first parameter is False, the third parameter is returned.
- LTEQ(num, num) -> bool   Returns true if the first parameter is less than or equal to the second
- LT(num, num) -> bool   Returns true if the first parameter is less than the second
- NE(obj, obj) -> bool   Returns true if the 2 objects are not equal
- NOT(bool) -> bool   Returns the opposite boolean value
- OR(bool...) -> bool   Returns True if 1 or more of the values is True.
- STARTSWITH(obj, obj...) -> bool   Returns true if the first parameter starts with any of the following parameters
- TRUE() -> bool   Returns true
- FALSE() -> bool   Returns false



### Usage:

```
StringParser parser = new StringParser();
String result = parser.parse("{CONCAT('Hello', ' ', 'world', '!')}");  // Hello world!

parser.setValue("name", "Lebowski");
result = parser.parse("Where is the money {name}?"); // Where is the money Lebowski?
```


### Some more examples:

```
- {players} player{IF(EQ(players, 1), '', 's')}      // players = 1
1 player
- You were slain by {IF(isname, name, CONCAT(IF(STARTSWITH(name, 'a', 'e', 'i', 'o', 'u'), 'an', 'a'), ' ', name))}   // name = Creeper, isname = false
You were slain by a Creeper
- 2 + 2 = {ADD(2, 2)}
4
- {IF(TRUE(), 'Always true', 'I think I forgot something')}
Always true
```
