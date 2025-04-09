package org.zbinfinn.wecode.template_editor.refactor;

public abstract class Reader<T> {
    protected int index;

    protected abstract T getElementAt(int index);
    protected abstract boolean hasIndex(int index);

    protected void reset() {
        index = 0;
    }
    protected boolean canPeek(int ahead) {
        return hasIndex(index + ahead);
    }
    protected T peek(int ahead) {
        return getElementAt(index + ahead);
    }

    protected T consume() {
        return getElementAt(index++);
    }

    protected void consume(int amount) {
        for (int i = 0; i < amount; i++) {
            consume();
        }
    }

    protected T peek() {
        return peek(0);
    }
    protected boolean canPeek() {
        return canPeek(0);
    }

    protected boolean sequence(T[] sequence) {
        for (int index = 0; index < sequence.length; index++) {
            if (!canPeek(index)) {
                return false;
            }
            T element = sequence[index];
            if (!peek(index).equals(element)) {
                return false;
            }
        }
        return true;
    }
}
