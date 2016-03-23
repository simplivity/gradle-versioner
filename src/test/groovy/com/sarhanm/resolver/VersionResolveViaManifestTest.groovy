package com.sarhanm.resolver

import groovy.mock.interceptor.MockFor
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ModuleVersionSelector
import org.junit.Test

/**
 *
 * @author mohammad sarhan
 */
class VersionResolveViaManifestTest {

    @Test
    void testManifestVersion() {
        def file = new File("src/test/resources/versions.yaml")

        def options = getOption(file.toURI().toString())

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion(1) { params -> 'auto' }
        selectorMock.demand.getGroup { params -> 'com.coinfling' }
        selectorMock.demand.getName { params -> 'auth-service-api' }

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested(1) { params -> selectorMock.proxyInstance() }

        def details = detailsMock.proxyInstance()
        def resolver = new VersionResolver(null, options)
        def ver = resolver.resolveVersionFromManifest(details)
        assert ver == "1.0-SNAPSHOT"

    }

    @Test
    void testManifestVersionFromFile() {
        def file = new File("src/test/resources/versions.yaml")

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion(1) { params -> 'auto' }
        selectorMock.demand.getGroup { params -> 'com.coinfling' }
        selectorMock.demand.getName { params -> 'auth-service-api' }

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested(1) { params -> selectorMock.proxyInstance() }

        def details = detailsMock.proxyInstance()
        def resolver = new VersionResolver(null, null,file)
        def ver = resolver.resolveVersionFromManifest(details)
        assert ver == "1.0-SNAPSHOT"

    }

    @Test
    void testManifestVersionUsingGroups() {
        def file = new File("src/test/resources/versions-using-groups.yaml")

        def apiSelectorMock = new MockFor(ModuleVersionSelector)
        apiSelectorMock.demand.getVersion(1) { params -> 'auto' }
        apiSelectorMock.demand.getGroup { params -> 'com.coinfling' }
        apiSelectorMock.demand.getName { params -> 'auth-service-api' }

        def apiDetailsMock = new MockFor(DependencyResolveDetails)
        apiDetailsMock.demand.getRequested(1) { params -> apiSelectorMock.proxyInstance() }

        def implSelectorMock = new MockFor(ModuleVersionSelector)
        implSelectorMock.demand.getVersion(1) { params -> 'auto' }
        implSelectorMock.demand.getGroup { params -> 'com.coinfling' }
        implSelectorMock.demand.getName { params -> 'auth-service-impl' }

        def implDetailsMock = new MockFor(DependencyResolveDetails)
        implDetailsMock.demand.getRequested(1) { params -> implSelectorMock.proxyInstance() }

        def resolver = new VersionResolver(null, null,file)
        def ver = resolver.resolveVersionFromManifest(apiDetailsMock.proxyInstance())
        assert ver == "1.0-SNAPSHOT"
        ver = resolver.resolveVersionFromManifest(implDetailsMock.proxyInstance())
        assert ver == "1.0-SNAPSHOT"

    }


    @Test
    void testManifestVersionModuleWinsOverGroup() {
        def file = new File("src/test/resources/versions-with-both.yaml")

        def apiSelectorMock = new MockFor(ModuleVersionSelector)
        apiSelectorMock.demand.getVersion(1) { params -> 'auto' }
        apiSelectorMock.demand.getGroup { params -> 'com.coinfling' }
        apiSelectorMock.demand.getName { params -> 'auth-service-api' }

        def apiDetailsMock = new MockFor(DependencyResolveDetails)
        apiDetailsMock.demand.getRequested(1) { params -> apiSelectorMock.proxyInstance() }

        def implSelectorMock = new MockFor(ModuleVersionSelector)
        implSelectorMock.demand.getVersion(1) { params -> 'auto' }
        implSelectorMock.demand.getGroup { params -> 'com.coinfling' }
        implSelectorMock.demand.getName { params -> 'auth-service-impl' }

        def implDetailsMock = new MockFor(DependencyResolveDetails)
        implDetailsMock.demand.getRequested(1) { params -> implSelectorMock.proxyInstance() }

        def resolver = new VersionResolver(null, null,file)
        def ver = resolver.resolveVersionFromManifest(apiDetailsMock.proxyInstance())
        assert ver == "1.0-SNAPSHOT"
        ver = resolver.resolveVersionFromManifest(implDetailsMock.proxyInstance())
        assert ver == "2.1"

    }

    @Test
    void testManifestVersionMissing()
    {
        def file = new File("src/test/resources/versions.yaml")

        def options = getOption(file.toURI().toString())

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion{ params-> 'auto'}
        selectorMock.demand.getGroup{ params-> 'com.coinfling'}
        selectorMock.demand.getName{ params-> 'not-there'}

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested{params-> selectorMock.proxyInstance()}

        def resolver = new VersionResolver(null, options)
        def ver = resolver.resolveVersionFromManifest(detailsMock.proxyInstance())
        assert ver == "auto"
    }

    @Test
    void testNoExecution()
    {
        def file = new File("src/test/resources/versions.yaml")

        def options = getOption(file.toURI().toString())

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion{ params-> '1.2.3'}
        selectorMock.demand.getGroup{ params -> 'com.coinfling'}
        selectorMock.demand.getName{ params -> 'foobar'}

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested{params-> selectorMock.proxyInstance()}

        def resolver = new VersionResolver(null, options)
        def ver = resolver.resolveVersionFromManifest(detailsMock.proxyInstance())
        assert ver == "1.2.3"

    }

    @Test
    void testForceManifestVersions()
    {
        def file = new File("src/test/resources/versions.yaml")

        def options = getOption(file.toURI().toString())
        options.forceManifestVersions = true

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion{ params-> '1.2.3'}
        selectorMock.demand.getGroup{ params -> 'com.coinfling'}
        selectorMock.demand.getName{ params -> 'auth-service-impl'}

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested{params-> selectorMock.proxyInstance()}

        def resolver = new VersionResolver(null, options)
        def ver = resolver.resolveVersionFromManifest(detailsMock.proxyInstance())
        assert ver == "1.0-SNAPSHOT"

    }

    //@Test
    void testRemoteLocation() {

        def options = getOption("https://repo.coinfling.com/service/local/artifact/maven/redirect?r=public&g=com.coinfling&a=version-manifest&v=2.0.master-SNAPSHOT&e=yaml&c=yaml",
                                "nexusread", "fulus777")

        options.manifest.ignoreSSL = true

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion(1) { params -> 'auto' }
        selectorMock.demand.getGroup { params -> 'org.hibernate' }
        selectorMock.demand.getName { params -> 'hibernate-core' }

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested(1) { params -> selectorMock.proxyInstance() }

        def details = detailsMock.proxyInstance()
        def resolver = new VersionResolver(null,options)
        def ver = resolver.resolveVersionFromManifest(details)
        assert ver == "4.3.5.Final"

    }

    private VersionResolverOptions getOption(def url, def username = null, def password = null)
    {
        def options = new VersionResolverOptions()
        options.manifest = [ url: url, username: username, password: password] as VersionManifestOption
        options
    }
}
