package parser;

import java.util.ArrayList;

public class CommandParser {

    public ParsedCommand parse(String line) throws ParseException {
        return new ParsedCommand("", new ArrayList<String>());
    }
}