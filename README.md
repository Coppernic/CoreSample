# CoreSample

Sample for CpcCore use

## Prerequisites

App **CpcSystemServices** at version 2.1.0 or above needs to be installed on device. Please contact Coppernic support team in case of difficulties.

## Build

**build.gradle**

```groovy
repositories {
        //[...]
        maven { url "https://artifactory.coppernic.fr/artifactory/libs-release" }
    }
```

```groovy
dependencies {
    implementation(group: 'fr.coppernic.sdk.cpcutils', name: 'CpcUtilsLib', version: '6.17.0', ext: 'aar')
    implementation(group: 'fr.coppernic.sdk.core', name: 'CpcCore', version: '1.6.1', ext: 'aar') {
        transitive = true
    }
}
```

 * Last versions of libs can be found in [artifactory](https://artifactory.coppernic.fr/artifactory/webapp/#/home)
 * `implementation` is a key work of android gradle plugin 3.0.0, if you are using a older plugin, consider using `compile` instead

## More documentation

[developer.coppernic.fr](https://developer.coppernic.fr)

