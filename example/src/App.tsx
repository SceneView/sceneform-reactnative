import React, { Component } from 'react';

import { StyleSheet, View } from 'react-native';
import SceneformView from 'react-native-sceneform';

export default class App extends Component {

  render(){
    return (
      <View style={styles.container}>
        <SceneformView
          ref={(c) => this.sfRef = c}
          style={styles.box}
          viewMode={true}
          onTapPlane={(event) => {
            this.sfRef.addObject({name: "https://storage.googleapis.com/linkworld/modelos/banana/banana.glb", anchorId: event.planeId, isCloudAnchor: false});
          }}
          />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    width: "100%",
    height: "100%",
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: "100%",
    height: "100%",
    marginVertical: 20,
  },
});
