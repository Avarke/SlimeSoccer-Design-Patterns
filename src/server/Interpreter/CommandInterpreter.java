package server.Interpreter;

import java.util.HashMap;
import java.util.Map;


public class CommandInterpreter {
    private final Map<String, CommandExpression> commands;
    
    public CommandInterpreter() {
        commands = new HashMap<>();
        registerDefaultCommands();
    }
    
    private void registerDefaultCommands() {
        registerCommand(new HelpCommand());
        registerCommand(new SetBrightnessCommand());
        registerCommand(new ScoreCommand());
        registerCommand(new ResetCommand());
        registerCommand(new ReportCommand());
        registerCommand(new SetScoreCommand());
    }
    
    public void registerCommand(CommandExpression command) {
        commands.put(command.getCommandName().toLowerCase(), command);
    }
    
    
    public String interpret(String message) {
        if (message == null || !message.startsWith("/")) {
            return null; 
        }
        
        
        String commandLine = message.substring(1).trim();
        if (commandLine.isEmpty()) {
            return "Type /help for available commands.";
        }
        
        String[] parts = commandLine.split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        
        CommandExpression command = commands.get(commandName);
        if (command == null) {
            return "Unknown command: /" + commandName + ". Type /help for available commands.";
        }
        
        return command.execute(args);
    }
    
    public boolean isCommand(String message) {
        return message != null && message.trim().startsWith("/");
    }
}
