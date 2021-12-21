import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.weightpickercompose.ScrollableTabRow

@Composable
fun Sample(){
    ScrollableTabRow(
        backgroundColor = Color.Transparent,
        selectedTabIndex = 0,
        edgePadding = 24.dp,
        modifier = Modifier.height(80.dp)
    ) {
        (1..20).forEach { index ->
            Tab(
                selected = false,
                onClick = { },
                modifier = Modifier.padding(10.dp)
            ){
                Text("DemoBox_$index")
            }

        }
    }
}

@Preview
@Composable
fun prev(){
    Sample()
}