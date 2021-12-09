package org.example.gradle.homework;

import org.jetbrains.annotations.NotNull;

public class DefaultHomeWorkExtension implements HomeWorkExtension {
    private static final String DEFAULT_LIST_URL = "https://raw.githubusercontent.com/noproxy/Samples/main/samples.csv";
    public static final String DEFAULT_SUBMIT_TO = "yiyazhou@apusapps.com";

    private String listUrl = DEFAULT_LIST_URL;
    private String submitTo = DEFAULT_SUBMIT_TO;

    @NotNull
    public String getSubmitTo() {
        return submitTo;
    }

    public void setSubmitTo(String submitTo) {
        this.submitTo = submitTo;
    }

    @NotNull
    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(@NotNull String listUrl) {
        this.listUrl = listUrl;
    }
}
