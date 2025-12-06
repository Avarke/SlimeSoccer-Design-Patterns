package common.chat;

public interface ChatInterpreter {
    /**
     Add your pattern here
     */
    ChatMessage process(ChatMessage original);
}