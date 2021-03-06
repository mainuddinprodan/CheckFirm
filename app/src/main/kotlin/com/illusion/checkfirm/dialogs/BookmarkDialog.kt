package com.illusion.checkfirm.dialogs

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.illusion.checkfirm.R
import com.illusion.checkfirm.database.BookmarkDB
import com.illusion.checkfirm.database.BookmarkDBHelper
import java.util.*

class BookmarkDialog: BottomSheetDialogFragment() {

    private lateinit var cancelButton: MaterialButton
    private lateinit var saveButton: MaterialButton
    private lateinit var name: TextInputEditText
    private lateinit var model: TextInputEditText
    private lateinit var csc: TextInputEditText
    private lateinit var mDB: BookmarkDBHelper
    private val mBookMarkList = ArrayList<BookmarkDB>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.dialog_bookmark, container, false)

        cancelButton = rootView.findViewById(R.id.cancel)
        saveButton = rootView.findViewById(R.id.save)

        name = rootView.findViewById(R.id.name)
        model = rootView.findViewById(R.id.model)
        csc = rootView.findViewById(R.id.csc)

        mDB = BookmarkDBHelper(context!!)
        mBookMarkList.addAll(mDB.allBookmarkDB)

        val modelFilters = model.filters
        val newModelFilters = arrayOfNulls<InputFilter>(modelFilters.size + 1)
        System.arraycopy(modelFilters, 0, newModelFilters, 0, modelFilters.size)
        newModelFilters[modelFilters.size] = InputFilter.AllCaps()
        model.filters = newModelFilters

        val cscFilters = csc.filters
        val newCscFilters2 = arrayOfNulls<InputFilter>(cscFilters.size + 1)
        System.arraycopy(cscFilters, 0, newCscFilters2, 0, cscFilters.size)
        newCscFilters2[cscFilters.size] = InputFilter.AllCaps()
        csc.filters = newCscFilters2

        val shouldUpdate = arguments!!.getBoolean("shouldUpdate")
        val bundleName = arguments!!.getString("name")
        val bundleDevice = arguments!!.getString("device")
        val bundleCSC = arguments!!.getString("csc")
        val position = arguments!!.getInt("position")

        if (shouldUpdate && bundleName != "") {
            name.setText(bundleName)
        }

        if (shouldUpdate && bundleDevice != "") {
            model.setText(bundleDevice)
        }

        if (shouldUpdate && bundleCSC != "") {
            csc.setText(bundleCSC)
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        saveButton.setOnClickListener {
            val name = name.text.toString()
            val model = model.text.toString()
            val csc = csc.text.toString()

            if (shouldUpdate) {
                updateBookMark(name, model, csc, position)
            } else {
                createBookMark(name, model, csc)
            }
            dismiss()
        }

        return rootView
    }

    private fun createBookMark(name: String, model: String, csc: String) {
        val id = mDB.insertBookMark(name, model, csc)

        val n = mDB.getBookMark(id)
        mBookMarkList.add(n)
    }

    private fun updateBookMark(name: String, model: String, csc: String, position: Int) {
        val b = mBookMarkList[position]
        b.name = name
        b.model = model
        b.csc = csc

        mDB.updateBookMark(b)

        mBookMarkList[position] = b
    }

    companion object {
        fun newInstance(shouldUpdate: Boolean, name: String, device: String, csc: String, position: Int): BookmarkDialog {
            val f = BookmarkDialog()

            val args = Bundle()
            args.putBoolean("shouldUpdate", shouldUpdate)
            args.putString("name", name)
            args.putString("device", device)
            args.putString("csc", csc)
            args.putInt("position", position)
            f.arguments = args

            return f
        }
    }
}