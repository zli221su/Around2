package edu.syr.around

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ZhiProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var YPlayer: YouTubePlayer? = null
    private val YoutubeDeveloperKey = "123" //getString(R.string.google_api_key)
    private val RECOVERY_DIALOG_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.youtube_fragment_zhi, youTubePlayerFragment as Fragment).commit()

        youTubePlayerFragment.initialize(YoutubeDeveloperKey, object :
            YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                arg0: YouTubePlayer.Provider?,
                youTubePlayer: YouTubePlayer,
                b: Boolean
            ) {
                if (!b) {
                    YPlayer = youTubePlayer
                    YPlayer!!.setFullscreen(false)
                    YPlayer!!.loadVideo("rm1mEx0B_Ro")
                    YPlayer!!.play()
                }
            }

            override fun onInitializationFailure(
                arg0: YouTubePlayer.Provider?,
                arg1: YouTubeInitializationResult?
            ) { // TODO Auto-generated method stub
            }
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zhi_profile, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ZhiProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        @JvmStatic
        fun newInstance() =
            ZhiProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
