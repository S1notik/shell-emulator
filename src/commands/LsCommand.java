package commands;


import java.util.List;

public class LsCommand implements Command {

    @Override
    public String execute(List<String> args) {
        return "ls " + args;
    }

}