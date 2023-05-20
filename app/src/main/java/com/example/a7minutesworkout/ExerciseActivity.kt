package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {
    private var binding : ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress: Long = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Long = 0

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        setupRestView()
    }

    // 휴식 뷰 셋
    private fun setupRestView(){
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

        // 운동 프로그래스바 시작
        setExerciseProgressBar()
    }

    // 휴식 프로그래스 시작
    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress.toInt()

        restTimer = object : CountDownTimer(1000, 1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress.toInt()
                binding?.tvtimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                setUpExerciseView();
            }

        }.start()
    }

    // 운동 프로그래스 시작
    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress.toInt()

        exerciseTimer = object : CountDownTimer(3000, 1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress.toInt()
                binding?.tvtimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                if(currentExercisePosition < exerciseList?.size!! - 1){
                    setupRestView()
                }else{
                    Toast.makeText(this@ExerciseActivity, "Congratulations! You have completed the 7 minutes workout.",
                    Toast.LENGTH_SHORT)
                }
            }

        }.start()
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
        // 뷰 바인딩 값 삭제
        binding = null
    }
}