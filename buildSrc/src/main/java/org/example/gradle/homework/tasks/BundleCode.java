package org.example.gradle.homework.tasks;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.BundleWriter;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BundleCode extends DefaultTask {
    @Input
    public Map<String, String> getCommitGitRevision() throws IOException {
        return getBundleRefs().stream()
                .collect(Collectors.toMap(Ref::getName, ref -> ref.getObjectId().getName()));
    }

    private Git git() throws IOException {
        return Git.open(getProject().getRootDir());
    }

    private List<Ref> getBundleRefs() throws IOException {
        return git().getRepository().getAllRefs().values()
                .stream()
                .filter(ref -> !ref.getName().equals(Constants.HEAD)).collect(Collectors.toList());
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return new File(getProject().getBuildDir(), "outputs/homework");
    }

    @Internal
    public File getOutputFile() {
        return new File(getOutputDirectory(), "homework.bundle");
    }

    @TaskAction
    public void bundle() throws IOException {
        final Repository repository = git().getRepository();
        final BundleWriter bundleWriter = new BundleWriter(git().getRepository());

        getBundleRefs().forEach(ref -> {
            if (ref.getName().equals(Constants.HEAD)) {
                return;
            }
            getLogger().quiet("bundle " + ref.getName());
            bundleWriter.include(ref);
        });

        try (final FileOutputStream os = new FileOutputStream(getOutputFile())) {
            bundleWriter.writeBundle(new TextProgressMonitor(), os);
        }

        if (Files.size(getOutputFile().toPath()) > 45 * 1024 * 1024) {
            throw new IllegalArgumentException("代码体积过大，请联系开发者处理");
        }
    }
}
