package server.Interpreter;


public interface CommandExpression {
    
    String execute(String[] args);
    String getCommandName();
}
