package ni.edu.uam.practica2704

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Profile(
    val username: String,
    val name: String,
    val lastName: String,
    val interests: List<String>,
    val age: String,
    val email: String,
    val photoUri: Uri?
)

class ProfileViewModel : ViewModel() {
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    fun saveProfile(profile: Profile) {
        viewModelScope.launch {
            _profile.value = profile
        }
    }
}
