package com.mobile.hinde.spacetime

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.hinde.connection.AsyncResponse
import com.mobile.hinde.connection.LaunchList
import com.mobile.hinde.database.DBHandler
import org.json.JSONArray

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragLaunch.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragLaunch.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragLaunch : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var mDBHandler: DBHandler? = null
    private val dbInstance = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDBHandler = DBHandler(context!!)
    }

    override fun onResume() {
        super.onResume()
        val ver = mDBHandler!!.getLaunchVersion()?.value
        dbInstance.collection("settings").document("launch_version").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newV = task.result!!["ver"] as String
                if(ver != newV){
                    mDBHandler!!.updateLaunchVersion(newV)
                    LaunchList(object : AsyncResponse {
                        override fun processFinish(output: Any) {
                            val resJson = output as JSONArray
                            activity!!.openFileOutput("launch_list.json", Context.MODE_PRIVATE).use {
                                it.write(resJson.toString().toByteArray(Charsets.UTF_8))
                            }

                            updateLaunchList(view!!)
                        }
                    }).execute()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.frag_launch, container, false)

        if(activity!!.fileList().contains("launch_list.json")){
            //DISPLAY CURRENT FILE
            updateLaunchList(view)
        }
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragLaunch.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragLaunch {
            val fragment = FragLaunch()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private fun updateLaunchList(v : View){
        var jsonLaunchList:JSONArray? = null

        activity!!.openFileInput("launch_list.json").use {
            it.bufferedReader(Charsets.UTF_8).use{buf->
                jsonLaunchList = JSONArray(buf.readText())
            }
        }

        if(jsonLaunchList ==  null){
            return
        }

        val rootLayout = v.findViewById<LinearLayout>(R.id.launch_root)
        rootLayout.removeAllViews()

        val fragTransaction = childFragmentManager.beginTransaction()

        for(i in 0..(jsonLaunchList!!.length() - 1 )){

            val currLaunch = jsonLaunchList!!.getJSONObject(i)

            val fragSpaceCraft = FragSpaceCraft()
            val bundleLaunch = Bundle()
            bundleLaunch.putString("rocket", currLaunch.getString("rocket"))
            bundleLaunch.putString("mission", currLaunch.getString("mission"))
            bundleLaunch.putString("date", currLaunch.getString("date"))
            fragSpaceCraft.arguments = bundleLaunch

            fragTransaction.add(R.id.launch_root, fragSpaceCraft, "fragment$i")
        }
        fragTransaction.commit()

    }
}// Required empty public constructor
