package parser;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    public ParsedCommand parse(String line) throws ParseException {
        if (line.isBlank()) {
            return new ParsedCommand("", new ArrayList<>());
        }
        String[] splitLine = line.trim().split(" ");
        String command = splitLine[0];
        List<String> args = new ArrayList<>();
        for (int i = 1; i < splitLine.length; i++) {
            args.add(splitLine[i]);
        }
        return new ParsedCommand(command, args);
    }
}