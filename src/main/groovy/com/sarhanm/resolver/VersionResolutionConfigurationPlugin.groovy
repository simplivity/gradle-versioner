package com.sarhanm.resolver

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 *
 * @author mohammad sarhan
 */
class VersionResolutionConfigurationPlugin extends VersionResolutionPluginBase implements Plugin<Project> {

    @Override
    void apply(Project project) {
        createPluginExtension(project)
    }
}
