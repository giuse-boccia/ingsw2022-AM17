package it.polimi.ingsw.messages.action;

public class Action {

    private final String name;
    private final ActionArgs args;

    public Action(String name, ActionArgs args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public ActionArgs getArgs() {
        return args;
    }
}
