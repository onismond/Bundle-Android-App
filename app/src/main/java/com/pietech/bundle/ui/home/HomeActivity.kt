package com.pietech.bundle.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.pietech.bundle.R
import com.pietech.bundle.databinding.ActivityHomeBinding
import com.pietech.bundle.ui.home.home.HomeFragmentDirections
import com.pietech.bundle.ui.home.profile.ProfileFragmentDirections
import com.pietech.bundle.util.*
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.*

class HomeActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: HomeViewModel
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        navController = findNavController(R.id.nav_fragment)
        val appBarConfiguration = AppBarConfiguration
            .Builder(R.id.homeFragment, R.id.searchFragment, R.id.productsFragment, R.id.cartFragment, R.id.profileFragment)
            .build()
        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_nav, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_notifications -> {
            true
        }

        R.id.action_refresh -> {
            true
        }

        R.id.action_logout -> {
            binding.root.snackbar("log out")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_fragment).navigateUp() || super.onNavigateUp()

    fun setBottomNavigationVisibility(visibility: Int){
        binding.bottomNavigationView.visibility = visibility
    }

    fun navigateToCart(){
        val item = bottomNavigationView.menu.findItem(R.id.cartFragment)
        NavigationUI.onNavDestinationSelected(item, navController)
    }

    fun navigateToProfile(){
        val item = bottomNavigationView.menu.findItem(R.id.profileFragment)
        NavigationUI.onNavDestinationSelected(item, navController)
    }


    private fun submit(photo: Bitmap) {
        Coroutines.io {
            try {
                val imagePart = buildImageBodyPart("image", photo)

                val map = HashMap<String, String>()
                map["token"] = viewModel.getUserToken()
                val gson = Gson()
                val response = viewModel.changeAvatar(imagePart, gson.toJson(map).toString())
                Log.e("E", response.toString())
                if (response.isSuccessful!!){
                    val user = response.user!!
                    user.token = response.token
                    viewModel.saveLoggedInUser(user)
                    binding.root.snackbar("success")
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                binding.root.snackbar(e.message!!)
            } catch (e: NoInternetException) {
                binding.root.snackbar(e.message!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startCamera(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.pietech.bundle.fileprovider",
                photoFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            Log.e("LOCATION", (Uri.fromFile(photoFile)).toString())
            startActivityForResult(cameraIntent, pic_id)
        }
    }

    companion object {
        private const val pic_id = 1235
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == pic_id) {
                var photo = BitmapFactory.decodeFile(currentPhotoPath)
                val exif = ExifInterface(currentPhotoPath)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                val matrix = Matrix()

                when(orientation){
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
                }

                val rotatedBitmap = Bitmap.createBitmap(photo, 0,0 , photo.width, photo.height, matrix, true)
                photo = rotatedBitmap
//                val photo = data!!.extras!!["data"] as Bitmap? // returns bitmap
                submit(photo!!)
            }
        } catch (e: Exception) {
            Log.e("Error", "No image returned")
            binding.root.snackbar("No image captured")
        }
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis()
        val imageFileName = "product_image$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun buildImageBodyPart(fileName: String, bitmap: Bitmap): MultipartBody.Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap)
        val reqFile = RequestBody.create(MediaType.parse("image/*"),    leftImageFile)
        return MultipartBody.Part.createFormData(fileName,     leftImageFile.name, reqFile)
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(cacheDir, fileName)
//        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(
            okhttp3.MultipartBody.FORM, descriptionString)
    }

}



















