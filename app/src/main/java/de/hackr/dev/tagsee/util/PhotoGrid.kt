package de.hackr.dev.tagsee.util

//unused. was the gridview, but I prefer the single column 'polaroid' style
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun PhotoGrid(photos: List<String>) {
//    LazyVerticalGrid(cells = GridCells.Fixed(3)) {
//        items(photos.size) { index ->
//            Box(modifier = Modifier.size(150.dp)) {
//                Image(
//                    painter = rememberImagePainter(
//                        data = photos[index]
//                    ),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .fillMaxSize(),
//                    contentScale = ContentScale.FillBounds
//                )
//            }
//        }
//    }
//}