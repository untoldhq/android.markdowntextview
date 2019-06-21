# MarkdownTextView
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![LICENSE](https://img.shields.io/badge/LICENSE-MIT-brightgreen.svg?style=flat)](https://github.com/untoldhq/android.markdowntextview/blob/master/LICENSE)
[![stability-experimental](https://img.shields.io/badge/stability-experimental-orange.svg?style=flat)](https://github.com/untoldhq/android.markdowntextview)

![screenshot](https://raw.githubusercontent.com/untoldhq/android.markdowntextview/master/docs/images/Screenshot_1561155122.png)


This specialized [TextView][textview] can process markdown features introduced by a set of regular expression match and replace strategies. The two strategies are:

- Match and replace on match.
- Match and postpone replace with a unique replacement token at the end of set.

Regular expression replacements are defined in a common interface which allow translations from mark down rules to [android.text.Spanned][spanned]. This must be done with a good understanding of when matches are replaced, what the string becomes after a match and how following regex expressions will be effected by previous regex expressions.

There is also a bit of fiddly behavior in some of the background rendering calculation. I think a lot can be removed or redone to more clearly describe how xml-based drawables are getting placed in relation to the provided horizontal/vertical units. Or begin introducing more customizable dimen measurement attributes. Ideally implement a good way to make measurements relative to the current line height to keep configuration simple.

# Installation

Add Jitpack to your project `build.gradle` file

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Then add this dependency to your module `build.gradle` file.

```gradle
dependencies {
    implementation 'com.github.untoldhq:android.markdowntextview:latest-release'
}
```

# Usage

You should now have `studio.untold.MarkdownTextView` available to use in your module.

## View in XML
```xml
<studio.untold.MarkdownTextView
    style="@style/SampleMarkdownTheme"
    android:id="@+id/markdowntextview"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingEnd="@dimen/horizontal"
    android:paddingStart="@dimen/horizontal"
    android:paddingBottom="@dimen/vertical"
    android:paddingTop="@dimen/vertical"
    tools:text="Hello World!" />
```

## Kotlin
```kotlin
val view = findViewById<MarkdownTextView>(R.id.markdowntextview)
val string = "~strike *bold /italic _underline_/*~"
view.setMarkdownText(string)
view.setMarkdownTextAsync(uniqueIdForCache, string)
val current = view.currentMarkdownText // this == string
```

## XML Styling
```xml
<style name="SampleMarkdownTheme" parent="MarkdownTextView">
    <item name="markdownHorizontalPadding">@dimen/horizontal</item>
    <item name="markdownHVerticalPadding">@dimen/vertical</item>
    <item name="colorInlineCodeText">@color/colorAccent</item>
    <item name="colorBlockquoteText">@color/colorPrimaryDark</item>
    <item name="bgTextDrawable">@drawable/bg</item>
    <item name="bgTextDrawableLeft">@drawable/bg_left</item>
    <item name="bgTextDrawableMid">@drawable/bg_mid</item>
    <item name="bgTextDrawableRight">@drawable/bg_right</item>
    <item name="android:lineSpacingMultiplier">1.2</item>
</style>
```

# Special thanks to
- [Jabronis, untold.studio][jabronis]
- [John Gruber, daringfireball.net][johngruber]

[//]: # (UNTZ UNTZ UNTZ)

[textview]: <https://developer.android.com/reference/android/widget/TextView.html>
[spanned]: <https://developer.android.com/reference/android/text/Spanned>
[johngruber]: <https://daringfireball.net/2010/07/improved_regex_for_matching_urls>
[jabronis]: <https://untold.studio>