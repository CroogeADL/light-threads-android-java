# LightThreads for Android
LightTreads is designed to simplify using of threads in Android.  Enjoy using of Threads in your project!

## How to

To get **LightThreads** into your build:

### Step 1

Add it in your root `build.gradle` at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
   }
}
```

### Step 2

Add the dependency:
```
dependencies {
    ...
    implementation 'com.github.CroogeADL:light-threads-android-java:1.0.0'
}
```

### Step 3

Use methods of `LightThreads` and Enjoy!

```java
LightThreads.runInForeground(new Runnable() {
    @Override
    public void run() {
        Toast.makeText(MainActivity.this, "with / without delay" +
            "Enjoy using LightThreads!", Toast.LENGTH_SHORT).show();
    }
});
```

```java
LightThreads.runInBackground(new Runnable() {
    @Override
    public void run() {
        Log.i("with / without delay", "Enjoy using LightThreads!");
    }
});
```

```java
LightThreads.schedule(new Runnable() {
    @Override
    public void run() {
        Log.i("scheduled / periodically", "Enjoy using LightThreads!");
    }
}, 5, TimeUnit.SECONDS);
```

### License

The **LightThreads** is Open Source and available under the MIT license. See the LICENSE file for more information.