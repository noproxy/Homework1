package org.example.gradle.homework;

import org.example.gradle.homework.tasks.BundleCode;
import org.example.gradle.homework.tasks.FetchSample;
import org.example.gradle.homework.tasks.Submit;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

public class TaskManager {
    private static final String TASK_GROUP_HOMEWORK = "homework";
    private final Project project;
    private final DefaultHomeWorkExtension homework;
    private final PropertyOptions propertyOptions;

    public TaskManager(@NotNull Project project, @NotNull DefaultHomeWorkExtension homework, @NotNull PropertyOptions propertyOptions) {
        this.project = project;
        this.homework = homework;
        this.propertyOptions = propertyOptions;
    }

    public void createTasks() {
        final TaskProvider<BundleCode> bundleHomework = project.getTasks().register("bundleHomework", BundleCode.class, task -> {
            task.setGroup(TASK_GROUP_HOMEWORK);
        });

        project.getTasks().register("submitHomework", Submit.class, task -> {
            task.setGroup(TASK_GROUP_HOMEWORK);
            task.setTo(homework.getSubmitTo());

            task.getUsername().set(project.provider(propertyOptions::getUsername));
            task.getEmail().set(project.provider(propertyOptions::getEmail));
            task.getPassword().set(project.provider(propertyOptions::getPassword));

            task.dependsOn(bundleHomework);
            task.getAttachment().set(bundleHomework.get().getOutputFile());
        });

        project.getTasks().register("fetchLatestSample", FetchSample.class, fetchingTask -> {
            fetchingTask.setListUrl(homework.getListUrl());
            fetchingTask.setGroup(TASK_GROUP_HOMEWORK);
            fetchingTask.setSampleDirectory(project.getRootProject().file("samples"));
        });
    }

}
