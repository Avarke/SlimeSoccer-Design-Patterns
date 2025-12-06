package server.Interpreter;


public class ResetCommand implements CommandExpression {
    private static boolean resetRequested = false;
    
    public static boolean isResetRequested() {
        boolean requested = resetRequested;
        resetRequested = false; 
        return requested;
    }
    
    @Override
    public String execute(String[] args) {
        resetRequested = true;
        return "Game reset requested. The game will reset shortly.";
    }
    
    @Override
    public String getCommandName() {
        return "reset";
    }
}
