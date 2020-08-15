package me.honsesco.checker;

import java.util.*;

public class Worker {

    private final List<String> functions = Arrays.asList("EQ", "LTEQ", "LT", "GT", "GTEQ", "IF", "CONCAT",
            "ADD", "AND", "NOT", "OR", "ENDSWITH", "STARTSWITH", "NE", "TRUE", "FALSE");
    private final Map<String, String> values = new HashMap<>();

    public boolean isValue(String value) {
        return values.containsKey(value);
    }

    public String getValue(String key) {
        return values.get(key);
    }

    private int parseInt(String toParse) {
        return Integer.parseInt(toParse);
    }

    public void setValue(String key, String value) {
        values.put(key, value);
    }

    public String parse(String input) {
        return parseCode(input);
    }

    public void clearValues() {
        values.clear();
    }

    public void removeValue(String key) {
        values.remove(key);
    }

    private String parseCode(String input) {
        if (hasCode(input)) {
            StringBuilder result = new StringBuilder();
            int current = input.indexOf("{");

            if (current > 0) result.append(input, 0, current);
            while (hasCode(input, current)) {
                String code = getFunctionBody(input, current);
                if (code.equals("error")) return "error";
                String codeContent = getCodeContent(code);
                String parsedCode = parseFunction(codeContent);
                if (parsedCode.equals("error")) return "error";

                if (codeContent.equals(parsedCode)) {
                    result.append(code);
                } else {
                    result.append(parsedCode);
                }

                current += code.length();
                int indexOfOpenedBrace = input.indexOf("{", current);
                if (indexOfOpenedBrace == -1) break;
                result.append(input, current, indexOfOpenedBrace);
                current = indexOfOpenedBrace;
            }
            if (current >= 0 && current < input.length()) result.append(input.substring(current));
            return result.toString();
        } else {
            return "error";
        }
    }

    private String getIfExists(String key) {
        return isValue(key) ? getValue(key) : key;
    }

    private boolean hasCode(String input) {
        return hasCode(input, 0);
    }

    private boolean hasCode(String input, int start) {
        int a = input.indexOf("{", start);
        int b = input.lastIndexOf("}");
        return start >= 0 && a != -1 && b != -1 && a < b;
    }

    private String parseFunction(String input) {
        if (isFunction(input)) {
            String name = getFunctionName(input);

            if (name.equals("IF")) {
                List<String> list = parseComplexList(getFunctionContent(input));
                if (list.size() != 3) return "error";
                String condition = parseFunction(list.get(0));
                return condition.equals("true") ? parseFunction(list.get(1)) : condition.equals("false") ? parseFunction(list.get(2)) : "error";
            } else if (name.equals("EQ")) {
                List<String> list = parseSimpleList(getFunctionContent(input));
                if (list.size() != 2) return "error";
                String value1 = getIfExists(list.get(0));
                String value2 = getIfExists(list.get(1));
                return String.valueOf(value1.equals(value2));
            } else if (name.equals("NE")) {
                List<String> list = parseSimpleList(getFunctionContent(input));
                if (list.size() != 2) return "error";
                String value1 = getIfExists(list.get(0));
                String value2 = getIfExists(list.get(1));
                return String.valueOf(!value1.equals(value2));
            } else if (name.equals("LTEQ")) {
                List<String> list = parseSimpleList(getFunctionContent(input));
                if (list.size() != 2) return "error";
                String sValue1 = getIfExists(list.get(0));
                String sValue2 = getIfExists(list.get(1));
                if (!isInteger(sValue1) || !isInteger(sValue2)) return "error";
                return String.valueOf(parseInt(sValue1) <= parseInt(sValue2));
            } else if (name.equals("LT")) {
                List<String> list = parseSimpleList(getFunctionContent(input));
                if (list.size() != 2) return "error";
                String sValue1 = getIfExists(list.get(0));
                String sValue2 = getIfExists(list.get(1));
                if (!isInteger(sValue1) || !isInteger(sValue2)) return "error";
                return String.valueOf(parseInt(sValue1) < parseInt(sValue2));
            } else if (name.equals("GTEQ")) {
                List<String> list = parseSimpleList(getFunctionContent(input));
                if (list.size() != 2) return "error";
                String sValue1 = getIfExists(list.get(0));
                String sValue2 = getIfExists(list.get(1));
                if (!isInteger(sValue1) || !isInteger(sValue2)) return "error";
                return String.valueOf(parseInt(sValue1) >= parseInt(sValue2));
            } else if (name.equals("GT")) {
                List<String> list = parseSimpleList(getFunctionContent(input));
                if (list.size() != 2) return "error";
                String sValue1 = getIfExists(list.get(0));
                String sValue2 = getIfExists(list.get(1));
                if (!isInteger(sValue1) || !isInteger(sValue2)) return "error";
                return String.valueOf(parseInt(sValue1) > parseInt(sValue2));
            } else if (name.equals("CONCAT")) {
                List<String> list = parseComplexList(getFunctionContent(input));
                StringBuilder sb = new StringBuilder();
                for (String str : list) {
                    String parsed = parseFunction(str);
                    if (parsed.equals("error")) return "error";
                    sb.append(parsed);
                }
                return sb.toString();
            } else if (name.equals("ADD")) {
                List<String> list = parseSimpleList(getFunctionContent(input));
                if (list.size() != 2) return "error";
                String sValue1 = getIfExists(list.get(0));
                String sValue2 = getIfExists(list.get(1));
                if (!isInteger(sValue1) || !isInteger(sValue2)) return "error";
                return String.valueOf(parseInt(sValue1) + parseInt(sValue2));
            } else if (name.equals("AND")) {
                List<String> list = parseComplexList(getFunctionContent(input));
                for (String str : list) {
                    if (isBoolean(str)) return "error";
                    String parsed = parseFunction(str);
                    if (parsed.equals("false")) return "false";
                    else if (!parsed.equals("true")) return "error";
                }
                return "true";
            } else if (name.equals("NOT")) {
                String functionContent = getFunctionContent(input);
                if (isBoolean(functionContent)) return "error";
                String parsed = parseFunction(functionContent);
                return isBoolean(parsed) ? String.valueOf(!Boolean.valueOf(parsed)) : "error";
            } else if (name.equals("OR")) {
                List<String> list = parseComplexList(getFunctionContent(input));
                if (list.size() == 0) return "error";
                for (String str : list) {
                    if (isBoolean(str)) return "error";
                    String parsed = parseFunction(str);
                    if (parsed.equals("true")) return "true";
                    else if (!parsed.equals("false")) return "error";
                }
                return "false";
            } else if (name.equals("ENDSWITH")) {
                List<String> list = parseComplexList(getFunctionContent(input));
                if (list.size() <= 1) return "error";
                String value = parseFunction(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    if (value.endsWith(parseFunction(list.get(i)))) return "true";
                }
                return "false";
            } else if (name.equals("STARTSWITH")) {
                List<String> list = parseComplexList(getFunctionContent(input));
                if (list.size() <= 1) return "error";
                String value = parseFunction(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    if (value.startsWith(parseFunction(list.get(i)))) return "true";
                }
                return "false";
            } else if (name.equals("TRUE")) {
                return "true";
            } else if (name.equals("FALSE")) {
                return "false";
            }

            return "error";
        } else {
            if (isValue(input)) return getValue(input);
            if (hasCode(input)) input = parseCode(input);
            return isString(input) ? getStringContent(input) : input;
        }
    }

    private boolean isBoolean(String str) {
        return str.equals("true") || str.equals("false");
    }

    private boolean isInteger(String str) {
        return str.matches("[-+]?\\d+");
    }

    private boolean isString(String str) {
        int a = str.indexOf("'");
        int b = str.lastIndexOf("'");
        return a != -1 && b != -1 && a < b;
    }

    private String getStringContent(String str) {
        int a = str.indexOf("'");
        int b = str.lastIndexOf("'");
        return a != -1 && b != -1 && a < b ? str.substring(a + 1, b) : str;
    }

    private String getFunctionBody(String input, int start) {
        int a = 0;    // '
        int ba = 0;   // {
        int bb = 0;   // }
        int ca = 0;   // (
        int cb = 0;   // )

        int current = start;

        while (input.length() > current) {
            char symbol = input.charAt(current);

            // for special chars
            if (symbol == '\\') {
                if (current + 1 == input.length()) return "error"; //error

                char specialChar = input.charAt(current + 1);
                if (!isSpecialChar(specialChar)) current--;
                current += 2;
                continue;
            }

            if (symbol == '\'') a++;
            else if (symbol == '{') ba++;
            else if (symbol == '}') bb++;
            else if (symbol == '(') ca++;
            else if (symbol == ')') cb++;
            else if (symbol == ',' && a >= 0 && ba >= 0 && ca >= 0 && a % 2 == 0 && ba == bb && ca == cb) {
                return input.substring(start, current);
            } else {
                current++;
                continue;
            }

            if (a >= 0 && ba >= 0 && ca >= 0 && a % 2 == 0 && ba == bb && ca == cb) {
                return input.substring(start, current + 1);
            }

            current++;
        }

        return "error";
    }

    private boolean isSpecialChar(char symbol) {
        return symbol == '\'' || symbol == '(' || symbol == ')' || symbol == '{' || symbol == '}';
    }

    private String getFunctionName(String input) {
        int a = input.indexOf("(");
        return a == -1 ? "" : input.substring(0, a).trim();
    }

    private boolean isFunction(String function) {
        int a = function.indexOf("(");
        int b = function.indexOf(")");
        return a != -1 && b != -1 && a < b && functions.contains(function.substring(0, a).trim());
    }

    private List<String> parseSimpleList(String input) {
        int start = 0;
        List<String> list = new ArrayList<>();
        for (String s : input.substring(start).split(",")) {
            list.add(s.trim());
        }
        return list;
    }

    private List<String> parseComplexList(String input) {
        int start = 0;
        List<String> list = new ArrayList<>();
        while (input.indexOf(",", start) != -1) {
            String body = getFunctionBody(input, start);
            if (body.equals("error")) return new ArrayList<>();
            int startsAt = input.indexOf(body, start);
            int comma = input.indexOf(",", startsAt + body.length());
            if (comma == -1) break;
            start = comma + 1;
            list.add(body.trim());
        }
        list.add(input.substring(start).trim());
        return list;
    }

    private String getFunctionContent(String input) {
        int a = input.indexOf("(");
        int b = input.lastIndexOf(")");
        return a == -1 || b == -1 ? input : input.substring(a + 1, b);
    }

    private String getCodeContent(String input) {
        int a = input.indexOf("{");
        int b = input.lastIndexOf("}");
        return a == -1 || b == -1 ? input : input.substring(a + 1, b);
    }

}
