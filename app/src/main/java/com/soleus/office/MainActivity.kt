package com.soleus.office

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.soleus.office.ui.nav.NavGraph
import com.soleus.office.ui.theme.Kagit
import com.soleus.office.ui.theme.SoleusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoleusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Kagit
                ) {
                    NavGraph()
                }
            }
        }
    }
}
