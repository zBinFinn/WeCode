package org.zbinfinn.wecode.templates;

import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.zbinfinn.wecode.helpers.MessageHelper;

public class CodeBlock {
    private String id;
    private String block;
    private String action;
    private String target;
    private String subAction;
    private String attribute;

    public CodeBlock(String id, String block, String action, String target, String subAction, String attribute) {
        this.id = id;
        this.block = block;
        this.action = action;
        this.target = target;
        this.subAction = subAction;
        this.attribute = attribute;
    }

    public static CodeBlock fromJSON(JsonObject json) {
        //MessageHelper.debug(json.toString());

        String newID = "ERROR: Lack of ID";
        String newBlock = "ERROR: Lack of Block";
        String newAction = "none";
        String newTarget = "none";
        String newSubAction = "none";
        String newAttribute = "none";

        newID = json.get("id").getAsString();
        if (json.has("block")) {
            newBlock = json.get("block").getAsString();
        } else if (json.has("direct")) {
            newBlock = json.get("direct").getAsString() + "_" + json.get("type").getAsString();
        }

        if (json.has("action")) {
            newAction = json.get("action").getAsString();
        } else {
            if (json.has("data")) {
                newAction = json.get("data").getAsString();
            }
        }

        if (json.has("target")) {
            newTarget = json.get("target").getAsString();
        }

        if (json.has("subAction")) {
            newSubAction = json.get("subAction").getAsString();
        }

        if (json.has("attribute")) {
            newAttribute = json.get("attribute").getAsString();
        }

        if (json.has("inverted")) {
            newAttribute = json.get("inverted").getAsString();
        }

        return new CodeBlock(newID, newBlock, newAction, newTarget, newSubAction, newAttribute);
    }

    public String getBlock() {
        return block;
    }

    public String getAction() {
        return action;
    }

    public String getDisplayName() {
        return switch (block) {
            case "event" -> "PLAYER EVENT";
            case "set_var" -> "SET VARIABLE";
            case "if_var" -> "IF VARIABLE";
            case "select_obj" -> "SELECT OBJECT";
            case "call_func" -> "CALL FUNCTION";
            case "func" -> "FUNCTION";
            default -> block.replace('_', ' ').toUpperCase();
        };
    }

    public String getSignLineOne() {
        return getDisplayName();
    }

    public String getSignLineTwo() {
        return getAction();
    }

    public String getSignLineThree() {
        if (!target.equals("none")) {
            return target;
        }
        if (!subAction.equals("none")) {
            return subAction;
        }
        return "";
    }

    public String getSignLineFour() {
        if (!attribute.equals("none")) {
            return attribute;
        }
        return "";
    }
}
