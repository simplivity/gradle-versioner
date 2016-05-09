package com.sarhanm.resolver

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class VersionResolutionPluginBase {

    public static final String VERSION_RESOLVER = "versionResolver"

    /**
     * Create the extensions for configuration metadata.
     * Create and return the configuration for version manifest.
     *
     * @param project The Gradle Project this is applied to.
     * @return the created Configuration reference.
     */
    Configuration createPluginExtension(Project project) {
        project.extensions.create(VERSION_RESOLVER, VersionResolverOptions)
        project."$VERSION_RESOLVER".extensions.create("versionManifest", VersionManifestOption)
        project.configurations.maybeCreate('versionManifest')
    }

    /**
     * Configure the VersionResolver for this project. May be called in an afterEvaluate
     * listener depending on which usage is chosen.
     *
     * @param project The Gradle Project this is applied to.
     * @param versionManifest The version manifest configuration reference.
     */
    void configureDependencyResolver(Project project, Configuration versionManifest) {
        def params = project."$VERSION_RESOLVER"
        params.manifest = params.versionManifest

        def resolved = versionManifest.resolve()

        def file = null

        if (resolved && !resolved.empty){
            file = resolved.first()
        }

        def resolver = new VersionResolver(project, params, file)
        project.configurations.all {
            // Avoid modifying already resolved configurations.
            // This will fail the build in Gradle v3+.
            if (state == Configuration.State.UNRESOLVED)
                resolutionStrategy.eachDependency(resolver)
        }

        // Output the computed version manifest
        if (params.outputComputedManifest)
        {
            def task = project.tasks.create(name: 'outputVersionManifest',
                    description: 'Outputs the version manifest of all the resolved versions.',
                    type: VersionManifestOutputTask) {
                outputFile  = project.file("$project.buildDir/version-manifest.yaml")
                versionResolver = resolver
            }

            project.tasks.build.dependsOn task
        }
    }
}
