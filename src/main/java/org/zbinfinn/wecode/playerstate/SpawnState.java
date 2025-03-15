package org.zbinfinn.wecode.playerstate;

public class SpawnState extends ModeState {
    private Node node;

    public SpawnState() {
        node = Node.UNKNOWN;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        if (node == null) {
            this.node = Node.UNKNOWN;
            return;
        }
        this.node = node;
    }
}
