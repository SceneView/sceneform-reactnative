import React, { Component } from 'react';

import { StyleSheet, View, Button } from 'react-native';
import { SceneformView, AugmentedFacesView } from '../../src';

export default class App extends Component {
  constructor(props){
    super(props);
    this.state = {
      face: 0
    }
    this.toggleFace = this.toggleFace.bind(this);
  }

  toggleFace = () => {
    var face;
    if(this.state.face == 0){
      face = 1;
    }
    else{
      face = 0;
    }
    this.setState({face: face});
  }

  render(){
    return (
      <View style={styles.container}>
        <AugmentedFacesView
          setAugmentedFace={this.state.face}
          ref={(c) => this.sfRef = c}
          style={styles.box}
          viewMode={true}
          />
        <View style={{width: '100%', height: 100, position: 'absolute', bottom: 0, left: 0, alignItems: 'center', justifyContent: 'center'}}>
          <View style={{width: 150}}>
            <Button title="Toggle face model" onPress={this.toggleFace}/>
          </View>
        </View>
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
