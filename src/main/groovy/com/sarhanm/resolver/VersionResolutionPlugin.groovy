package com.sarhanm.resolver

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 *
 * @author mohammad sarhan
 */
class VersionResolutionPlugin extends VersionResolutionPluginBase implements Plugin<Project> {

    @Override
    void apply(Project project) {
        Configuration versionManifest

        if (project.pluginManager.hasPlugin('com.sarhanm.version-resolver-config')) {
            versionManifest = project.configurations.versionManifest
            configureDependencyResolver(project, versionManifest)
        } else {
            versionManifest = createPluginExtension(project)
            project.afterEvaluate {
                configureDependencyResolver(project, versionManifest)
            }
        }
    }
}
