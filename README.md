### Features

- Remote assets
- Cloud anchors
- Plane detection
- Feature quality indicator
- Depth API
- Location anchors
- Video recording
- Screenshots

# react-native-sceneform

![](https://raw.githubusercontent.com/kboy-silvergym/ARCore-Kotlin-Sampler/master/readmeImages/sceneform.jpg)

# Requirements
This package requires your app to target Android SDK 24 at least and react-native 0.66+

# Installation

Install from npm running `npm install --save react-native-sceneform`
Then, add the following to your AndroidManifest.xml inside the Application node.
```xml
<meta-data android:name="com.google.ar.core" android:value="optional" />
```

If you are going to use Cloud Anchors, be sure to add your API Key to the AndroidManifest or to sign your application in the Google Cloud Platform console (keyless auth)

# Props, Methods and Events
The `SceneformView` component supports the following props:

|  name | values  | default  | required  |
| :------------ | :------------ | :------------ | :------------ |
| viewMode  | `true` for hosting, `false` for resolving  | `false`  | no |
| discoverMode  | `true` for cloud anchors, `false` for location anchors  | `true`  | no |
| locationMarkers  | `LocationMarker`[]  | []  | no |
| displayPlanes  | boolean  | `true`  | no |

Also, supports the following methods:

|  name | params  | return |
| :------------ | :------------ | :------------ |
| addObject  | `Model`  |  void  |
| hostCloudAnchor  | `Plane`  |  void  |
| resolveCloudAnchor  | `CloudAnchorId`  |  void  |
| takeScreenshot  |   |  `Screenshot` promise  |
| startVideoRecording  |   |  boolean promise  |
| stopVideoRecording  |   |  `VideoRecording` promise  |

The following events are supported:

|  name | description  | returns |
| :------------ | :------------ | :------------ |
| onSessionCreate  | Triggered when a sceneform session has been initialised  |   |
| onTapPlane  | Triggered when the user taps a plane  |  `Plane` |
| onAnchorResolve  | Triggered when a cloud anchor has been resolved  |  `CloudAnchor` |
| onAnchorHost  | Triggered when a cloud anchor has been hosted correctly  |  `CloudAnchor` |
| onFeatureMapQualityChange  | Triggered when the feature map quality changes (HOSTING ONLY)  |  `FeatureMapQuality` |

# Type definitions

## Plane
A plane is any touchable surface discovered in the AR session.
When the user taps on a plane it is automatically saved and an index is sent back to the bridge via the `onTapPlane` event.

You can access the value using `event.planeId`.
It can be used to host a cloud anchor or to place objects on tap.

## CloudAnchor
It is a simple structure containing the CloudAnchorId in the `event.anchorId` value.

## FeatureMapQuality
The FeatureMapQuality is an indicator used to get sure there is enough quality in the environment scan previous to host an anchor.

It is a value from 0 to 2 indicating the quality:
0: Insufficient
1: Sufficient
2: Good

You can access the value via `event.quality`
Host the anchor when the event returns at least 1.

## VideoRecording
After `stopVideoRecording` is called (`startVideoRecording` must be called first), the video is copied into storage and its path is returned when the promise fulfills.

You can get the path via `response.path`

## Screenshot
When `takeScreenshot` is called, the session's current view is copied into a bitmap and saved as a JPEG image. When the promise fulfills you can access the image via `response.path`

## Model
The `addObject` method is used to insert a renderable into scene, it supports a `Model` object described as follows:

##### model
It must be an URL pointing to a glb asset.

##### anchorId
It can be a CloudAnchorId or a PlaneId (returned by onTapPlane)

##### isCloudAnchor
`boolean`, if true then the `anchorId` value will be taken as a CloudAnchorId and the session will attempt to resolve it, triggering `onAnchorResolve` if succeed.

Otherwise, if `false` , `anchorId` will be taken as `Plane` and the object will be attached to it.

## LocationMarker
A location marker is placed calculating its real world position related to the user location.

There are two types of tags currently supported, showing a label or a simple one with an icon.

The location marker structure is:

##### title
`String` , it is the label to be shown by the marker.

##### lat
`Double`, the latitude value.

##### lng
`Double`, the longitude value.

##### isAnchor
`boolean`, if `false` the title is shown in the marker, if `true` then the icon marker is used,

## CloudAnchorId
A `String` value returned by the `onAnchorHost` event, you can use it to resolve the same anchor lately or share it with friends to get the same experience.


# To do
- Support for bundled assets
- Augmented Faces
- No-AR view (3D model viewer)
- Runtime renderable creation
- Custom lights
- Animation manipulation (currently animations are played automatically)
- Depth toggling
