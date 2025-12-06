package server.Interpreter;


public class SetBrightnessCommand implements CommandExpression {
    private static float currentBrightness = 100f;
    
    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            return "Current brightness: " + (int)currentBrightness + "%. Usage: /setbrightness <0-100>";
        }
        
        try {
            int brightness = Integer.parseInt(args[0]);
            if (brightness < 0 || brightness > 100) {
                return "Error: Brightness must be between 0 and 100.";
            }
            currentBrightness = brightness;
            return "Brightness set to " + brightness + "%";
        } catch (NumberFormatException e) {
            return "Error: Invalid brightness value. Must be a number between 0 and 100.";
        }
    }
    
    @Override
    public String getCommandName() {
        return "setbrightness";
    }
    
    public static float getCurrentBrightness() {
        return currentBrightness / 100f;
    }
}
