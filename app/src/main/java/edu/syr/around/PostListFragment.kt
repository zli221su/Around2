package edu.syr.around

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_post_list.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PostListFragment : Fragment(),
        PostRecyclerViewAdapter.MyItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    lateinit var myAdapter : PostRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()

        myAdapter =PostRecyclerViewAdapter(activity!!.applicationContext)

        var layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(view?.context)
        rvPostListView.hasFixedSize()
        rvPostListView.layoutManager = layoutManager
        myAdapter.setMyItemClickListener(this)
        rvPostListView.adapter = myAdapter
// default Item Animator
        rvPostListView.itemAnimator?.addDuration = 1000L
        rvPostListView.itemAnimator?.removeDuration = 1000L
        rvPostListView.itemAnimator?.moveDuration = 1000L
        rvPostListView.itemAnimator?.changeDuration = 1000L
// default adpater Animator
        animationAdapter()
// Button Actions!!

        btRefresh.setOnClickListener {
//            myAdapter.notifyDataSetChanged()
            rvPostListView.swapAdapter(myAdapter, true)
            rvPostListView.scrollBy(0, 0)
        }
        rvPostListView.swapAdapter(myAdapter, true)
        rvPostListView.scrollBy(0, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onItemClicked(post : PostData)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun newInstance() =
            PostListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private fun animationAdapter() {
        rvPostListView.adapter = AlphaInAnimationAdapter(myAdapter).apply {
            // Change the durations.
            setDuration(2000)
            setStartPosition(200)
// Disable the first scroll mode.
            setFirstOnly(false)
        }
    }

    override fun onItemClickedFromAdapter(post: PostData) {
        listener?.onItemClicked(post)
    }

    override fun onNotifyPostAddedFromAdapter(post: PostData) {
        NotificationUtils().setNotification(1000, activity as Activity)
    }

}
