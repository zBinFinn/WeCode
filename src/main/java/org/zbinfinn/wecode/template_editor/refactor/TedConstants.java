package org.zbinfinn.wecode.template_editor.refactor;

import org.spongepowered.include.com.google.common.collect.HashBiMap;

public class TedConstants {
    public static final int TAB_SPACES = 3;

    public static final double EDITOR_TAB_WIDTH = 0.1d;
    public static final double EDITOR_TAB_HEIGHT = 0.05d;

    public static final double LINE_STARTER_HEIGHT = 0.05d;
    public static final double LINE_STARTER_WIDTH = 0.1d;

    public static final double EDITOR_X = 0.1d;
    public static final double EDITOR_Y = 0.1d;
    public static final double EDITOR_WIDTH = 0.8d;
    public static final double EDITOR_HEIGHT = 0.8d;

    public static final HashBiMap<String, String> ACTION_SPECIFIERS;

    static {
        ACTION_SPECIFIERS = HashBiMap.create();
        ACTION_SPECIFIERS.put("PE", "PLAYER EVENT"); // Player Event
        ACTION_SPECIFIERS.put("PA", "PLAYER ACTION"); // Player Action
        ACTION_SPECIFIERS.put("IP", "IF PLAYER"); // If Player

        ACTION_SPECIFIERS.put("EE", "ENTITY EVENT"); // Entity Event
        ACTION_SPECIFIERS.put("EA", "ENTITY ACTION"); // Entity Action
        ACTION_SPECIFIERS.put("IE", "IF ENTITY"); // If Entity

        ACTION_SPECIFIERS.put("SV", "SET VARIABLE"); // Set Variable
        ACTION_SPECIFIERS.put("IV", "IF VARIABLE"); // If Variable

        ACTION_SPECIFIERS.put("GA", "GAME ACTION"); // Game Action
        ACTION_SPECIFIERS.put("IG", "IF GAME"); // If Game

        ACTION_SPECIFIERS.put("SO", "SELECT OBJECT"); // Select Object

        // Else Doesn't Have One it's just "Else"

        ACTION_SPECIFIERS.put("FN", "FUNCTION"); // Function (Always needs to be specified)
        ACTION_SPECIFIERS.put("CF", "CALL FUNCTION"); // Call Function (Always needs to be specified)

        ACTION_SPECIFIERS.put("PC", "PROCESS"); // Process (Always needs to be specified)
        ACTION_SPECIFIERS.put("SP", "START PROCESS"); // Start Process (Always needs to be specified)

        ACTION_SPECIFIERS.put("CT", "CONTROL"); // Control

        ACTION_SPECIFIERS.put("RP", "REPEAT"); // Repeat
    }
}
