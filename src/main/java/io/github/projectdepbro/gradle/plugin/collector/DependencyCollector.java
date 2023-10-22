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

package io.github.projectdepbro.gradle.plugin.collector;

import io.github.projectdepbro.gradle.plugin.filter.ConfigurationFilter;
import io.github.projectdepbro.gradle.plugin.filter.DependencyFilter;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DependencyCollector {

    private final ConfigurationFilter configurationFilter;
    private final DependencyFilter dependencyFilter;

    public DependencyCollector(@Nullable DependencyFilter dependencyFilter) {
        this(null, dependencyFilter);
    }

    public DependencyCollector(
            @Nullable ConfigurationFilter configurationFilter,
            @Nullable DependencyFilter dependencyFilter
    ) {
        this.configurationFilter = configurationFilter;
        this.dependencyFilter = dependencyFilter;
    }

    public Set<String> collectDependencies(Project project) {
        ConfigurationContainer configurations = project.getConfigurations();
        Stream<Configuration> configurationStream = configurations.stream();
        if (configurationFilter != null) {
            configurationStream = configurationStream.filter(configurationFilter);
        }
        Stream<Dependency> dependencyStream = configurationStream
                .flatMap(configuration -> configuration.getDependencies().stream());
        if (dependencyFilter != null) {
            dependencyStream = dependencyStream.filter(dependencyFilter);
        }
        return dependencyStream.map(this::getInlineDependency).collect(Collectors.toSet());
    }

    private String getInlineDependency(Dependency dependency) {
        String version = dependency.getVersion();
        if (version != null && !version.isBlank()) {
            return dependency.getGroup() + ":" + dependency.getName() + ":" + version;
        } else {
            return dependency.getGroup() + ":" + dependency.getName();
        }
    }

}
