package com.capstone.receh.fragment.akun

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.*
import com.capstone.receh.databinding.FragmentAkunBinding
import com.capstone.receh.ui.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class AkunFragment : Fragment() {

    private var _binding: FragmentAkunBinding? = null
    private lateinit var akunViewModel: MainViewModel
    private lateinit var imageUri: Uri
    private lateinit var auth: FirebaseAuth
    private var myBitmap: Bitmap? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root
        akunViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction()
        auth = FirebaseAuth.getInstance()
        val userData = auth.currentUser

        akunViewModel.getUser().observe(viewLifecycleOwner) {
            binding.apply {
                if (userData != null) {
                    userData.reload()
                    if (userData.photoUrl != null) {
                        Picasso.get().load(userData.photoUrl).placeholder(R.drawable.people)
                            .error(R.drawable.people).into(fotoakun)
                    } else {
                        Picasso.get().load("https://picsum.photos/200/300").into(fotoakun)
                    }
                }
                nameacc.text = it.name
                nomorhp.text = it.hp
                emailmu.text = it.email
                fotoakun.setOnClickListener {
                    startGallery()
                }
                button.setOnClickListener {
                    val image = when {
                        //kondisi upload foto baru
                        ::imageUri.isInitialized -> imageUri
                        //kondisi jika kita tdk upload foto = default foto
                        userData?.photoUrl == null -> Uri.parse("https://picsum.photos/id/316/200")
                        //kondidi jika sudah ada foto sebelumnya
                        else -> userData.photoUrl
                    }
                    val builder = AlertDialog.Builder(it.context)
                    with(builder) {
                        setTitle("Update Foto")
                        setMessage("Anda yakin?")
                        setPositiveButton("OK") { _: DialogInterface, _: Int ->
                            loadingimage(true)
                            UserProfileChangeRequest.Builder().setPhotoUri(image).build()
                                .also { changeReq ->
                                    userData?.updateProfile(changeReq)
                                        ?.addOnCompleteListener { update ->
                                            if (update.isSuccessful) {
                                                loadingimage(false)
                                                Toast.makeText(
                                                    activity,
                                                    "Foto telah diubah",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            } else {
                                                loadingimage(false)
                                                Toast.makeText(
                                                    activity,
                                                    update.exception?.message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        }
                                }
                        }
                        setNegativeButton(R.string.tidak) { _: DialogInterface, _: Int -> return@setNegativeButton }
                        show()
                    }
                }
            }
        }
    }

     fun startGallery() {
         val intent = Intent(Intent.ACTION_PICK)
         intent.type = "image/*"
         startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val img = data?.data
            myBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, img)
            uploadImage(myBitmap as Bitmap)
        }
    }

    private fun uploadImage(imgBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()
        loadingimage(true)
        ref.putBytes(image).addOnCompleteListener {
            if (it.isSuccessful) {
                ref.downloadUrl.addOnCompleteListener { task ->
                    task.result?.let { uri ->
                        loadingimage(false)
                        imageUri = uri
                        binding.fotoakun.setImageBitmap(imgBitmap)
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.logout.setOnClickListener {
            akunViewModel.logout()
        }
    }

    private fun loadingimage(loadingfoto: Boolean){
        if (loadingfoto) {
            binding.loading.visibility = View.VISIBLE
            binding.loading.bringToFront()
            binding.button.isEnabled = false
        } else {
            binding.loading.visibility = View.GONE
            binding.loading.bringToFront()
            binding.button.isEnabled = true
        }
    }

    companion object {
        const val IMAGE_PICK_CODE = 1000
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}