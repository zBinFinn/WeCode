package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.template.Template;

public class TemplateParser {
    private final Template template;
    public TemplateParser(Template template) {
        this.template = template;
    }
    public String parse() {
        StringBuilder result = new StringBuilder();

        result.append("SendMessage($\"Hello World!\")");

        return result.toString();
    }
}
