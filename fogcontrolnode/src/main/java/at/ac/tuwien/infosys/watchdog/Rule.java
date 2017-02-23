package at.ac.tuwien.infosys.watchdog;

import lombok.Data;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 * Watchdog rule to specify a specific threshold by identifier, operator, and value. This rule can then be checked
 * by the provided checkrule method
 */
@Data
public class Rule {

    private String identifier;
    private RuleOperator operator;
    private double value;

    public Rule(String identifier, RuleOperator operator, double value){
        this.identifier = identifier;
        this.operator = operator;
        this.value = value;
    }

    public boolean checkRule(double input){
        switch(this.operator){
            case BIGGER:
                return input > value;
            case SMALLER:
                return input < value;
            case IS:
                return value == input;
            case IS_NOT:
                return value != input;
            default:
                return false;
        }
    }
}
