package com.devscion.metaprobe.utils

import com.devscion.metaprobe.model.ProbedData


interface OnMetaDataProbed {

    fun onMetaDataProbed(probedData: Result<ProbedData>)

}