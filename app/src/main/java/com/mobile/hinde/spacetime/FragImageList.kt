package com.mobile.hinde.spacetime

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.hinde.utils.Constant
import com.mobile.hinde.utils.GridAdapter
import com.mobile.hinde.utils.UserSettings

import java.util.ArrayList

class FragImageList : Fragment() {

    private val dbInstance = FirebaseFirestore.getInstance()

    private var mNumColumn = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)

        mNumColumn = (dm.widthPixels / TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                120f,
                dm
        )).toInt()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_image_list,
                container, false)

        dbInstance.collection("users").document(UserSettings.userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                val userMap = document!!.data

                val root = getView()!!.findViewById<ScrollView>(R.id.scrollLayout)

                // Create LinearLayout
                val ll = LinearLayout(activity)
                ll.orientation = LinearLayout.VERTICAL


                for (target in Constant.TARGET_LIST) {
                    if (userMap!!.containsKey(target) && (userMap[target] as ArrayList<String>).size > 0) {
                        val title = createTextView(target)
                        val line = createLine()

                        val rv = createRecycler(userMap[target] as ArrayList<String>)

                        ll.addView(title)
                        ll.addView(line)
                        ll.addView(rv)
                    }
                }

                root.addView(ll)

            } else {
            }
        }
        return view
    }

    private fun createTextView(target: String): TextView {
        val txt = TextView(activity)

        val font = ResourcesCompat.getFont(activity!!, R.font.digitaldream)
        txt.typeface = font
        txt.textSize = 20f
        txt.text = target
        txt.setTextColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(20, 50, 0, 20)
        txt.layoutParams = params

        return txt
    }

    private fun createLine(): View {
        val v = View(activity)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5)
        v.layoutParams = params
        v.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
        return v
    }

    private fun createRecycler(imgList: ArrayList<String>): RecyclerView {
        val rv = RecyclerView(activity!!)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        params.setMargins(20, 20, 20, 20)
        rv.layoutParams = params

        val customAdapter = GridAdapter(activity!!, imgList)
        rv.adapter = customAdapter // set the Adapter to RecyclerView
        // set a GridLayoutManager with 3 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end

        val gridLayoutManager = GridLayoutManager(activity!!.applicationContext, mNumColumn, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = gridLayoutManager // set LayoutManager to RecyclerView
        return rv
    }
}
