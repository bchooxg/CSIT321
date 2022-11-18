package com.example.firstapp

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileListAdapter(files: Array<File>?, fileList: fileList) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var files: Array<File>? = null
    private var fileList: fileList? = null

    init {
        this.files = files
        this.fileList = fileList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return FileViewHolder(parent, fileList)
    }

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
            if (file.isDirectory) {
                fileIcon!!.setImageResource(R.drawable.ic_baseline_folder_24)
            } else {
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
