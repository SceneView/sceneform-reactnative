## AugmentedFacesView

### Props, Methods and Events

The AugmentedFacesView supports the following props:

| name | values |
| :------------ | :------------ |
| setAugmentedFace | `index` returned by the `addAugmentedFace` method. Use -1 as starting value. |


It also supports the following methods:

| name | params | return |
| :------------ | :------------ | :------------ |
| addAugmentedFace | `AugmentedFaceModel` | `Promise` returning an `index` to be used by the `setAugmentedFace` prop |
| takeScreenshot | | `Screenshot` promise |
| startVideoRecording | | boolean promise |
| stopVideoRecording | | `VideoRecording` promise |

[Example](https://github.com/doranteseduardo/augmented-faces-demo)