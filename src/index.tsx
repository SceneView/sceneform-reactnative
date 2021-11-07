import {
  requireNativeComponent,
  UIManager,
  Platform,
  ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-sceneform' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

type SceneformProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'SceneformView';

export const SceneformView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<SceneformProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
