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

package io.github.projectdepbro.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

public class DepBroExtension implements Serializable {

    public static final String NAME = "depbro";
    private final Property<String> url;
    private final Deps deps;

    @Inject
    public DepBroExtension(ObjectFactory objects) {
        this.url = objects.property(String.class)
                .convention("http://localhost:3820");
        this.deps = objects.newInstance(Deps.class);
    }

    public static void create(Project project) {
        ExtensionContainer extensions = project.getExtensions();
        extensions.create(NAME, DepBroExtension.class);
    }

    public Property<String> getUrl() {
        return this.url;
    }

    @SuppressWarnings("unused")
    public void setUrl(String url) {
        this.url.set(url);
    }

    public Deps getDeps() {
        return this.deps;
    }

    @SuppressWarnings("unused")
    public void deps(Action<Deps> action) {
        action.execute(this.deps);
    }

    public static class Deps {

        private final ListProperty<String> includedGroupRegexes;

        @Inject
        public Deps(ObjectFactory objects) {
            this.includedGroupRegexes = objects.listProperty(String.class);
        }

        public ListProperty<String> getIncludedGroupRegexes() {
            return this.includedGroupRegexes;
        }

        @SuppressWarnings("unused")
        public void setIncludedGroupRegexes(List<String> includedGroupRegexes) {
            this.includedGroupRegexes.set(includedGroupRegexes);
        }

    }

}
