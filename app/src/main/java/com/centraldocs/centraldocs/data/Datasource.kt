package com.example.myapplicationyyy.data

import com.example.myapplicationyyy.R
import com.example.myapplicationyyy.model.Topic

class Datasource {
    fun loadTopics(): List<Topic>{

        return listOf(
            Topic(R.string.c, SubTopicData().assignTopic("C#").loadSubTopics()) ,
            Topic(R.string.java, SubTopicData().assignTopic("Java").loadSubTopics()),
            Topic(R.string.kotlin, SubTopicData().assignTopic("Kotlin").loadSubTopics())
        )

    }

}