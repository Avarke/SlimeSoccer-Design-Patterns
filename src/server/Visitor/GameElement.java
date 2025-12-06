package server.Visitor;


public interface GameElement {
    void accept(GameElementVisitor visitor);
}
