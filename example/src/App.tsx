import React, { Component } from 'react';

import { StyleSheet, View, Button, FlatList, TouchableOpacity, Text } from 'react-native';
import { SceneformView, AugmentedFacesView } from '../../src';

const Faces = [
  {title: "Points", model: 'models/face.glb', texture: 'textures/face.png'},
  {title: "Fox", model: 'models/fox.glb', texture: 'textures/freckles.png'},
]

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
          ref={(c) => this.sfRef = c}
          style={styles.box}
          />
        <View style={{width: '100%', height: 100, position: 'absolute', bottom: 0, left: 0, alignItems: 'center', justifyContent: 'center'}}>
          <FlatList
            style={{width: '100%', height: 60}}
            horizontal={true}
            data={this.state.faces}
            renderItem={({item}) => {
              return(
                <TouchableOpacity key={item.id} style={{width: 60, height: 60, padding: 5}} onPress={() => {this.setState({face: item.id})}}>
                  <View style={{width: '100%', height: '100%', backgroundColor: 'white', alignItems: 'center', justifyContent: 'center'}}>
                    <Text style={{textAlign: 'center'}}>{item.title}</Text>
                  </View>
                </TouchableOpacity>
              );
            }}
          />
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
