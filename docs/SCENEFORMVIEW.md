# SceneformView

### Props, Methods and Events

The `SceneformView` component supports the following props:

| name | values | default | required |
| :------------ | :------------ | :------------ | :------------ |
| viewMode | `true` for hosting, `false` for resolving | `false` | no |
| discoverMode | `true` for cloud anchors, `false` for location anchors | `true` | no |
| locationMarkers | `LocationMarker`[] | [] | no |
| displayPlanes | boolean | `true` | no |

Also, supports the following methods:

| name | params | return |
| :------------ | :------------ | :------------ |
| addObject | `Model` | void |
| hostCloudAnchor | `Plane` | void |
| resolveCloudAnchor | `CloudAnchorId` | void |
| takeScreenshot | | `Screenshot` promise |
| startVideoRecording | | boolean promise |
| stopVideoRecording | | `VideoRecording` promise |

The following events are supported:

| name | description | returns |
| :------------ | :------------ | :------------ |
| onSessionCreate | Triggered when a sceneform session has been initialised | |
| onTapPlane | Triggered when the user taps a plane | `Plane` |
| onAnchorResolve | Triggered when a cloud anchor has been resolved | `CloudAnchor` |
| onAnchorHost | Triggered when a cloud anchor has been hosted correctly | `CloudAnchor` |
| onFeatureMapQualityChange | Triggered when the feature map quality changes (HOSTING ONLY) | `FeatureMapQuality` |

Example
```js
<SceneformView
    ref={(c) => this.sceneformview = c}
    style={{width: '100%', height: '100%'}}
    onTapPlane={(event) => {
        this.sceneformview.addObject({name: "models/Rabbit.glb", anchorId: event.planeId, isCloudAnchor: false});
    }}
/>
```