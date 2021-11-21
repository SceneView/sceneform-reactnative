# Type definitions

### AugmentedFaceModel

An `AugmentedFaceModel` is a structure meant to be used by the `addAugmentedFace` method. It has two properties:

- **model**: `URL`/`path` (inside android's assets folder) referencing a GLB object.
- **texture**: (nullable) `URL`/`path` (inside android's assets folder) referencing an image object.

Example:
`{model: 'models/fox.glb', texture: 'textures/freckles.png'}`

------------

### Plane

A plane is any touchable surface discovered in the AR session.
When the user taps on a plane it is automatically saved and an index is sent back to the bridge via the `onTapPlane` event.

You can access the value using `event.planeId`.
It can be used to host a cloud anchor or to place objects on tap.

------------

### CloudAnchor

It is a simple structure containing the CloudAnchorId in the `event.anchorId` value.

------------

### FeatureMapQuality

The FeatureMapQuality is an indicator used to get sure there is enough quality in the environment scan previous to host an anchor.

It is a value from 0 to 2 indicating the quality:

- 0: Insufficient
- 1: Sufficient
- 2: Good

You can access the value via `event.quality`
Host the anchor when the event returns at least 1.

------------

### VideoRecording

After `stopVideoRecording` is called (`startVideoRecording` must be called first), the video is copied into storage and its path is returned when the promise fulfills.

You can get the path via `response.path`

------------

### Screenshot

When `takeScreenshot` is called, the session's current view is copied into a bitmap and saved as a JPEG image. When the promise fulfills you can access the image via `response.path`

------------

### Model

The `addObject` method is used to insert a renderable into scene, it supports a `Model` object described as follows:

- **name**:             It must be an URL pointing to a glb asset.
- **anchorId**:         It can be a CloudAnchorId or a PlaneId (returned by onTapPlane)
- **isCloudAnchor**:    `boolean`, if true then the `anchorId` value will be taken as a CloudAnchorId and the session will attempt to resolve it, triggering `onAnchorResolve` if succeed. Otherwise, if `false` , `anchorId` will be taken as `Plane` and the object will be attached to it.

------------

### LocationMarker

A location marker is placed calculating its real world position related to the user location.
There are two types of tags currently supported, showing a label or a simple one with an icon.

The location marker structure is:

- **title**: `String` , it is the label to be shown by the marker.
- **lat**: `Double`, the latitude value.
- **lng**: `Double`, the longitude value.
- **isAnchor**: `boolean`, if `false` the title is shown in the marker, if `true` then the icon marker is used.

------------

### CloudAnchorId
A `String` value returned by the `onAnchorHost` event, you can use it to resolve the same anchor lately or share it with friends to get the same experience.