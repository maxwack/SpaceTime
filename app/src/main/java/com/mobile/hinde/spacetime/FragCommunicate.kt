package com.mobile.hinde.spacetime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.hinde.utils.Constant

import android.content.ContentValues.TAG
import com.mobile.hinde.database.DBHandler
import com.mobile.hinde.service.BroadcastService
import com.mobile.hinde.utils.Tools
import com.mobile.hinde.utils.UserSettings


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragCommunicate.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class FragCommunicate : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private val mFilter = IntentFilter()
    private var mDBHandler: DBHandler? = null

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val target = intent.extras!!.get("target") as String

            if (childFragmentManager.findFragmentByTag(target) != null) {
                val frag = childFragmentManager.findFragmentByTag(target) as FragSendAccept?
                val action = intent.action!!
                val remain = intent.extras!!.getLong("countdown")
                frag!!.updateTickingView(action, remain)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFilter.addAction(BroadcastService.COUNTDOWN_TICK)
        mFilter.addAction(BroadcastService.COUNTDOWN_FINISH)

        mDBHandler = DBHandler(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val myView = inflater.inflate(R.layout.frag_communicate, container, false)

        val dbInstance = FirebaseFirestore.getInstance()
        val docRef = dbInstance.collection("users").document(UserSettings.userId)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    val userMap = document.data

                    UserSettings.money = userMap!!["money"] as Long

                    val moneyCount = activity!!.findViewById<TextView>(R.id.moneyCount)
                    moneyCount.text = Tools.formatMoneyCount(UserSettings.money)

                    val transaction = childFragmentManager.beginTransaction()

                    val acceptSendSunFrag = FragSendAccept()
                    val bundleSun = Bundle()
                    bundleSun.putString("target", Constant.SUN_NAME)
                    acceptSendSunFrag.arguments = bundleSun
                    transaction.replace(R.id.frag_SUN, acceptSendSunFrag, Constant.SUN_NAME)

                    val acceptSendMoonFrag = FragSendAccept()
                    val bundleMoon = Bundle()
                    bundleMoon.putString("target", Constant.MOON_NAME)
                    acceptSendMoonFrag.arguments = bundleMoon
                    transaction.replace(R.id.frag_MOON, acceptSendMoonFrag, Constant.MOON_NAME)

                    if (userMap.containsKey(Constant.VOYAGER1_NAME + "_unlock")) {
                        val acceptSendVoy1Frag = FragSendAccept()
                        val bundleVoy1 = Bundle()
                        bundleVoy1.putString("target", Constant.VOYAGER1_NAME)
                        acceptSendVoy1Frag.arguments = bundleVoy1
                        transaction.replace(R.id.frag_VOYAGER1, acceptSendVoy1Frag, Constant.VOYAGER1_NAME)
                    } else {
                        val unlockVoy1Frag = FragUnlock()
                        val bundleVoy1 = Bundle()
                        bundleVoy1.putString("target", Constant.VOYAGER1_NAME)
                        unlockVoy1Frag.arguments = bundleVoy1
                        transaction.replace(R.id.frag_VOYAGER1, unlockVoy1Frag, Constant.VOYAGER1_NAME)
                    }

                    if (userMap.containsKey(Constant.INSIGHT_NAME + "_unlock")) {
                        val acceptSendInsFrag = FragSendAccept()
                        val bundleIns = Bundle()
                        bundleIns.putString("target", Constant.INSIGHT_NAME)
                        acceptSendInsFrag.arguments = bundleIns
                        transaction.replace(R.id.frag_INSIGHT, acceptSendInsFrag, Constant.INSIGHT_NAME)
                    } else {
                        val unlockInsFrag = FragUnlock()
                        val bundleIns = Bundle()
                        bundleIns.putString("target", Constant.INSIGHT_NAME)
                        unlockInsFrag.arguments = bundleIns
                        transaction.replace(R.id.frag_INSIGHT, unlockInsFrag, Constant.INSIGHT_NAME)
                    }

                    //                        transaction.addToBackStack(null);
                    transaction.commit()
                    childFragmentManager.executePendingTransactions()
                } else {
                    Log.d(TAG, "No such document")
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        }

        return myView
    }

    override fun onPause() {
        super.onPause()
        try {
            context!!.unregisterReceiver(br)
        } catch (e: Exception) {
            //TODO
        }

        Log.i(TAG, "Unregistered broacast receiver")
    }

    override fun onStop() {
        try {
            context!!.unregisterReceiver(br)
        } catch (e: Exception) {
            // Receiver was probably already stopped in onPause()
        }

        super.onStop()
    }

    override fun onDestroy() {
        try {
            context!!.stopService(Intent(context, BroadcastService::class.java))
        } catch (e: Exception) {

        }

        Log.i(TAG, "Stopped service")
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        context!!.registerReceiver(br, mFilter)

        val currTime = System.currentTimeMillis()
        val targetList = mDBHandler!!.searchEndedData(currTime)

        for (target in targetList) {
            val intent = Intent(BroadcastService.COUNTDOWN_FINISH)
            intent.putExtra("target", target)
            context!!.sendBroadcast(intent)
        }
        Log.i(TAG, "Registered broacast receiver")
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
        fun onFragmentInteraction(uri: Uri)
    }
}// Required empty public constructor
