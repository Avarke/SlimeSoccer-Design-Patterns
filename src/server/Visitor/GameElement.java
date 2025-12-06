package server.Visitor;

/**
 * Interface for game elements that can be visited.
 */
public interface GameElement {
    void accept(GameElementVisitor visitor);
}
