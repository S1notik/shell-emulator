package parser;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    public ParsedCommand parse(String line) throws ParseException {
        if (line.isBlank()) {
            return new ParsedCommand("", new ArrayList<>());
        }
        StringBuilder current = new StringBuilder();
        List<String> tokens = new ArrayList<>();
        Character quote = null;
        boolean started = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quote != null) {
                if (c == quote) {
                    quote = null;
                    started = true;
                } else {
                    current.append(c);
                }
                continue;
            }
            if (c == '"' || c == '\'') {
                quote = c;
                continue;
            }
            if (Character.isWhitespace(c)) {
                if (started) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                    started = false;
                }
                continue;
            }
            current.append(c);
            started = true;
        }
        if (quote != null) {
            throw new ParseException("Незакрытая кавычка: " + line);
        }
        if (started) {
            tokens.add(current.toString());
        }
        String command = tokens.get(0);
        List<String> args = new ArrayList<>();
        for (int i = 1; i < tokens.size(); i++) {
            args.add(tokens.get(i));
        }
        return new ParsedCommand(command, args);
    }
}