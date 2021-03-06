package com.grognak.sobuddy.app;

import com.grognak.sobuddy.commands.BaseCommand;
import com.grognak.sobuddy.commands.HelpCommand;
import com.grognak.sobuddy.commands.QuitCommand;
import com.grognak.sobuddy.commands.SearchCommand;

import java.util.*;

public class QueryTerminal {
    private Scanner inputScanner;
    private boolean terminalIsActive;
    private Map<BaseCommand, ArrayList<String>> commands;

    public QueryTerminal() {
        inputScanner = new Scanner(System.in);
        terminalIsActive = false;
        commands = new HashMap<BaseCommand, ArrayList<String>>();
        refreshCommands();
    }

    private void refreshCommands() {
        commands.clear();
        BaseCommand[] commandList = {
                new HelpCommand(this),
                new QuitCommand(this),
                new SearchCommand(this),
        };

        for (BaseCommand command : commandList) {
            commands.put(command, command.getAliases());
        }
    }

    public BaseCommand commandSearch(String commandToken) {
        for (BaseCommand command : getCommandList()) {
            for (String alias : command.getAliases()) {
                if (commandToken.equals(alias)) {
                    return command;
                }
            }
        }
        throw new NoSuchElementException();
    }

    public Set<BaseCommand> getCommandList() {
        return commands.keySet();
    }

    public void startLoop() {
        terminalIsActive = true;

        try {
            while (terminalIsActive) {
                System.out.print("> ");
                String input = inputScanner.nextLine();
                System.out.println(parseInput(input));
            }
        }
        catch (RuntimeException e) {
            // User quit program
        }
    }

    private String parseInput(String input) {
        BaseCommand command;
        String inputCommand = extractCommand(input);
        String[] parameters = splitParameters(input);

        try {
            command = commandSearch(inputCommand);
            return command.executeAndGetOutput(parameters);
        }
        catch (NoSuchElementException e) {
            return "Command not found. Type 'commands' for help.";
        }
    }

    private String extractCommand(String input) {
        return splitParameters(input)[0].toLowerCase();
    }

    private String[] splitParameters(String input) {
        return input.split("\\s+");
    }
}
