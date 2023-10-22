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

import io.github.projectdepbro.gradle.plugin.collector.DependencyCollector;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;

import java.util.Set;

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
        DependencyCollector dependencyCollector = new DependencyCollector(project);
        Set<String> dependencies = dependencyCollector.collectDependencies(project);
        System.out.println(String.join(System.lineSeparator(), dependencies));
    }

}
