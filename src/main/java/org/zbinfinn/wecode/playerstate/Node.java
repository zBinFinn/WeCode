package org.zbinfinn.wecode.playerstate;

public enum Node {
    NODE_1("node1", "Node 1"),
    NODE_2("node2", "Node 2"),
    NODE_3("node3", "Node 3"),
    NODE_4("node4", "Node 4"),
    NODE_5("node5", "Node 5"),
    NODE_6("node6", "Node 6"),
    NODE_7("node7", "Node 7"),

    BETA("beta", "Node Beta"),
    DEV_1("dev", "Dev"),
    DEV_2("dev2", "Dev 2"),
    DEV_3("dev3", "Dev 3"),

    UNKNOWN("unknown", "Unknown");

    public final String id;
    public final String displayName;
    Node (String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static Node getFromDisplayString(String node) {
        return switch (node) {
            case "Node 1" -> NODE_1;
            case "Node 2" -> NODE_2;
            case "Node 3" -> NODE_3;
            case "Node 4" -> NODE_4;
            case "Node 5" -> NODE_5;
            case "Node 6" -> NODE_6;
            case "Node 7" -> NODE_7;
            case "Node Beta" -> BETA;
            case "Dev" -> DEV_1;
            case "Dev 2" -> DEV_2;
            case "Dev 3" -> DEV_3;
            default -> UNKNOWN;
        };
    }
}
