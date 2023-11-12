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
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

public class RegistrationDependencies4DepBroTask extends DefaultTask {

    public static final String NAME = "registerDeps4Depbro";

    public RegistrationDependencies4DepBroTask() {
        setGroup(DepBroTasks.GROUP);
        setDescription("Registers dependencies for DepBro.");
    }

    public static void register(Project project) {
        TaskContainer tasks = project.getTasks();
        tasks.register(NAME, RegistrationDependencies4DepBroTask.class);
    }

    @TaskAction
    public void register() {
        Project project = getProject();
        DependencyCollector dependencyCollector = new DependencyCollector(project);
        Set<String> dependencies = dependencyCollector.collectDependencies(project);
        URI uri = getUri();
        String json = getJson(dependencies);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Dependencies registered successfully");
            } else {
                System.out.println("Error sending dependencies - code " + response.statusCode() + ": " + response.body());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Exception of sending dependencies to DepBro", e);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Sending dependencies to DepBro was interrupted", e);
        }
    }

    private URI getUri() {
        Project project = getProject();
        DepBroExtension extension = project.getExtensions().getByType(DepBroExtension.class);
        String depbroUrl = extension.getUrl().get();
        String group = project.getGroup().toString();
        String name = project.getName();
        String version = project.getVersion().toString();
        String url = depbroUrl + "/api/" +
                     "groups/" + group +
                     "artifacts/" + name +
                     "versions/" + version;
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Exception of creating URI for sending to DepBro: '" + url + "'", e);
        }
    }

    private String getJson(Set<String> dependencies) {
        // ["one","two"]
        return dependencies.stream().collect(Collectors.joining("\",\"", "[\"", "\"]"));
    }

}
