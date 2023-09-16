package com.devscion.metaprobesample

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devscion.metaprobe.MetaProbe
import com.devscion.metaprobe.model.ProbedData
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import tech.devscion.typist.Typist
import tech.devscion.typist.TypistSpeed

private const val TAG = "LinkDetailItem"

@Composable
fun LinkDetailItem(
    url: String,
) {

    val probedData = remember {
        mutableStateOf<ProbedData?>(null)
    }
    val isLoading = remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = Unit) {
        //Callback Method

//        MetaProbe(url)
//            .probeLink(object : OnMetaDataProbed {
//                override fun onMetaDataProbed(pb: Result<ProbedData>) {
//                    isLoading.value = false
//                    Log.d(TAG, "onMetaDataProbed: $pb")
//                    Log.d(TAG, "onMetaDataProbed: ${pb.getOrNull()?.title}")
//                    Log.d(TAG, "onMetaDataProbed: ${pb.getOrNull()?.icon}")
//                    Log.d(
//                        TAG,
//                        "onMetaDataProbed: ${pb.getOrNull()?.description}"
//                    )
//                    probedData.value = pb.getOrNull()
//                }
//            })

        //Suspend function
        MetaProbe(url)
            .apply {
                setClient(
                    HttpClient(Android) {
                        engine {
                            connectTimeout = 100_000
                            socketTimeout = 100_000
                        }
                    }
                )
            }
            .probeLink()
            .let { pb ->
                isLoading.value = false
                Log.d(TAG, "onMetaDataProbed: $pb")
                Log.d(TAG, "onMetaDataProbed: ${pb.getOrNull()?.title}")
                Log.d(TAG, "onMetaDataProbed: ${pb.getOrNull()?.icon}")
                Log.d(TAG, "onMetaDataProbed: ${pb.getOrNull()?.image}")
                Log.d(
                    TAG,
                    "onMetaDataProbed: ${pb.getOrNull()?.description}"
                )
                probedData.value = pb.getOrNull()
            }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(Color.DarkGray)
            .border(
                2.dp,
                Brush.linearGradient(
                    listOf(
                        Color.Green,
                        Color.Green,
                        Color.LightGray,
                        Color.LightGray,
                    )
                ),
                RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
    ) {
        if (isLoading.value) {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Typist(
                    text = "Probing Metadata...",
                    textStyle = TextStyle(color = Color.White),
                    typistSpeed = TypistSpeed.FAST,
                    cursorColor = Color.White
                )
            }
        }
        probedData.value?.let {
            Column(
                Modifier
                    .wrapContentHeight()
                    .scrollable(
                        rememberScrollState(),
                        orientation = Orientation.Vertical
                    )
            ) {
                it.image?.let { img ->
                    Box {
                        KamelImage(
                            resource = asyncPainterResource(data = img),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentScale = ContentScale.FillBounds
                        )
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.LightGray.copy(alpha = 0f),
                                            Color.LightGray.copy(alpha = 0.1f),
                                            Color.LightGray.copy(alpha = 0.1f),
                                            Color.LightGray.copy(alpha = 0.5f),
                                            Color.LightGray.copy(alpha = 0.5f),
                                            Color.Gray,
                                        )
                                    )
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(1.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    it.icon?.let {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color.White)
                        ) {
                            KamelImage(
                                asyncPainterResource(data = it),
                                "",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = url, style = TextStyle(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray
                            )
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        it.title?.let {
                            Text(
                                text = it, style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(1.dp))
                        it.description?.let {
                            Text(
                                text = it, style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}