package commands;

import java.util.List;

public class CdCommand implements Command {

    @Override
    public String execute(List<String> args) {
        return "cd " + args;
    }
}