package server.Interpreter;


public class HelpCommand implements CommandExpression {
    
    @Override
    public String execute(String[] args) {
        return "=== SLIME SOCCER HELP ===\n" +
               "Controls:\n" +
               "  Player 1 (Green): W/A/D - Jump/Left/Right\n" +
               "  Player 2 (Cyan): T/F/H - Jump/Left/Right\n" +
               "  Player 3 (Red): I/J/L - Jump/Left/Right\n" +
               "  Player 4 (Orange): Arrow Keys - Up/Left/Right\n" +
               "Commands:\n" +
               "  /help - Show this help\n" +
               "  /setbrightness <0-100> - Adjust brightness\n" +
               "  /reset - Reset game\n" +
               "  /score - Show current score\n" +
               "  /report - Generate game statistics report\n" +
               "  /setscore <team1> <team2> - Set scores";
    }
    
    @Override
    public String getCommandName() {
        return "help";
    }
}
