package com.example.myapplicationyyy.model

import androidx.annotation.StringRes

data class Topic (@StringRes val stringID: Int, val subtopic_list: List<Subtopic>){
}