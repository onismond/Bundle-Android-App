package com.bundlegh.bundle.ui.new_product

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.bundlegh.bundle.databinding.ActivityNewProductBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.util.*
import kotlinx.android.synthetic.main.activity_new_product.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.*


class NewProductActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: NewProductViewModelFactory by instance()
    private lateinit var viewModel: NewProductViewModel
    private lateinit var binding: ActivityNewProductBinding
    private lateinit var newProductAdapter: NewProductAdapter
    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button
    private lateinit var viewPager2: ViewPager2
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        viewPager2 = binding.viewPager2
        buttonNext = binding.buttonNext
        buttonPrevious = binding.buttonPrevious

        viewModel = ViewModelProvider(this, factory)[NewProductViewModel::class.java]
        setupAdapter()
        binding.buttonPrevious.setOnClickListener {
            previousPage()
        }
        binding.buttonNext.setOnClickListener {
            nextPage()
        }
        updateCategories()
        binding.selectImage.setOnClickListener { hideSelectImage() }
        binding.constraintChooseImage.setOnClickListener { chooseImage() }
        binding.constraintTakePicture.setOnClickListener { startCamera() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupAdapter(){
        newProductAdapter = NewProductAdapter(supportFragmentManager, lifecycle)
        binding.viewPager2.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = newProductAdapter
        }
    }

    private fun nextPage(){
        val last = viewPager2.nextPage(true)
        if (buttonNext.text == "Submit"){
            submit()
        }
        if (last){
            buttonNext.text = "Submit"
        }
        buttonPrevious.visibility = View.VISIBLE
    }

    private fun previousPage() {
        val first = viewPager2.previousPage(true)
        buttonNext.text = "Next"
        if (first) {
            buttonPrevious.visibility = View.GONE
        }
    }

    private fun submit() {
        val name = (supportFragmentManager.findFragmentByTag("f0") as ProductOneFragment).getName()
        val amount = (supportFragmentManager.findFragmentByTag("f0") as ProductOneFragment).getAmount()
        val category = (supportFragmentManager.findFragmentByTag("f1") as ProductTwoFragment).getCategory()
        val subCategory = (supportFragmentManager.findFragmentByTag("f1") as ProductTwoFragment).getSubCategory()
        val description = (supportFragmentManager.findFragmentByTag("f2") as ProductThreeFragment).getDescription()
        val images = (supportFragmentManager.findFragmentByTag("f3") as ProductFourFragment).getImages()
        if (name.isEmpty()) {
            binding.root.snackbar("Product name is required")
            return
        }
        if (amount.isEmpty()) {
            binding.root.snackbar("Amount is required")
            return
        }
        if (category.isEmpty()) {
            binding.root.snackbar("Category is required")
            return
        }
        if (subCategory.isEmpty()) {
            binding.root.snackbar("SubCategory is required")
            return
        }
        if (description.isEmpty()) {
            binding.root.snackbar("Description is required")
            return
        }
        if (images.size <= 0) {
            binding.root.snackbar("Please capture the product")
            return
        }
        Coroutines.io {
            try {
                val images_parts = mutableListOf<MultipartBody.Part>()
                for (i in 0 until images.size) {
                    images_parts.add(buildImageBodyPart("image$i", images[i]))
                }
                val map = HashMap<String, String>()
                map["token"] = viewModel.getUserToken()
                map["name"] = name
                map["amount"] = amount
                map["category"] = category
                map["subcategory"] = subCategory
                map["description"] = description

                val gson = Gson()

                val response = viewModel.newProduct(images_parts, gson.toJson(map).toString())
                if (response.isSuccessful!!){
                    binding.root.snackbar("success")
//                    finish()
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

    fun alertConfirmDelete(pos: Int): Boolean {
        val alertDialogBuilder = AlertDialog.Builder(this@NewProductActivity)

        alertDialogBuilder.setTitle("Remove Image")
        alertDialogBuilder.setMessage("Do you want to remove this image?")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton("Remove"){_,_ ->
            (supportFragmentManager.findFragmentByTag("f3") as ProductFourFragment).removeImage(pos)
        }
        alertDialogBuilder.setNeutralButton("Cancel"){_,_ ->
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        return true
    }

    fun chooseImage(){
        val fileIntent = Intent()
        fileIntent.type = "image/*"
        fileIntent.action = Intent.ACTION_GET_CONTENT
        hideSelectImage()
        startActivityForResult(Intent.createChooser(fileIntent, "Select Picture"),
            file_id
        )

    }

    fun startCamera(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.bundlegh.bundle.fileprovider",
                photoFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//            Log.e("LOCATION", (Uri.fromFile(photoFile)).toString())
            hideSelectImage()
            startActivityForResult(cameraIntent, pic_id)
        }
    }

    companion object {
        private const val pic_id = 123
        private const val file_id = 1234
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
                (supportFragmentManager.findFragmentByTag("f3") as ProductFourFragment).addImage(photo!!)
            } else if (requestCode == file_id) {

                val imageUri = data!!.data
                val photo = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                (supportFragmentManager.findFragmentByTag("f3") as ProductFourFragment).addImage(photo!!)
            }
        } catch (e: Exception) {
//            Log.e("Error", "No image returned")
//            binding.root.snackbar("No image captured")
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

    private fun buildImageBodyPart(fileName: String, bitmap: Bitmap): Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap)
        val reqFile = RequestBody.create(MediaType.parse("image/*"),    leftImageFile)
        return MultipartBody.Part.createFormData(fileName,     leftImageFile.name, reqFile)
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(cacheDir, fileName)
        //file.createNewFile()

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

    private fun updateCategories() {
        Coroutines.io {
            try {
                val categoryResponse = viewModel.loadCategories()
                for (category in categoryResponse.categories)
                    viewModel.saveCategory(category)
            } catch (e: Exception) {
//                activity?.runOnUiThread { binding.root.snackbar("Sorry, an error occurred") }
            }
        }
        Coroutines.io {
            try {
                val subCategoryResponse = viewModel.loadSubCategories()
                for (subCategory in subCategoryResponse.sub_categories)
                    viewModel.saveSubCategory(subCategory)
            } catch (e: Exception) {
//                activity?.runOnUiThread { binding.root.snackbar("Sorry, an error occurred") }
            }
        }
    }

    fun showSelectImage() {
        binding.selectImage.visibility = View.VISIBLE
    }

    private fun hideSelectImage() {
        binding.selectImage.visibility = View.GONE
    }


}

