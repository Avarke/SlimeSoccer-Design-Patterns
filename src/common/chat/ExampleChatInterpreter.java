package common.chat;

import server.Interpreter.CommandInterpreter;

public class ExampleChatInterpreter implements ChatInterpreter {
    private final CommandInterpreter commandInterpreter;
    
    public ExampleChatInterpreter() {
        this.commandInterpreter = new CommandInterpreter();
    }
    
    @Override
    public ChatMessage process(ChatMessage original) {
        String text = original.getText();
        
       
        if (commandInterpreter.isCommand(text)) {
            String result = commandInterpreter.interpret(text);
            if (result != null) {
                return new ChatMessage(
                    "System",
                    "",
                    ChatScope.GLOBAL,
                    result,
                    System.currentTimeMillis(), null
                );
            }
        }
        
        return original; 
    }
}
