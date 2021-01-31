import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.inzynierka.R
import com.example.inzynierka.models.Photo
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class PhotoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
     return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var photo = getFullscreenPhoto()

        val photoView = view.findViewById<ImageView>(R.id.fullscreen_photo)
        val photoText = view.findViewById<TextView>(R.id.name_fullscreenPhoto)
        val deleteBtn = view.findViewById<ImageButton>(R.id.delete_photo_btn)

        Glide.with(view).load(photo.url)
            .into(photoView)
        photoText.text = photo.Text
        deleteBtn.setOnClickListener{
            val storageReference = Firebase.storage.getReferenceFromUrl(photo.url)
            storageReference.delete().addOnSuccessListener { // File deleted successfully
                Toast.makeText(context, "File deleted", Toast.LENGTH_LONG)
                view.findNavController().navigate(R.id.action_navigation_fullscreenPhoto_to_navigation_main_fragment)
            }.addOnFailureListener { // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file")
                Toast.makeText(context, "Error while deleting file", Toast.LENGTH_LONG)
            }
        }



    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}
var photo: Photo = Photo("", "")

fun setFullscreenPhoto(_photo: Photo){
        photo = _photo
}
fun getFullscreenPhoto(): Photo {
    return photo
}



