package org.example.gradle.homework;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class HomeWorkPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        final PropertyOptions propertyOptions = new PropertyOptions(project);
        propertyOptions.ensurePropertiesCreated();

        final DefaultHomeWorkExtension homework = (DefaultHomeWorkExtension) project.getExtensions()
                .create(HomeWorkExtension.class, "homework", DefaultHomeWorkExtension.class);

        final TaskManager taskManager = new TaskManager(project, homework, propertyOptions);
        taskManager.createTasks();
    }
}
