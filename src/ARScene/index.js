'use strict';
import React from 'react';
import { requireNativeComponent, View, DeviceEventEmitter, NativeModules, findNodeHandle } from 'react-native';
import PropTypes from 'prop-types';

class SceneformView extends React.Component {
    constructor() {
        super();
        this.NativeArView = undefined;
        this.onChange = this.onChange.bind(this);
        this.subscriptions = [];
    }

    onChange(event) {
        if(event.nativeEvent.onAnchorCreate){
            if (!this.props.onAnchorCreate) { return; }
            this.props.onAnchorCreate({ onAnchorCreate: event.nativeEvent });
        }
        if(event.nativeEvent.onAnchorResolve){
            if (!this.props.onAnchorResolve) { return; }
            this.props.onAnchorResolve({ onAnchorResolve: event.nativeEvent});
        }
        if(event.nativeEvent.onSessionCreate){
            if(!this.props.onSessionCreate){ return; }
            this.props.onSessionCreate({ onSessionCreate: event.nativeEvent});
        }
        if(event.nativeEvent.onTapPlane){
            if(!this.props.onTapPlane){ return; }
            this.props.onTapPlane({ onTapPlane: event.nativeEvent});
        }
        if(event.nativeEvent.onFeatureMapQualityChange){
            if(!this.props.onFeatureMapQualityChange){ return; }
            this.props.onFeatureMapQualityChange({ onFeatureMapQualityChange: event.nativeEvent});
        }
        if(event.nativeEvent.onAnchorHost){
            if(!this.props.onAnchorHost){ return; }
            this.props.onAnchorHost({ onAnchorHost: event.nativeEvent});
        }
    }

    componentDidMount() {
        if (this.props.onAnchorCreate) {
            let sub = DeviceEventEmitter.addListener('onAnchorCreate',this.props.onAnchorCreate);
            this.subscriptions.push(sub);
        }
        if (this.props.onAnchorResolve) {
            let sub = DeviceEventEmitter.addListener('onAnchorResolve', this.props.onAnchorResolve);
            this.subscriptions.push(sub);
        }
        if(this.props.onSessionCreate){
            let sub = DeviceEventEmitter.addListener("onSessionCreate", this.props.onSessionCreate);
            this.subscriptions.push(sub);
        }
        if(this.props.onTapPlane){
            let sub = DeviceEventEmitter.addListener("onTapPlane", this.props.onTapPlane);
            this.subscriptions.push(sub);
        }
        if(this.props.onAnchorHost){
            let sub = DeviceEventEmitter.addListener("onAnchorHost", this.props.onAnchorHost);
            this.subscriptions.push(sub);
        }
        if(this.props.onFeatureMapQualityChange){
            let sub = DeviceEventEmitter.addListener("onFeatureMapQualityChange", this.props.onFeatureMapQualityChange);
            this.subscriptions.push(sub);
        }
    }

    componentWillUnmount() {
        this.subscriptions.forEach(sub => sub.remove());
        this.subscriptions = [];
    }

    /**
     * 
     * @param {Model} model 
     */
    // {anchorId, name, isCloudAnchor?}
    addObject = (model) => {
        NativeModules.SceneformModule.addObject(findNodeHandle(this), model);
    }

    takeScreenshot = () => {
        return NativeModules.SceneformModule.takeSnapshot(findNodeHandle(this));
    }

    hostCloudAnchor = (anchorId) => {
        NativeModules.SceneformModule.hostCloudAnchor(findNodeHandle(this), parseInt(anchorId));
    }

    resolveCloudAnchor = (anchorId) => {
        NativeModules.SceneformModule.resolveCloudAnchor(findNodeHandle(this), anchorId);
    }

    startVideoRecording = () => {
        return NativeModules.SceneformModule.startVideoRecording(findNodeHandle(this));
    }

    stopVideoRecording = () => {
        return NativeModules.SceneformModule.stopVideoRecording(findNodeHandle(this));
    }

    render() {
        return <NativeArView ref={(c) => this.NativeArView = c} {...this.props} onChange={this.onChange}/>
    }
}

SceneformView.propTypes = {
  ...View.propTypes,
    viewMode:                       PropTypes.bool,
   onAnchorCreate:                  PropTypes.func,
   onAnchorResolve:                 PropTypes.func,
   onSessionCreate:                 PropTypes.func,
   displayPointCloud:               PropTypes.bool,
   displayPlanes:                   PropTypes.bool

};

const NativeArView = requireNativeComponent('SceneformView', SceneformView, {
    nativeOnly: { onChange: true }
});

module.exports = SceneformView;