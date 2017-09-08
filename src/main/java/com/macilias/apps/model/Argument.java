package com.macilias.apps.model;

import java.util.List;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class Argument {

    private ArgumentName argumentName;
    private List<String> argumentValues;

    public Argument(ArgumentName argumentName, List<String> argumentValues) {
        this.argumentName = argumentName;
        this.argumentValues = argumentValues;
    }

    public ArgumentName getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(ArgumentName argumentName) {
        this.argumentName = argumentName;
    }

    public List<String> getArgumentValues() {
        return argumentValues;
    }

    public void setArgumentValues(List<String> argumentValues) {
        this.argumentValues = argumentValues;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "argumentName=" + argumentName +
                ", argumentValues=" + argumentValues +
                '}';
    }
}
