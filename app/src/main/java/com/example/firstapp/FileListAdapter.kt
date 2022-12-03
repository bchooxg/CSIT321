package com.example.firstapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.nio.file.Files

class FileListAdapter(files: ArrayList<File>?, fileList: fileList) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var files: ArrayList<File>? = null
    private var fileList: fileList? = null

    init {
        this.files = files
        this.fileList = fileList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return FileViewHolder(parent, fileList)
    }

   fun removeItem(position: Int) {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val file = files!![position]
        (holder as FileViewHolder).bind(file)

        // if selected file is directory set image resource to folder
        if (file.isDirectory) {
            holder.imageView?.setImageResource(R.drawable.ic_baseline_folder_24)
        }else{
            // else set image resource to file
            holder.imageView?.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
        }

        // Set on click listener for each item
        holder.itemView.setOnClickListener {
            // if selected file is directory, create intent to go to file list
            if(file.isDirectory){
                var path = file.absolutePath
                fileList?.openDirectory(path)
            }else{
                // else create intent to go to file viewer
                var uri = Uri.parse(file.absolutePath)
                fileList?.openFile(uri)

            }
        }

        // Set long click listener for each item
        holder.itemView.setOnLongClickListener {
            // Show popup menu
            val popup = PopupMenu(fileList, holder.itemView)
            popup.menu.add("Delete")
            popup.menu.add("Move")
            popup.menu.add("Rename")
            popup.menu.add("Encrypt")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Delete" -> {
                        // Delete file
                        // Check permissions to delete file
                        Log.v("Can write", file.canWrite().toString())
                        Log.v("Can Read", file.canRead().toString())
                        Log.v("Can Execute", file.canExecute().toString())

//                        fileList?.contentResolver?.let { it1 ->
//                            val uri = file.toUri()
//                            MediaStore.createDeleteRequest(it1, mutableListOf(uri))
//                        }
                        if(file.delete()){
                            // Hide item
                            holder.itemView.visibility = View.GONE
                            // Notify adapter that file is deleted
//                            notifyItemRemoved(position)
//                            notifyItemRangeChanged(position, files!!.size)
                            Toast.makeText(fileList, "File deleted", Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(fileList, "File not deleted", Toast.LENGTH_SHORT).show()
                        }

                        // Remove item from recyclerview
                        files?.removeAt(position)

                        // Update recycler view
                        Log.v("Position", position.toString())
                        Log.v("Files", files.toString())
                        Log.v("Files", files?.size.toString())
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, this.itemCount)
                        Log.v("Files", files.toString())
                        Log.v("Files", files?.size.toString())






                        true
                    }
                    "Move" -> {
                        // Move file
                        // Create toast to show that this feature is not implemented
                        Toast.makeText(fileList, "Move feature is not implemented", Toast.LENGTH_SHORT).show()
                        true
                    }
                    "Rename" -> {
                        // Rename file
                        Toast.makeText(fileList, "Rename feature is not implemented", Toast.LENGTH_SHORT).show()
                        true
                    }
                    "Encrypt" -> {
                        // Encrypt file

                        // Get file extension
                        var extension = file.extension
                        // Create new file with .enc extension
                        var newFile = File(file.absolutePath + ".enc")
                        // Encrypt file
                        // Throw error if file is not encrypted
                        if(file.renameTo(newFile)) {
                            Toast.makeText(fileList, "File encrypted", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(fileList, "File not encrypted", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    else -> false
                }
            }

            popup.show()
            true
        }


    }

    override fun getItemCount(): Int {
        return files!!.size
    }

    class FileViewHolder(parent: ViewGroup, fileList: fileList?) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)) {
        val imageView: ImageView? = null
        private var fileList: fileList? = null
        private var fileName: android.widget.TextView? = null
        private var fileIcon: android.widget.ImageView? = null

        init {
            this.fileList = fileList
            fileName = itemView.findViewById(R.id.tv_fileName)
            fileIcon = itemView.findViewById(R.id.iv_fileIcon)
        }

        fun bind(file: File) {
            fileName!!.text = file.name

            // get file type
            val fileType = file.name.substring(file.name.lastIndexOf(".") + 1)

            if (file.isDirectory) {
                fileIcon!!.setImageResource(R.drawable.ic_baseline_folder_24)
            } else if (fileType == "txt") {
                fileIcon!!.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
            } else if (fileType == "pdf") {
                fileIcon!!.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24)
            } else if (fileType == "png" || fileType == "jpg" || fileType == "jpeg") {
                fileIcon!!.setImageResource(R.drawable.ic_baseline_photo_24)
            } else if (fileType == "mp4" || fileType == "mkv" || fileType == "avi") {
                fileIcon!!.setImageResource(R.drawable.ic_baseline_movie_24)
            } else if (fileType == "mp3" || fileType == "wav") {
                fileIcon!!.setImageResource(R.drawable.ic_baseline_music_note_24)
            } else{
                fileIcon!!.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
            }

            itemView.setOnClickListener {
                if (file.isDirectory) {
//                    fileList!!.openFolder(file)
                }
            }
        }
    }

}

private fun File?.toTypedArray(): Array<File>? {
    return this?.let { arrayOf(it) }
}
