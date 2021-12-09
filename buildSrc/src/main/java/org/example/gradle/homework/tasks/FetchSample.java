package org.example.gradle.homework.tasks;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchSample extends DefaultTask {

    private String listUrl;
    private File sampleDirectory;
    private Map<String, String> sampleList;

    @Input
    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(String listUrl) {
        this.listUrl = listUrl;
    }

    @OutputDirectory
    public File getSampleDirectory() {
        return sampleDirectory;
    }

    public void setSampleDirectory(File sampleDirectory) {
        this.sampleDirectory = sampleDirectory;
    }

    @Input
    public Map<String, String> getSampleList() throws IOException {
        if (sampleList != null) {
            return sampleList;
        }

        getLogger().quiet("fetch samples list from " + getListUrl());

        OkHttpClient client = new OkHttpClient();
        final Response response = client.newCall(new Request.Builder().url(getListUrl()).build())
                .execute();
        if (response.code() != 200) {
            throw new IOException("failed to fetch sample list: " + response);
        }

        final String text = Objects.requireNonNull(response.body()).string();
        final LineNumberReader reader = new LineNumberReader(new StringReader(text));

        Map<String, String> urls = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] segment = line.split(",");
            if (segment.length != 2) {
                throw new IOException("illegal format line '" + line + "' at: " + reader.getLineNumber());
            }

            urls.put(segment[0], segment[1]);
        }

        getLogger().quiet("found " + urls.size() + " samples");
        sampleList = urls;
        return sampleList;
    }

    @TaskAction
    public void fetchAll() throws IOException {
        final File sampleDirectory = getSampleDirectory();
        assert sampleDirectory.exists() || sampleDirectory.mkdirs();

        getSampleList().forEach((name, url) -> {
            final File childDirectory = new File(getSampleDirectory(), name);

            try {
                cloneOrUpdateModule(url, childDirectory);
            } catch (IOException e) {
                throw new UncheckedIOException("io exception while executing git api", e);
            } catch (GitAPIException e) {
                throw new GradleException("failed in executing git api", e);
            }
        });
    }

    private void cloneOrUpdateModule(String url, File outputDirectory) throws IOException, GitAPIException {
        if (!outputDirectory.exists()) {
            getLogger().quiet("fetch " + outputDirectory.getName() + " from " + url);

            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(outputDirectory)
                    .call();
            return;
        }

        getLogger().quiet("update " + outputDirectory.getName() + " from " + url);
        Git.open(outputDirectory).pull().call();
    }
}
