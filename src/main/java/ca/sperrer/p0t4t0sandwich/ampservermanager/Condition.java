package ca.sperrer.p0t4t0sandwich.ampservermanager;

public class Condition {
    public final String placeholder;
    public final String operator;
    public final Object value;

    // Constructor
    public Condition(String placeholder, String operator, Object value) {
        this.placeholder = placeholder;
        this.operator = operator;
        this.value = value;
    }
}