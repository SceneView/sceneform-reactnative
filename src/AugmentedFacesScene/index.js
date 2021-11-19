'use strict';
import React from 'react';
import { requireNativeComponent, View, DeviceEventEmitter, NativeModules, findNodeHandle } from 'react-native';
import PropTypes from 'prop-types';

class AugmentedFacesView extends React.Component {
    constructor() {
        super();
        this.NativeArView = undefined;
        this.onChange = this.onChange.bind(this);
        this.subscriptions = [];
        this.mounted = false;
        this.state = {

        }
    }

    onChange(event) {
        
    }

    componentDidMount() {
        this.mounted = true;
    }

    componentWillUnmount() {
        this.subscriptions.forEach(sub => sub.remove());
        this.subscriptions = [];
        this.mounted = false;
    }

    addAugmentedFace = ({model, texture}) => {
        const mModel = model;
        if(!model){
            throw 'At least a model source is required';
        }
        const mTexture = texture || '';
        // returns an index;
        console.log("Adding object to scene", model, texture)
        return NativeModules.SceneformAugmentedFacesModule.addFaceModel(findNodeHandle(this), mModel, mTexture);
    }

    render() {
        return <NativeArView ref={(c) => this.NativeArView = c} {...this.props} onChange={this.onChange}/>
    }
}

AugmentedFacesView.propTypes = {
  ...View.propTypes,
};

const NativeArView = requireNativeComponent('SceneformAugmentedFacesView', AugmentedFacesView, {
    nativeOnly: { onChange: true }
});

module.exports = AugmentedFacesView;