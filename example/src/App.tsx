import React, { useRef } from 'react';
import { StyleSheet, View, TouchableOpacity } from 'react-native';
import { SceneformView, ModelViewer, AugmentedFacesView } from '../../src';

const App = () => {
  const sceneformView = useRef(null);

  return (
    <View style={styles.container}>
      <SceneformView
        ref={sceneformView}
        style={styles.container}
        onTapPlane={(event) => {
          sceneformView.current.addObject({name: "models/Rabbit.glb", anchorId: event.planeId, isCloudAnchor: false});
        }}
        />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  }
});

export default App;