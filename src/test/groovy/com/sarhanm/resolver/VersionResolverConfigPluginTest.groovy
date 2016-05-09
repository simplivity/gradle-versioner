package com.sarhanm.resolver

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

/**
 *
 * @author mohammad sarhan
 */
class VersionResolverConfigPluginTest {

    @Test
    public void testPlugin()
    {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(JavaPlugin)
        project.plugins.apply(VersionResolutionConfigurationPlugin)
        assert project.versionResolver
        assert project.versionResolver.versionManifest

        // Enable this to test the activation is immediate now
        project.versionResolver.outputComputedManifest = true
        project.plugins.apply(VersionResolutionPlugin)
        assert project.outputVersionManifest
    }
}
