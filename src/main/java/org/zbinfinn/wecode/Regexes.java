package org.zbinfinn.wecode;

import org.intellij.lang.annotations.RegExp;

public class Regexes {
    @RegExp
    public static final String PLAYER_NAME = "[A-Za-z0-9_]{3,16}";
    public static final String HEX_COLOR = "#[0-9a-fA-F]{6}";
}
