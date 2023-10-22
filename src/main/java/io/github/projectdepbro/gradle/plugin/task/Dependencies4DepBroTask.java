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

import io.github.projectdepbro.gradle.plugin.DepBroExtension;
import io.github.projectdepbro.gradle.plugin.collector.DependencyCollector;
import io.github.projectdepbro.gradle.plugin.filter.DependencyFilter;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;

import javax.annotation.Nullable;
import java.util.List;
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
        DepBroExtension extension = project.getExtensions().getByType(DepBroExtension.class);
        DependencyFilter dependencyFilter = getDependencyFilter(extension);
        DependencyCollector dependencyCollector = new DependencyCollector(dependencyFilter);
        Set<String> dependencies = dependencyCollector.collectDependencies(project);
        System.out.println(String.join(System.lineSeparator(), dependencies));
    }

    @Nullable
    private DependencyFilter getDependencyFilter(DepBroExtension extension) {
        DependencyFilter dependencyFilter = null;
        List<String> groupRegexes = extension.getIncludedGroupRegexes().getOrNull();
        if (groupRegexes != null && !groupRegexes.isEmpty()) {
            if (groupRegexes.size() == 1) {
                dependencyFilter = DependencyFilter.ofGroupRegex(groupRegexes.get(0));
            } else {
                dependencyFilter = groupRegexes.stream()
                        .map(DependencyFilter::ofGroupRegex)
                        .reduce(DependencyFilter::and)
                        .orElseThrow(() -> new IllegalStateException("Number of filter must be > 1"));
            }
        }
        return dependencyFilter;
    }

}
