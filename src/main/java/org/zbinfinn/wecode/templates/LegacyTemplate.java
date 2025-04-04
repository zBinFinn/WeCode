package org.zbinfinn.wecode.templates;

import java.util.ArrayList;
import java.util.List;

public class LegacyTemplate {
    private List<CodeBlock> blocks;
    private String author;
    private String name;
    private String version;

    public LegacyTemplate(ArrayList<CodeBlock> blocks, String author, String name, String version) {
        this.blocks = blocks;
        this.author = author;
        this.name = name;
        this.version = version;
    }

    public List<CodeBlock> getCodeBlocks() {
        return blocks;
    }
}
