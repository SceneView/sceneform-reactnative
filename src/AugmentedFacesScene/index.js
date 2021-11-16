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
    }

    onChange(event) {
        
    }

    componentDidMount() {

    }

    componentWillUnmount() {
        this.subscriptions.forEach(sub => sub.remove());
        this.subscriptions = [];
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