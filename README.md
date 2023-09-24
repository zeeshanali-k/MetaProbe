[![Maven Central](https://img.shields.io/maven-central/v/tech.dev-scion/metaprobe.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22tech.dev-scion%22%20AND%20a:%22metaprobe%22)

# MetaProbe
A simple Android Kotlin library for fetching link details like Title, Description, Icon and OG Image effortlessly based on <a href="https://ktor.io/docs/">Ktor</a> for fetching HTML from provided URL and <a href="https://github.com/jhy/jsoup">Jsoup</a> for parsing Meta Data. The usecase of this library could be seen in Social Media and Messaging Apps. 

For Kotlin Multiplatform Check <a href="https://github.com/zeeshanali-k/MetaProbeKMP">MetaProbeKMP</a>

<img src="/imgs/ss_1.png" height="300px">


## Usage

A sample usage is also provided on code along with the design most social media apps use.

Add following dependency to your module level gradle file and make sure `mavenCentral()` is added in repositories in your `settings.gradle` file.

For `build.gradle.kts`:

```kotlin
implementation("tech.dev-scion:metaprobe:1.0.3")
```

For `build.gradle`:

```groovy
implementation "tech.dev-scion:metaprobe:1.0.3"
```

You can use this library in Coroutines or via Callback method (without managing any Coroutine or Thread).

With Coroutine Suspend Method:

```kotlin
MetaProbe(url)
            .apply {
                setClient(
                    HttpClient(Android) {
                        engine {
                            connectTimeout = 100_000
                            socketTimeout = 100_000
                        }
                    }
                )
            }
            .probeLink()
            .fold(
                onSuccess = {
                    isLoading.value = false
                    probedData.value = it
                },
                onFailure = {
                    isLoading.value = false
                },
            )
```
Fold here is just an Convinience Extension Method of Kotlin `Result` Class, so you can also use MetaProbe without it.
For Callback Method Use this code:

```kotlin
MetaProbe(url)
  .probeLink(object : OnMetaDataProbed {
    override fun onMetaDataProbed(pb: Result<ProbedData>) {
        isLoading.value = false
        Log.d(TAG, "onMetaDataProbed: $pb")
        Log.d(TAG, "onMetaDataProbed: ${pb.getOrNull()?.title}")
        Log.d(TAG, "onMetaDataProbed: ${pb.getOrNull()?.icon}")
        Log.d(TAG,"onMetaDataProbed: ${pb.getOrNull()?.description}")
        probedData.value = pb.getOrNull()
      }
  })
```

You can remove `setClient` as it is not necessary and only needed when you want to cutomise your HTTPClient, for example for logging etc.

`ProbedData` is the class returned in result which contains the actual values of Title, Description, Icon and OGImage.


<a href="https://www.buymeacoffee.com/devscion"><img src="https://img.buymeacoffee.com/button-api/?text=Buy me a coffee&emoji=&slug=ZeeshanAli&button_colour=FFDD00&font_colour=000000&font_family=Cookie&outline_colour=000000&coffee_colour=ffffff"></a>

