package com.devscion.metaprobe.model

sealed class MetaProbeError : Throwable() {
    object NetworkError : MetaProbeError()
    object ParsingError : MetaProbeError()
    object UnknownError : MetaProbeError()

}
