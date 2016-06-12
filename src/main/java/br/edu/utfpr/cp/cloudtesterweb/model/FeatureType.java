package br.edu.utfpr.cp.cloudtesterweb.model;

/**
 *
 * @author Douglas
 */
public enum FeatureType {
    // storage
    STORAGE_UPLOAD,
    STORAGE_DOWNLOAD,
    STORAGE_LIST,
    STORAGE_DELETE,
    // queue
    QUEUE_CREATE_QUEUE,
    QUEUE_RETRIEVE_QUEUE,
    QUEUE_DELETE_QUEUE,
    QUEUE_ADD_MESSAGE,
    QUEUE_RETRIEVE_MESSAGE,
    QUEUE_DELETE_MESSAGE;
}
