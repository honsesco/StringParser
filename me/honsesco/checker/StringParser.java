package me.honsesco.checker;

public class StringParser {

    private Worker worker;
    private String input;

    public StringParser() {
        worker = new Worker();
    }

    public void setString(String stringToParse) {
        input = stringToParse;
    }

    public String parse(String stringToParse) {
        setString(stringToParse);
        return parse();
    }

    public String parse() {
        if (input == null) return null;
        String result = worker.parse(input);
        return result.equals("error") ? input : result;
    }

    public void setValue(String key, String value) {
        worker.setValue(key, value);
    }

    public void setValue(String key, int value) {
        setValue(key, String.valueOf(value));
    }

    public void setValue(String key, double value) {
        setValue(key, String.valueOf(value));
    }

    public void unsetValue(String key) {
        worker.removeValue(key);
    }

    public void unsetValues() {
        worker.clearValues();
    }

}
