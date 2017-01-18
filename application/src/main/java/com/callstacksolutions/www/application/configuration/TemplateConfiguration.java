package com.callstacksolutions.www.application.configuration;

import com.callstacksolutions.www.domain.Template;

public class TemplateConfiguration {

    private String content = "Hello";
    private String defaultName = "Stranger";

    public Template buildTemplate() {
        return new Template(content, defaultName);
    }

    public String getContent() {
        return content;
    }

    public String getDefaultName() {
        return defaultName;
    }
}
