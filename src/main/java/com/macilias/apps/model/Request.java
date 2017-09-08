package com.macilias.apps.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.*;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class Request {

    public Request() {
        this.date = new Date();
    }

    private Intent intent;
    private Set<Argument> arguments = new HashSet<>();

    private Date date;
    private Response response;

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        Validate.notNull(intent, "intent can not be NULL");
        if (this.intent != null) {
            throw new RuntimeException("The intent of this request has already been set to " + this.intent.name());
        }
        this.intent = intent;
    }

    public Set<Argument> getArguments() {
        return arguments;
    }

    public Optional<Argument> getOptionalArgument(ArgumentName argumentName) {
        for (Argument argument : arguments) {
            if (argument.getArgumentName().equals(argumentName)) {
                return Optional.of(argument);
            }
        }
        return Optional.empty();
    }

    /**
     * This method throws an RuntimeException if the argument is not present
     * @param argumentName
     * @return
     */
    public Argument getArgument(ArgumentName argumentName) {
        for (Argument argument : arguments) {
            if (argument.getArgumentName().equals(argumentName)) {
                return argument;
            }
        }
        throw new RuntimeException("This intent " + intent.name() + " does not have the argument " + argumentName.name());
    }

    public void addArgument(ArgumentName argumentName, String... values) {
        arguments.add(new Argument(argumentName, Arrays.asList(values)));
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "Request{" +
                "intent=" + intent +
                ", arguments=" + StringUtils.join(arguments, ", ") +
                ", date=" + date +
                ", response='" + response + '\'' +
                '}';
    }
}
