package com.example.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding : ActivityExerciseBinding? = null

    private var restTimerDuration: Long = 1
    private var restTimer: CountDownTimer? = null
    private var restProgress: Long = 0

    private var exerciseTimerDuration: Long = 1
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Long = 0

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener{
            customDialogForBackButton()
        }


        exerciseList = Constants.defaultExerciseList()

        // tts 초기화
        tts = TextToSpeech(this, this)

        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        customDialogForBackButton()

        //super.onBackPressed()
    }
    // 뒤로가기 다이얼로그
    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }

    // 운동화면에서 운동번호 알려주기
    private fun setupExerciseStatusRecyclerView(){
        // xml에서 설정함
        //binding?.rvExerciseStauts?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStauts?.adapter = exerciseAdapter
    }
    // 휴식 뷰 셋
    private fun setupRestView(){
        //
        try{
            //val soundURI = Uri.parse("android.resource://com.example.a7minutesworkout/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, R.raw.press_start)
            player?.isLooping = false
            player?.start()
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }

        // 휴식 프로그래스바, 제목, 다음 운동 표시를 보이게하고 나머지 숨김
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvUpComingExerciseName?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        // 다음 운동 보여주기
        binding?.tvUpComingExerciseName?.text = exerciseList!![currentExercisePosition+1].name

        setRestProgressBar()
    }
    // 운동 뷰 셋
    private fun setUpExerciseView(){
        // 휴식 타이머, 다음 운동 표시 숨김
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvUpComingExerciseName?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE

        // 운동 타이머, 운동 이미지 보이게
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE

        // 현재 운동 모델
        val exerciseModel = exerciseList!![currentExercisePosition]

        // 운동 이미지, 제목 표시
        binding?.ivImage?.setImageResource(exerciseModel.image)
        binding?.tvExerciseName?.text = exerciseModel.name

        // 운동 프로그래스 시작전 초기화
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        // 운동 이름 tts로 읽기
        speakOut(exerciseModel.name)

        // 운동 프로그래스바 시작
        setExerciseProgressBar()
    }

    // 휴식 프로그래스 시작
    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress.toInt()

        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress.toInt()
                binding?.tvtimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].isSelected = true
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseView();
            }

        }.start()
    }

    // 운동 프로그래스 시작
    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress.toInt()

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress.toInt()
                binding?.tvtimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {

                if(currentExercisePosition < exerciseList?.size!! - 1){
                    exerciseList!![currentExercisePosition].isCompleted = true
                    exerciseList!![currentExercisePosition].isSelected = false
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                }else{
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }.start()
    }

    // tts 실행
    private fun speakOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    // 액티비티 사라질때 함수
    override fun onDestroy() {
        super.onDestroy()

        // 휴식 프로그래스 값 삭제
        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        // 운동 프로그래스 값 삭제
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        // tts 값 삭제
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        // player 값 삭제
        if(player != null){
            player!!.stop()
        }
        // 뷰 바인딩 값 삭제
        binding = null
    }

    // tts 성공여부 확인
    override fun onInit(status: Int) {
        if (packageManager.queryIntentServices(Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA), 0).isNotEmpty()) {
            if (status == TextToSpeech.SUCCESS) {
                // set US English as language for tts
                val result = tts!!.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language specified is not supported!")
                }

            } else {
                Log.e("TTS", "Initialization Failed!")
            }
        }else{
            Log.e("TTS", "TTS 기능을 제공하지 않는 기기 입니다.")
        }
    }
}