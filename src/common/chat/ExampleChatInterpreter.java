package common.chat;

public class ExampleChatInterpreter implements ChatInterpreter {
    @Override
    public ChatMessage process(ChatMessage original) {
        return original; // no changes yet
    }
}
