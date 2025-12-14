package server.achievements;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Composite that now also provides multiple iterator variants (DFS, BFS, filtered leaves).
 */
public class AchievementComposite implements AchievementComponent, Iterable<AchievementComponent> {
    private final String name;  // optional, for debugging
    private final List<AchievementComponent> children = new ArrayList<>();

    public AchievementComposite(String name) {
        this.name = name;
    }

    public void addChild(AchievementComponent child) {
        if (child != null) {
            children.add(child);
        }
    }

    /** Default iterator: depth-first (preorder) using a stack. */
    @Override
    public Iterator<AchievementComponent> iterator() {
        return new DepthFirstIterator(this);
    }

    /** Breadth-first traversal (queue-backed). */
    public Iterable<AchievementComponent> breadthFirst() {
        return new BreadthFirstIterable(this);
    }

    /**
     * Iterate only unlocked leaves (list-backed), useful for reporting UI without exposing internals.
     */
    public Iterable<AbstractAchievementLeaf> unlockedOnly() {
        return new UnlockedLeavesIterable(this);
    }

    List<AchievementComponent> getChildren() {
        return children;
    }

    @Override
    public void onEvent(AchievementEventType type, AchievementContext ctx) {
        for (AchievementComponent c : children) {
            c.onEvent(type, ctx);
        }
    }

    private static final class DepthFirstIterator implements Iterator<AchievementComponent> {
        private final Deque<AchievementComponent> stack = new ArrayDeque<>();

        DepthFirstIterator(AchievementComposite root) {
            stack.push(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public AchievementComponent next() {
            if (stack.isEmpty()) {
                throw new NoSuchElementException();
            }
            AchievementComponent current = stack.pop();
            if (current instanceof AchievementComposite) {
                List<AchievementComponent> kids = ((AchievementComposite) current).getChildren();
                for (int i = kids.size() - 1; i >= 0; i--) {
                    stack.push(kids.get(i));
                }
            }
            return current;
        }
    }

    private static final class BreadthFirstIterable implements Iterable<AchievementComponent> {
        private final AchievementComposite root;

        BreadthFirstIterable(AchievementComposite root) {
            this.root = root;
        }

        @Override
        public Iterator<AchievementComponent> iterator() {
            return new Iterator<AchievementComponent>() {
                private final Queue<AchievementComponent> queue = init();

                private Queue<AchievementComponent> init() {
                    Queue<AchievementComponent> q = new LinkedList<>();
                    q.add(root);
                    return q;
                }

                @Override
                public boolean hasNext() {
                    return !queue.isEmpty();
                }

                @Override
                public AchievementComponent next() {
                    if (queue.isEmpty()) {
                        throw new NoSuchElementException();
                    }
                    AchievementComponent current = queue.remove();
                    if (current instanceof AchievementComposite) {
                        queue.addAll(((AchievementComposite) current).getChildren());
                    }
                    return current;
                }
            };
        }
    }

    private static final class UnlockedLeavesIterable implements Iterable<AbstractAchievementLeaf> {
        private final AchievementComposite root;

        UnlockedLeavesIterable(AchievementComposite root) {
            this.root = root;
        }

        @Override
        public Iterator<AbstractAchievementLeaf> iterator() {
            return new Iterator<AbstractAchievementLeaf>() {
                private final Deque<AchievementComponent> stack = init();
                private AbstractAchievementLeaf nextLeaf = advance();

                private Deque<AchievementComponent> init() {
                    Deque<AchievementComponent> s = new ArrayDeque<>();
                    s.push(root);
                    return s;
                }

                private AbstractAchievementLeaf advance() {
                    while (!stack.isEmpty()) {
                        AchievementComponent current = stack.pop();
                        if (current instanceof AchievementComposite) {
                            List<AchievementComponent> kids = ((AchievementComposite) current).getChildren();
                            for (int i = kids.size() - 1; i >= 0; i--) {
                                stack.push(kids.get(i));
                            }
                        } else if (current instanceof AbstractAchievementLeaf) {
                            AbstractAchievementLeaf leaf = (AbstractAchievementLeaf) current;
                            if (leaf.unlocked()) {
                                return leaf;
                            }
                        }
                    }
                    return null;
                }

                @Override
                public boolean hasNext() {
                    return nextLeaf != null;
                }

                @Override
                public AbstractAchievementLeaf next() {
                    if (nextLeaf == null) {
                        throw new NoSuchElementException();
                    }
                    AbstractAchievementLeaf current = nextLeaf;
                    nextLeaf = advance();
                    return current;
                }
            };
        }
    }
}
