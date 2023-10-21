/*
 * Copyright 2023 The Project DepBro Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.projectdepbro.gradle.plugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;

public class Dependencies4DepBroTask extends DefaultTask {

    public static final String NAME = "deps4Depbro";

    public Dependencies4DepBroTask() {
        setGroup(DepBroTasks.GROUP);
        setDescription("Displays dependencies for DepBro.");
    }

    public static void register(Project project) {
        TaskContainer tasks = project.getTasks();
        tasks.register(NAME, Dependencies4DepBroTask.class);
    }

    @TaskAction
    public void display() {
        Project project = getProject();
        ConfigurationContainer configurations = project.getConfigurations();
        configurations.configureEach(this::displayConfiguration);
    }

    private void displayConfiguration(Configuration configuration) {
        String configurationName = configuration.getName();
        DependencySet dependencies = configuration.getDependencies();
        if (dependencies.isEmpty()) return;
        int numberOfDependencies = dependencies.size();
        System.out.println("Configuration '" + configurationName +
                           "' has " + numberOfDependencies +
                           " " + (numberOfDependencies > 1 ? "dependencies" : "dependency") +
                           ":");
        dependencies.configureEach(this::displayDependency);
    }

    private void displayDependency(Dependency dependency) {
        String inlineDependency;
        String version = dependency.getVersion();
        if (version != null && !version.isBlank()) {
            inlineDependency = dependency.getGroup() + ":" + dependency.getName() + ":" + version;
        } else {
            inlineDependency = dependency.getGroup() + ":" + dependency.getName();
        }
        System.out.println(inlineDependency);
    }

}
