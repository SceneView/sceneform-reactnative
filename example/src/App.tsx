import React, { Component } from 'react';

import { StyleSheet, View, Button, FlatList, TouchableOpacity, Text } from 'react-native';
import { SceneformView, AugmentedFacesView } from '../../src';

export default class App extends Component {
  constructor(props){
    super(props);
    this.state = {

    }
  }

  componentDidMount(){

  }

  render(){
    return (
      <View style={styles.container}>
        <SceneformView
          ref={(c) => this.sceneformview = c}
          style={styles.box}
          onTapPlane={(event) => {
            this.sceneformview.addObject({name: "models/Rabbit.glb", anchorId: event.planeId, isCloudAnchor: false});
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
